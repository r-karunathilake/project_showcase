########################################################################################################################
############################                        Imports                                #############################
########################################################################################################################
from scipy.interpolate import make_interp_spline, CubicSpline, PchipInterpolator
from typing import Union, Tuple, List
from collections import OrderedDict
from scipy.constants import c, pi
from gen_logger import Logger
from pathlib import Path

import importlib.util
import numpy as np
import pickle
import time
import os
import sys

# Note: following paths are default Lumerical installation locations, change accordingly
# Load the Lumerical API
os.add_dll_directory(r'C:\Program Files\Lumerical\v222\api\python')
spec_win = importlib.util.spec_from_file_location("lumapi", r"C:\Program Files\Lumerical\v222\api\python\lumapi.py")
lumapi = importlib.util.module_from_spec(spec_win)
spec_win.loader.exec_module(lumapi)

# Add module to system path for proper pickling in multiprocessing 
sys.modules["lumapi"] = lumapi

# Change the logging level for more information
#   - DEBUG
#   - INFO
#   - WARNING
#   - ERROR
#   - CRITICAL
__LOG__ = Logger(__name__, "INFO", Path.cwd() / "Python_Code/Lumerical_RCWA_PSO").get_logger()
__UNITS__ = {"nano": 1e-9, "micro": 1e-6, "femto": 1e-15}
__TARGET_WAVE__ = [1.55 * __UNITS__["micro"], 1.55 * __UNITS__["micro"]]
__ALPHA__ = 0.8 # Geometry transparency level
__MAT_FIT_RANGE__ = [1.4 * __UNITS__["micro"], 1.6 * __UNITS__["micro"]]
__MAT_NAMES__ = {"sub": "SiO2 (Glass) - Palik Custom Fit",
                 "a-pcm": "a-SbSe",
                 "c-pcm": "c-SbSe",
                 "post": "a-Si_Nanofab"}
__GEOMETRY_SYMMETRY_ANGLE_RANGE__ = pi/2

# Second value of the tuple is color (RGB)
__STATIC_MAT_NAMES__ = {"sub": ("n,k SiO2", np.array([0.8667, 0.8667, 0.8667, 1])), # gray
                        "a-pcm": ("n,k a-SbSe", np.array([0, 1, 0, 1])), # green
                        "c-pcm": ("n,k c-SbSe", np.array([1, 1, 0, 1])), # yellow
                        "post": ("n,k a-Si_Nanofab", np.array([1, 0 , 0, 1]))} # red

# Define the simulation parameters 
__PERIOD__ = 650 * __UNITS__["nano"]
__MAX_PERIOD__ = __TARGET_WAVE__[0] * 0.9
__MIN_PERIOD__ = 250 * __UNITS__["nano"]
__Z_SPAN__ = 4 * __UNITS__["micro"]
__ALLOW_SYM__ = True
__SIM_TYPE__ = "3D"
__PML_PROF__ = 3  # Steep angle
__BACK_INDEX__ = 1
__NUM_FREQ_POINTS__ = 100
__MESH_REFINEMENT__ = "precise volume average"
__MESH_REFINE_PARAM__ = 5
__MESH_ACCU__ = 4
__SIM_TIME__ = 10000 * __UNITS__["femto"] # fs

# Define geometry parameters 
__H__ = 300 * __UNITS__["nano"] # Total post height
__PCM_H__ = 100 * __UNITS__["nano"] # PCM layer thickness
__SI_H__ = __H__ - 2 * __PCM_H__ # Si layer thickness bi-layer 
__LAYER_POS__ = [0, __PCM_H__, __PCM_H__ + __SI_H__, __H__] # Layer interface positions 
__SUB_H__ = 3 * __UNITS__["micro"]
__INI_R__ = 275 * __UNITS__["nano"] # Initial radius of the nanodisk
__X_OVERRIDE__ = 10 * __UNITS__["nano"]
__Y_OVERRIDE__ = 10 * __UNITS__["nano"]
__Z_SI_OVERRIDE__ = 20 * __UNITS__["nano"]
__Z_PCM_OVERRIDE__ = 5 * __UNITS__["nano"]

#======== Optimization constraint parameters====================
__STRUCT_BUFFER__ = 50 * __UNITS__["nano"]
__MAX_R__ = (__MAX_PERIOD__/2) - __STRUCT_BUFFER__
__MIN_R__ = 100 * __UNITS__["nano"]

# Nanodisk height
__MAX_H__ = 850 * __UNITS__["nano"]
__MIN_H__ = 250 * __UNITS__["nano"]

# PCM layer thickness
__MAX_PCM_H__ = 100 * __UNITS__["nano"]
__MIN_PCM_H__ = 50 * __UNITS__["nano"] 

# RCWA optional parameters 
__OPTIONS__ = {"max_N": 100, 
               "report_amplitudes": True}

# RCWA geoemetry parameters 
__LAYER_POS__ = [0, __PCM_H__, __PCM_H__ + __SI_H__, __H__] # Layer interface positions 
__GEOMETRY__ = {"injection_axis": "z-axis",
                "x_min": -1 * __PERIOD__/2,
                "x_max": __PERIOD__/2,
                "y_min": -1 * __PERIOD__/2,
                "y_max": __PERIOD__/2,
                "z_min": -1 * __Z_SPAN__/2,
                "z_max": __Z_SPAN__/2,
                "layer_positions": np.array(__LAYER_POS__)}

# Define the RCWA source parameters
__SOURCE_PHI__ = 0 # In degrees
__SOURCE_THETA__ = 0 # In degrees
__SOURCE_DIR__ = "forward"
__SOURCE_P_POL__ = 1 
__SOURCE_PROP_AXIS__ = "z-axis"
__EXCITATION__ = {"f": c/__TARGET_WAVE__[0],
                  "phi": __SOURCE_PHI__,
                  "theta": __SOURCE_THETA__,
                  "p_pol": __SOURCE_P_POL__,
                  "direction": "forward"}

# Structure names 
__TOP_STRUCT_NAME__ = "top_pcm"
__MID_STRUCT_NAME__ = "si_layer"
__BOT_STRUCT_NAME__ = "bottom_pcm"
__STRUCT_GROUP__ = "nanodisk"
__SCOPE_RESET__ = "::model"
__MESH_GROUP__ = "mesh_group"
__TOP_MESH_NAME__ = "top_pcm"
__MID_MESH_NAME__ = "middle_si"
__BOT_MESH_NAME__ = "bottom_pcm"
__SUB_NAME__ = "substrate"

# Define S parameter analysis group configuration
__S_OBJ_NAME__ = "s_param"
__S_OBJ_POL__ = 0 # in degrees

# Optimization parameters 
__NUM_ANCHOR_VERTICES__ = {"symmetry": 10, "default": 25, "nanodisk": 100}
__INTERP_RES__ = 200

class FDTDSim(object):
    def __str__(self) -> str:
        """Return the name of this class"""
        return self.name
    
    def __init__(self, save_path: Path, sim_name: Path, open_gui: bool=True,
                 c4_symmetry: bool=False, nanodisk_optimization: bool=False) -> None:

        """Initialize the attributes of this class"""
        self.sim_path = save_path / sim_name
        self.pickle_path = self.sim_path.parent / "final_pso_geo_params.pkl"
        # Generate the Lumerical API object
        self.fdtd = lumapi.FDTD(hide=not open_gui)

        self.symmetry_enabled = c4_symmetry
        self.opt_nanodisk = nanodisk_optimization

        # Name for the vertices
        if not self.symmetry_enabled and not self.opt_nanodisk:
            self.num_vertices = __NUM_ANCHOR_VERTICES__["default"]
        elif self.symmetry_enabled and not self.opt_nanodisk:
            self.num_vertices = __NUM_ANCHOR_VERTICES__["symmetry"]
        else:
            self.num_vertices = __NUM_ANCHOR_VERTICES__["nanodisk"]
            
        self.vertices_names = ["V" + str(num) for num in range(1, self.num_vertices + 1)]

        # Generate the static n,k materials used in the simulation
        self._gen_static_mat()

        # Extract the simulation name from the Path object 
        self.name = sim_name.stem

        # RCWA simulation parameters 
        self.rcwa_excitation = __EXCITATION__
        self.rcwa_options = __OPTIONS__
        self.rcwa_geometry = __GEOMETRY__

        # Note: 'self._anchor_vertices' will change after each iteration of the PSO algorithm 
        self.ini_interp_vertices, self._anchor_vertices = self._gen_initial_nanodisk_vertices()
        
        __LOG__.debug("=" * 80)
        __LOG__.debug("Initial anchor vertices in polar coordinates: ")
        for idx, v in enumerate(self._anchor_vertices):
            __LOG__.debug(f"V{idx+1}: {self._convert_vertex_units(v)}")
        __LOG__.debug("=" * 80)
        # __LOG__.debug("Initial interpolated X, Y Lumerical vertices: ")
        # for idx, v in enumerate(self.ini_interp_vertices):
        #     __LOG__.debug(f"V{idx+1}: {v[0]/1e-9} nm, {v[1]/1e-9} nm")
        # __LOG__.debug("=" * 80)

        # Get the initial positions for the PSO optimizer 
        self._initial_pso_positions = self.get_initial_position()

         # Update the anchor vertices with a dictionary of vertices with names 
        self.orig_anchor_vertices = OrderedDict(map(lambda name, vertex: (name, vertex), self.vertices_names, self._anchor_vertices))
        
        # This attribute will be updated as the structure is updated
        self._anchor_vertices = OrderedDict(map(lambda name, vertex: (name, vertex), self.vertices_names, self._anchor_vertices))

        # Calculate the radial and angle constraints for all the initial anchor vertices
        self._anchor_constraints = self.eval_parameter_constraints()

        # Add FDTD simulation region
        __LOG__.info("Generating Lumerical FDTD simulation model...")
        self._gen_fdtd_model()
        time.sleep(1)

    def get_initial_position(self) -> np.array:
        """This function will return the initial positions for the PSO
        algorithm. The initial positions represent a circular nanodisk."""
        
        # Extract the initial position for PSO into a list 
        ini_pos = [val for vertex in self._anchor_vertices for val in vertex]
        if not self.symmetry_enabled:
            ini_pos = ini_pos[:-2] # First vertex is same as last 

        # Need to add the inital layer parameters 
        return np.array([ini_pos + [__PCM_H__, __PCM_H__, __H__, __PERIOD__]])

    def _gen_static_mat(self) -> None:
        """This function will generate custom n,k material for each layer in the nanodisk structure"""
        for layer_name, n_k_mat_info in __STATIC_MAT_NAMES__.items():
            __LOG__.debug(f"Layer: '{layer_name}', Real Material: '{__MAT_NAMES__[layer_name]}'")
            n_k_mat = self.fdtd.addmaterial("(n,k) Material")
            index = np.squeeze(self._get_n_k(__MAT_NAMES__[layer_name], c/__TARGET_WAVE__[0]))
            n_real = float(np.real(index))
            n_imag = float(np.imag(index))
            __LOG__.debug(f"Index for {layer_name}: n - {n_real}, k - {n_imag}")

            properties = {"name": n_k_mat_info[0],
                          "color": n_k_mat_info[1],
                          "Refractive Index": n_real,
                          "Imaginary Refractive Index": n_imag}
            self.fdtd.setmaterial(n_k_mat, properties)

    def _get_n_k(self, mat_name: str, freq: float, fit_f_range: list=__MAT_FIT_RANGE__) -> Tuple[float, float]:
        """ This function will update the correct n,k material for the given frequency"""
        __LOG__.debug(f"Getting index data from {mat_name} at {(c/freq)*1e9} nm in fit range {fit_f_range[0]/__UNITS__['nano']} nm to {fit_f_range[1]/__UNITS__['nano']} nm")
        return self.fdtd.getfdtdindex(mat_name, freq, c/fit_f_range[0], c/fit_f_range[1])

    def _update_n_k_mat(self, new_freq: float) -> None:
        """This function will update the n,k values for all material used in the simulation to the index 
        at 'new_freq'"""
        for layer_name, n_k_mat in __STATIC_MAT_NAMES__.items():
            index= np.squeeze(self._get_n_k(__MAT_NAMES__[layer_name], new_freq))
            properties = {"Refractive Index": float(np.real(index)),
                          "Imaginary Refractive Index": float(np.imag(index))}
            self.fdtd.setmaterial(n_k_mat[0], properties)

    def _gen_initial_nanodisk_vertices(self) -> Tuple[np.array, np.array]:
        """This function generates the vertices for the nanodisk structure
        based on the given angle 'phi' and 'radius'.
        """
        if self.symmetry_enabled:
            theta = np.linspace(0, __GEOMETRY_SYMMETRY_ANGLE_RANGE__, self.num_vertices)
            # Keep the original anchors in polar coordinates for future 
            v_polar = np.c_[np.linspace(__INI_R__, __INI_R__,  self.num_vertices), theta] 
        else:
            theta = np.linspace(0, 2*pi,  self.num_vertices); 
            # Keep the original anchors in polar coordinates for future 
            v_polar = np.c_[np.linspace(__INI_R__, __INI_R__,  self.num_vertices), theta]
        
        v_complex = __INI_R__ * np.exp(1j * theta)

        # Vertex coordinates for Lumerical based on complex numbers 
        v = np.column_stack((np.real(v_complex), np.imag(v_complex)))

        if self.symmetry_enabled:
            cs = CubicSpline(theta, v, bc_type = "not-a-knot")
            v_interpolated = cs(np.linspace(0, __GEOMETRY_SYMMETRY_ANGLE_RANGE__, __INTERP_RES__))
        else:
            cs = CubicSpline(theta, v, bc_type = "periodic")
            v_interpolated = cs(np.linspace(0, 2*pi, __INTERP_RES__))

        return v_interpolated, v_polar

    @staticmethod
    def _convert_vertex_units(array: np.array) -> str:
        # Radius in m convert to nm
        return f"[{array[0]/1e-9} nm, {array[1]*(180/pi)} degrees]"
    
    @staticmethod
    def _polar_vertices_to_cartesian(radius: np.array, theta: np.array) -> np.array:
        v_complex = radius * np.exp(1j * theta)
        return np.column_stack([np.real(v_complex), np.imag(v_complex)])
    
    def _calculate_full_param_constraints(self) -> Tuple: 
        """This function calculates the contraints for full 360 degress when geometry
        symmetry is NOT enabled"""
        
        if self.opt_nanodisk:
            # 1 (radius) + 4 (top pcm thickness, bottom pcm thickness, total height, period)
            min = [-1] * (1 + 4)
            max = [-1] * (1 + 4)

            # Constraints for radius 
            # No way to deal with coupled parameters constraints cleanly since "pyswarm" library does not support that... 
            min[0] = __MIN_R__ # This will result in nanodisk with diameter > period sometimes. 
            max[0] = __MAX_R__

        else:
            # Last 5 indices are for the nanodisk parameters (pcm thickness x 2, nanodisk height)
            min = [-1] * ((self.num_vertices * 2 - 2)  + 4) # Subtract 2 parameters b/c end vertex == first vertex
            max = [-1] * (( self.num_vertices * 2 - 2)  + 4)

            # Calculate the constraints for the vertices
            for i in range(1,  self.num_vertices + 1):
                # last vertex is always equal to the first vertex
                if i ==  self.num_vertices:
                    continue

                if i == 1: 
                    # Minimum constraints
                    min[i-1] = __MIN_R__
                    min[i] = 0

                    # Maximum constraints 
                    max[i-1] = __MAX_R__
                    max[i] = 0

                    continue 
                
                min_theta = self.orig_anchor_vertices[f"V{i-1}"][1]
                max_theta = self.orig_anchor_vertices[f"V{i}"][1]

                # Minimum constraints
                min[2*(i-1)] = __MIN_R__
                min[(2*i)-1] = min_theta 

                # Maximum constraints 
                max[2*(i-1)] = __MAX_R__
                max[(2*i)-1] = max_theta 
            
        # Calculate the constraints for the nanodisk layers
        # Order of in which constraints are added: 
        # [top pcm thickness, bot pcm thickness, nanodisk height, period]
        max[-1] = __MAX_PERIOD__ # Maximum period based on the wavelength
        min[-1] = __MIN_PERIOD__ # Minimum period based on the wavelength 

        max[-2] = __MAX_H__ # Maximum nanodisk height
        min[-2] = __MIN_H__ # Minimum nanodisk height

        max[-3] = __MAX_PCM_H__ # Maximum bot pcm thickness
        min[-3] = __MIN_PCM_H__  # Minimum bot pcm thickness 

        max[-4] = __MAX_PCM_H__ # Maximum top pcm thickness
        min[-4] = __MIN_PCM_H__  # Minimum top pcm thickness  

        # Constraints for radius and angle in m and radians, respectively 
        return (np.array(min), np.array(max))
    
    def _calculate_restricted_param_constraints(self) -> Tuple: 
        """Calculate parameter constraints only for a restricted range of 
        angles defined by '__GEOMETRY_SYMMETRY_ANGLE_RANGE__'. This function is 
        valid only when symmetry condition is enabled."""
        if self.opt_nanodisk:
            # 1 (radius) + 4 (top pcm thickness, bottom pcm thickness, total height, period)
            min = [-1] * (1 + 4)
            max = [-1] * (1 + 4)

            # Constraints for radius 
            # No way to deal with coupled parameters constraints cleanly since "pyswarm" library does not support that... 
            min[0] = __MIN_R__ # This will result in nanodisk with diameter > period sometimes. 
            max[0] = __MAX_R__

        else:
            # Last 5 indices are for the nanodisk parameters (pcm thickness x 2, nanodisk height, period)
            min = [-1] * ((self.num_vertices * 2)  + 4) # -2 is NOT present b/c end vertex != start vertex when symmetry is enabled 
            max = [-1] * ((self.num_vertices * 2)  + 4)

            # Calculate the constraints for the vertices
            for i in range(1,  self.num_vertices + 1):
                # First anchor vertex always has 0 radians angle 
                if i == 1: 
                    # Minimum constraints
                    min[i-1] = __MIN_R__
                    min[i] = 0

                    # Maximum constraints 
                    max[i-1] = __MAX_R__
                    max[i] = 0

                    continue # Go to next loop iteration 

                # Last anchor vertex always has '__GEOMETRY_SYMMETRY_ANGLE_RANGE__' radians angle 
                if i ==  self.num_vertices: 
                    # Minimum constraints
                    min[2*(i-1)] = __MIN_R__
                    min[(2*i)-1] = __GEOMETRY_SYMMETRY_ANGLE_RANGE__  

                    # Maximum constraints 
                    max[2*(i-1)] = __MAX_R__
                    max[(2*i)-1] = __GEOMETRY_SYMMETRY_ANGLE_RANGE__ 

                    continue # Go to next loop iteration 

                min_theta = self.orig_anchor_vertices[f"V{i-1}"][1]
                max_theta = self.orig_anchor_vertices[f"V{i}"][1]

                # Minimum constraints
                min[2*(i-1)] = __MIN_R__
                min[(2*i)-1] = min_theta 

                # Maximum constraints 
                max[2*(i-1)] = __MAX_R__
                max[(2*i)-1] = max_theta 

        # Calculate the constraints for the nanodisk layers
        # Order of in which constraints are added: 
        # [top pcm thickness, bot pcm thickness, nanodisk height, period]
        max[-1] = __MAX_PERIOD__ # Maximum period based on the wavelength
        min[-1] = __MIN_PERIOD__ # Minimum period based on the wavelength 

        max[-2] = __MAX_H__ # Maximum nanodisk height
        min[-2] = __MIN_H__ # Minimum nanodisk height

        max[-3] = __MAX_PCM_H__ # Maximum bot pcm thickness
        min[-3] = __MIN_PCM_H__  # Minimum bot pcm thickness 

        max[-4] = __MAX_PCM_H__ # Maximum top pcm thickness
        min[-4] = __MIN_PCM_H__  # Minimum top pcm thickness 

        # Constraints for radius and angle in m and radians, respectively 
        return (np.array(min), np.array(max))
    
    def eval_parameter_constraints(self) -> Tuple:
        """This function calculates the constraints on radius and 
        angle for each anchor vertex along with contraints nanodisk 
        layer dimensions (pcm thickness, overall height) and
        create a constraints tuple for the specification of the Python PSO
        library ('pyswarms')

        Note: no need to calculate the constraint for the end anchor vertex because
              it is the same as the first.  
        """
        if not self.symmetry_enabled:
            return self._calculate_full_param_constraints()
        else:
            return self._calculate_restricted_param_constraints() 

    def _add_fdtd(self) -> None:
        """Add FDTD simulation region to FDTD"""
        if not self.symmetry_enabled:
            FDTD_PARAM = {"dimension": "3D",
                        "x": 0,
                        "y": 0,
                        "z": 0,
                        "x span": __PERIOD__,
                        "y span": __PERIOD__,
                        "z span": __Z_SPAN__,
                        "allow symmetry on all boundaries": __ALLOW_SYM__,
                        "x min bc": "Periodic",
                        "x max bc": "Periodic",
                        "y min bc": "Periodic",
                        "y max bc": "Periodic",
                        "pml profile": __PML_PROF__,
                        "mesh refinement": __MESH_REFINEMENT__,
                        "meshing refinement": __MESH_REFINE_PARAM__,
                        "mesh accuracy": __MESH_ACCU__,
                        "index": __BACK_INDEX__,
                        "simulation time": __SIM_TIME__
                        }
        else:
            FDTD_PARAM = {"dimension": "3D",
                        "x": 0,
                        "y": 0,
                        "z": 0,
                        "x span": __PERIOD__,
                        "y span": __PERIOD__,
                        "z span": __Z_SPAN__,
                        "allow symmetry on all boundaries": __ALLOW_SYM__,
                        "x min bc": "Anti-Symmetric",
                        "x max bc": "Anti-Symmetric",
                        "y min bc": "Symmetric",
                        "y max bc": "Symmetric",
                        "pml profile": __PML_PROF__,
                        "mesh refinement": __MESH_REFINEMENT__,
                        "meshing refinement": __MESH_REFINE_PARAM__,
                        "mesh accuracy": __MESH_ACCU__,
                        "index": __BACK_INDEX__,
                        "simulation time": __SIM_TIME__
                        }
        self.fdtd.addfdtd(FDTD_PARAM)

    def _add_nanodisk_struct(self) -> None:
        """Add nanodisk structure model to Lumerical FDTD region"""
        if self.symmetry_enabled:
            second_quadrant_vertices = self.ini_interp_vertices.copy() # Vertices in the first quadrant of a cartesian coordinate system 
            second_quadrant_vertices[:, 0] *= -1 # Turn all x-values negative 

            third_quadrant_vertices = self.ini_interp_vertices.copy()
            third_quadrant_vertices[:, :] *= -1 # Turn all x-values and y-values negative 

            fourth_quadrant_vertices = self.ini_interp_vertices.copy()  
            fourth_quadrant_vertices[:, 1] *= -1 # Turn all y-values negative 

            # Assemble all the vertices going CCW
            initial_xy_verices = np.concatenate((self.ini_interp_vertices, second_quadrant_vertices[::-1], third_quadrant_vertices[1:], fourth_quadrant_vertices[::-1]), axis=0) 
        else:
            initial_xy_verices = self.ini_interp_vertices

        self.fdtd.addstructuregroup({"name": __STRUCT_GROUP__})
        # Si structure 
        si_params = {"name": __MID_STRUCT_NAME__,
                    "x": self.fdtd.getnamed("FDTD", "x"),
                    "y": self.fdtd.getnamed("FDTD", "y"),
                    "z": __H__/2,
                    "z span": __SI_H__,
                    "material": __STATIC_MAT_NAMES__["post"][0],
                    "vertices": initial_xy_verices
                    }
        self.fdtd.addpoly(si_params)
        self.fdtd.addtogroup(__STRUCT_GROUP__)

        # Add top PCM structure 
        top_pcm_param = {"name": __TOP_STRUCT_NAME__,
                        "x": self.fdtd.getnamed("FDTD", "x"),
                        "y": self.fdtd.getnamed("FDTD", "y"),
                        "z": __H__ - (__PCM_H__/2),
                        "z span": __PCM_H__,
                        "material": __STATIC_MAT_NAMES__["a-pcm"][0],
                        "vertices": initial_xy_verices
                        }
        self.fdtd.addpoly(top_pcm_param)
        self.fdtd.addtogroup(__STRUCT_GROUP__)

        # Add bottom PCM structure
        bottom_pcm_param = {"name": __BOT_STRUCT_NAME__,
                            "x": self.fdtd.getnamed("FDTD", "x"),
                            "y": self.fdtd.getnamed("FDTD", "y"),
                            "z": __PCM_H__/2,
                            "z span": __PCM_H__,
                            "material": __STATIC_MAT_NAMES__["a-pcm"][0],
                            "vertices": initial_xy_verices
                            }
        self.fdtd.addpoly(bottom_pcm_param)
        self.fdtd.addtogroup(__STRUCT_GROUP__)

    def _add_s_param_analysis(self) -> None:
        """Add the S parameter analysis group to the FDTD
        simulation model"""

        # Add predefined analysis object from the Lumerical library 
        self.fdtd.addobject("grating_s_params")
        self.fdtd.set({"name":__S_OBJ_NAME__})

        PARAM = {"metamaterial span": __H__,
                 "metamaterial center": __H__/2,
                 "start wavelength": __TARGET_WAVE__[0],
                 "stop wavelength": __TARGET_WAVE__[-1],
                 "polarization angle": __S_OBJ_POL__,
                 "angle theta": __SOURCE_THETA__,
                 "angle phi": __SOURCE_PHI__,
                 "propagation axis": __SOURCE_PROP_AXIS__.split("-")[0],
                 "x": self.fdtd.getnamed("FDTD", "x"),
                 "y": self.fdtd.getnamed("FDTD", "y"),
                 "z": self.fdtd.getnamed("FDTD", "z"),
                 "x span": 3 * __PERIOD__,
                 "y span": 3 * __PERIOD__,
                 "z span": 2.5 * __UNITS__["micro"]}
        self.fdtd.setnamed(__S_OBJ_NAME__, PARAM)
        
    def _add_mesh_group(self) -> None:
        """Add meshing override regions to PCM and 
        Si layers of the nanodisk"""
        self.fdtd.addgroup({"name": __MESH_GROUP__})

        # Top PCM mesh  
        TOP_MESH_PARAMS = {"name": __TOP_MESH_NAME__,
                           "override x mesh": True,
                           "override y mesh": True,
                           "override z mesh": True,
                           "based on a structure": True,
                           "structure": __TOP_STRUCT_NAME__,
                           "dx": __X_OVERRIDE__,
                           "dy": __Y_OVERRIDE__,
                           "dz": __Z_PCM_OVERRIDE__
                           }
        self.fdtd.addmesh(TOP_MESH_PARAMS)
        self.fdtd.addtogroup(__MESH_GROUP__)

        # Middle Si mesh
        MID_MESH_PARAMS = {"name": __MID_MESH_NAME__,
                           "override x mesh": True,
                           "override y mesh": True,
                           "override z mesh": True,
                           "based on a structure": True,
                           "structure": __MID_STRUCT_NAME__,
                           "dx": __X_OVERRIDE__,
                           "dy": __Y_OVERRIDE__,
                           "dz": __Z_SI_OVERRIDE__
                           }
        self.fdtd.addmesh(MID_MESH_PARAMS)
        self.fdtd.addtogroup(__MESH_GROUP__)

        # Bottom PCM mesh
        BOT_MESH_PARAMS = {"name": __BOT_MESH_NAME__,
                           "override x mesh": True,
                           "override y mesh": True,
                           "override z mesh": True,
                           "based on a structure": True,
                           "structure": __BOT_STRUCT_NAME__,
                           "dx": __X_OVERRIDE__,
                           "dy": __Y_OVERRIDE__,
                           "dz": __Z_PCM_OVERRIDE__
                           }
        self.fdtd.addmesh(BOT_MESH_PARAMS)
        self.fdtd.addtogroup(__MESH_GROUP__)

    def _add_substrate_struct(self) -> None:
        """Add a substrate to the simulation model"""
        # Add substrate
        SUB_PARAMS = {"name": __SUB_NAME__,
                      "x": self.fdtd.getnamed("FDTD", "x"),
                      "y": self.fdtd.getnamed("FDTD", "y"),
                      "z max": self.fdtd.getnamed("FDTD", "z"),
                      "x span": self.fdtd.getnamed("FDTD", "x span") + __TARGET_WAVE__[-1],
                      "y span": self.fdtd.getnamed("FDTD", "y span") + __TARGET_WAVE__[-1],
                      "z span":  __SUB_H__ + 3 * __TARGET_WAVE__[-1],
                      "material": __STATIC_MAT_NAMES__["sub"][0],
                      "alpha": __ALPHA__
                      }
        self.fdtd.addrect(SUB_PARAMS)

    def _gen_fdtd_model(self) -> None:
        """This function will generate the RCWA model in FDTD
        given the global parameters defined above."""

        self.fdtd.newproject("current")
        # Define global monitor and source parameters 
        self.fdtd.setglobalsource("wavelength start", __TARGET_WAVE__[0])
        self.fdtd.setglobalsource("wavelength stop", __TARGET_WAVE__[-1])
        self.fdtd.setglobalmonitor("frequency points", __NUM_FREQ_POINTS__)

        self._add_fdtd()
        self._add_nanodisk_struct()
        self._add_s_param_analysis()
        self._add_mesh_group()
        self._add_substrate_struct() 

    def _interp_new_xy_vertices(self) -> np.array:
        """Generate interpolated XY vertices for nanodisk polygon given 
        new anchor vertices in polar coordinates."""
        # Create a interpolating curve parametrized by theta
        __LOG__.debug(f"New anchor vertices in polar coordinates: ")
        for key, item in self._anchor_vertices.items():    
            __LOG__.debug(f"{key}: {self._convert_vertex_units(item)}")
        
        vertices_data =  list(self._anchor_vertices.values())
        r, angle = map(np.array, zip(*vertices_data))

        # Iterpolate the vertices for smaller angle steps
        if not self.symmetry_enabled:
            old_angle = np.linspace(0, 2*pi,  self.num_vertices)
            spl = make_interp_spline(old_angle, self._polar_vertices_to_cartesian(r, angle), bc_type="periodic")

            theta_new = np.linspace(0, 2*pi, __INTERP_RES__)
            x, y = spl(theta_new).T

            # Build a proper vector containing the vertices for Lumerical
            v_interpolated = np.c_[x, y]
        
        else: 
            old_angle = np.linspace(0, __GEOMETRY_SYMMETRY_ANGLE_RANGE__, self.num_vertices)
            # Monotonic cubic interpolation for when symmetry is requested to ensure positive values
            #  in the the first cartersian quadrant 
            spl = make_interp_spline(old_angle, self._polar_vertices_to_cartesian(r, angle), bc_type="not-a-knot")

            theta_new = np.linspace(0, __GEOMETRY_SYMMETRY_ANGLE_RANGE__, __INTERP_RES__)
            v_interpolated = spl(theta_new) # This type of interpolation can give negative points when values are close to zero 
            v_interpolated[v_interpolated<0] = 0

            # Build full XY vertice list using symmetry conditions
            second_quadrant_vertices = v_interpolated.copy() # Vertices in the first quadrant of a cartesian coordinate system
            # Flip the order of the points in the second quadrant since Lumerical draws polygons in a CCW fashion
            second_quadrant_vertices = second_quadrant_vertices[::-1]   
            second_quadrant_vertices[:, 0] *= -1 # Turn all x-values negative 

            third_quadrant_vertices = v_interpolated.copy()
            # Eliminate duplicate points going from second quadrant to third quadrant when drawing the polygon 
            third_quadrant_vertices = third_quadrant_vertices[1:]
            third_quadrant_vertices[:, :] *= -1 # Turn all x-values and y-values negative 

            fourth_quadrant_vertices = v_interpolated.copy() 
            # Flip the order of the points in the second quadrant since Lumerical draws polygons in a CCW fashion   
            fourth_quadrant_vertices = fourth_quadrant_vertices[::-1] 
            fourth_quadrant_vertices[:, 1] *= -1 # Turn all y-values negative 

            # Assemble all the vertices going CCW
            v_interpolated = np.concatenate((v_interpolated, second_quadrant_vertices, third_quadrant_vertices, fourth_quadrant_vertices), axis=0) 


        # __LOG__.debug("=" * 80)
        # __LOG__.debug("Interpolated points in X, Y coordinates: ")
        # for row in range(v_interpolated.shape[0]):
        #     x, y = v_interpolated[row, :]
        #     __LOG__.debug(f"V{row+1}: {x/1e-9} nm, {y/1e-9} nm")
        # __LOG__.debug("=" * 80)
    
        return np.squeeze(v_interpolated)

    @property
    def anchor_vertices(self) -> dict:
        """This function will return the current 
        anchor vertices for polygon geometry"""

        return self._anchor_vertices 
    
    @property
    def anchor_constraints(self) -> dict:
        """This function will return the radius and angle
        constraints of the anchor vertices for the polygon
        geometry"""

        return self._anchor_constraints 

    @property
    def initial_pso_pos(self) -> np.array:
        """Return the initial PSO positions (anchor vertices and layer thicknesses) in a
        numpy array"""
        
        return self._initial_pso_positions

    def run_rcwa(self) -> dict:
        """This functions runs the RCWA simulation of this simulation model
        with initially generated attributes('self.rcwa_excitation',
        'self.rcwa_options', 'self.rcwa_geometry') and returns the results"""

        return self.fdtd.rcwa(self.rcwa_geometry, self.rcwa_excitation, self.rcwa_options)
    
    def pcm_mat_update(self, pcm_name: str = "n,k c-SbSe") -> None:
        """This function will update the PCM materials in the 
        nanodisk structure to 'pcm_name'.
        """
        self.fdtd.groupscope(__STRUCT_GROUP__)
        __LOG__.debug(f"Changing selected structure material to {pcm_name}.")
        self.fdtd.setnamed(__TOP_STRUCT_NAME__, "material", pcm_name)
        self.fdtd.setnamed(__BOT_STRUCT_NAME__, "material", pcm_name)
        self.fdtd.groupscope(__SCOPE_RESET__)

    def save_model(self) -> None:
        """Save this simulation model in the specified path during
        creation.
        
        Return:
            Save path of the simulation model
        """
        self.fdtd.save(str(self.sim_path))
        return self.sim_path

    def _update_polygon_struct(self) -> None:
        """Update the nanodisk with new polygon shape"""

        # Generate interpolated vertices between the new anchor vertices
        interp_vs = self._interp_new_xy_vertices()  
        # Change Lumerical scope to the 'nanodisk' structure group
        self.fdtd.groupscope(__STRUCT_GROUP__)
        self.fdtd.selectall()
        self.fdtd.set("vertices", interp_vs)
        self.fdtd.groupscope(__SCOPE_RESET__)
    
    def update_anchor_vertices(self, new_anchors: dict) -> None:
        """This function will update the simulation polygon
        geometries based on the new anchor vertices given.
        Args:
            new_anchors: these are new anchor vertices generated by the PSO algorithm. 
        """
        self._anchor_vertices = new_anchors
        self._update_polygon_struct()
    
    def update_nanodisk_layers(self, layer_info: np.ndarray) -> None:
        """Update the nanodisk structure according to the 
        'layer_info' information
        
        Note order of array: [top pcm thickness, bot pcm thickness, nanodisk height]
        """
        top_pcm_t, bot_pcm_t, h = layer_info
        # Calculate the Si layer thickness
        si_t = h - (top_pcm_t + bot_pcm_t)

        # Update the top PCM layer
        param_top = {"z": h - (top_pcm_t/2), "z span": top_pcm_t}
        self.fdtd.setnamed(f"{__STRUCT_GROUP__}::{__TOP_STRUCT_NAME__}", param_top)

        # Update the middle Si layer
        param_si = {"z": bot_pcm_t + (si_t/2), "z span": si_t}
        self.fdtd.setnamed(f"{__STRUCT_GROUP__}::{__MID_STRUCT_NAME__}",  param_si)
        
        # Update the bottom PCM layer
        param_bot = {"z": (bot_pcm_t/2), "z span": bot_pcm_t}
        self.fdtd.setnamed(f"{__STRUCT_GROUP__}::{__BOT_STRUCT_NAME__}", param_bot)

        # Update the RCWA geometry parameters
        self.rcwa_geometry["layer_positions"] = np.array([0, bot_pcm_t, bot_pcm_t + si_t, h])

    def update_s_param(self, final_h: float) -> None:
        """This function updates the 's_param" analysis group before saving
        the final simulation model"""
        update_fields = {"metamaterial center": final_h/2,
                         "metamaterial span": final_h}
        self.fdtd.setnamed(__S_OBJ_NAME__, update_fields)

    def update_period(self, span: float) -> None:
        """This function updates the period of the simulation model"""
        __NEW_GEOMETRY__ = {"injection_axis": "z-axis",
                            "x_min": -1 * span/2,
                            "x_max": span/2,
                            "y_min": -1 * span/2,
                            "y_max": span/2,
                            "z_min": -1 * __Z_SPAN__/2,
                            "z_max": __Z_SPAN__/2,
                            "layer_positions": self.rcwa_geometry["layer_positions"]}
        self.rcwa_geometry = __NEW_GEOMETRY__
        self.fdtd.setnamed("FDTD", {"x span": span, "y span": span})