########################################################################################################################
############################                   Import necessary libraries                  #############################
######################################################################################################################## 
from pyswarms.utils.plotters import plot_cost_history as pch
from custom_global_optimizer import Custom_GPSO as CGPSO
from custom_local_optimizer import Custom_LPSO as CLPSO
from setup_rcwa_sim_model import FDTDSim
from typing import Union, Tuple, List 
from collections import OrderedDict
from scipy.constants import c, pi
from datetime import datetime
from gen_logger import Logger
from pathlib import Path

import matplotlib.pyplot as plt
import random as rand
import pyswarms as ps
import scipy as spy
import numpy as np
import pickle
import json

########################################################################################################################
############################                        Setup logger                           #############################
########################################################################################################################
# Change the logging level for more information
#   - DEBUG
#   - INFO
#   - WARNING
#   - ERROR
#   - CRITICAL

__LOG__ = Logger(__name__, "INFO", Path.cwd() / "Python_Code/Lumerical_RCWA_PSO").get_logger()
__UNITS__ = {"nano": 1e-9, "micro": 1e-6, "femto": 1e-15}

# Relative weights for the FOM
__PHASE_W__ = 1
__T_W__ = 1

# PSO parameters 
__SEARCH_DIM__ = 0
__SWARM_SIZE_FACTOR__ = 10 # Number of particles should be x10 the dimension 
__MAX_ITER__ = 60 # Number of iterations of the PSO algorithm 
__STARTING_OPTIONS__ = {"c1": 2.5, "c2": 0.5, "w": 0.9, "k": 4, "p": 2}
__ENDING_OPTIONS__ = {"c1": 0.5, "c2": 2.5, "w": 0.4, "k": 4, "p": 2}
__OPTION_STRATEGY__ = {"c1": "nonlin_mod", "c2": "lin_variation", "w": "exp_decay"}
__BOUNDARY_STRATEGY__ = {"default": "bounded_struct", "nanodisk": "bounded_struct"} 
__VELOCITY_STRATEGY__ = "unmodified"
__VELOCITY_CLAMP__ = (0, 0)  # Initial velocity mininmum and maximum, respectively
__NUM_PROCESSES__ = 1  # Number of processes for parallel particle evaluation  
__EARLY_TERM_ERR_THRES__ = 1e-4 # Optimization early termination threshold
__EARLY_TERM_ITER_THRES__ = 10 # Number of interation for early termination
__PHASE_DIFF_TARGET__ = 0
__PHASE_RELATIVE_ADJUSTMENT__ = 0 # From Run #1 in the amorphous phase 

class Optimizer:
    def __init__(self, path: str, name: str, gui: bool, c4: bool, disk_opt: bool) -> None:
        """ This class defines the PSO optimizer based on the initial attributes given
        Args:
            path: full path to .fsp file save location 
            name: Name of the simulation model to be saved 
            gui: If True, open the Lumerical GUI when creating the base simulation file.
            c4: Apply C4 symmetry when creating the simulation model 
            disk_opt: Optimize a nanodisk structure (when True) or a freeform geometry (when False)
        Returns:
            None
        """ 
        self.save_path = path
        self.sim_name = name
        self.open_gui = gui
        self.c4_symmetry = c4
        self.nanodisk_opt = disk_opt

        # Run the particle swarm optimization 
        self.sim_obj, self.final_pos, self.best_cost, self.cost_hist= self._run_pso()

    @classmethod
    def fom(cls, r_a: dict, r_c: dict, target_phase_diff: float) -> float:
        """This function will evaluate the FOM from the RCWA simulations
        given the results for both amorphous and crystalline SbSe cases"""

        # Get the transmission 
        T_a = np.squeeze(r_a["TotalEnergy"]["T"])
        T_c = np.squeeze(r_c["TotalEnergy"]["T"])

        __LOG__.info(f"Transmission: a-SbSe - {T_a:.3f}, c-SbSe - {T_c:.3f}")
        __LOG__.info(f"Phase Difference Target: {target_phase_diff} radians")

        # Calculate the T FOM
        T_fom_a = (1 - T_a)
        T_fom_c = T_c

        #T_fom = (2 - (T_a + T_c)) / 2

        # Get phase values
        cmplx_a = np.squeeze(r_a["Amplitudes"]["ekx_f"])
        size_a = np.shape(cmplx_a) # tuple (num_rows, num_colums) for a 2D array
        mid_idx = int(np.round((size_a[-1] + 1)/2)) - 1 # -1 because index start is 0 in Python
        P_a = np.angle(cmplx_a[mid_idx, mid_idx])
        __LOG__.debug(f"Phase a-SbSe: {P_a} radians")

        cmplx_c = np.squeeze(r_c["Amplitudes"]["ekx_f"])
        size_c = np.shape(cmplx_c) # tuple (num_rows, num_colums) for a 2D array
        mid_idx = int(np.round((size_c[-1] + 1)/2)) - 1 # -1 because index start is 0 in Python
        P_c = np.angle(cmplx_c[mid_idx, mid_idx])
        __LOG__.debug(f"Phase c-SbSe: {P_c} radians")

        __LOG__.debug("Unwrapping phase values...")
        # Unwrap all angle values to positive radians 
        if P_a < 0:
            P_a += 2*pi
        
        if P_c < 0:
            P_c += 2*pi

        __LOG__.info(f"Phase (radians): a-SbSe - {P_a:.3f}, c-SbSe - {P_c:.3f}")
        # Calculate the phase FOM
        P_target_fom = np.abs(target_phase_diff - (P_a - __PHASE_RELATIVE_ADJUSTMENT__)) / (2*pi)
        
        #P_fom = np.abs((target_phase_diff - np.abs(P_a - P_c))) / (2*pi)

        # Figure of merit for minimizing 
        fom = np.sqrt((__T_W__ * T_fom_a**2 + __T_W__ * T_fom_c**2 + __PHASE_W__ * P_target_fom**2) / 3)

        #fom = np.sqrt((__T_W__ * T_fom**2 + __PHASE_W__ * P_fom**2)/2)
        # __LOG__.info(f"Total T FOM: {T_fom:.3f}")
        # __LOG__.info(f"Total Phase FOM: {P_fom:.3f}")
        # __LOG__.info(f"Total FOM: {1-fom:.3f} with T weight {__T_W__} and Phase weight {__PHASE_W__}")

        __LOG__.info(f"Amorphous T FOM: {T_fom_a:.3f}")
        __LOG__.info(f"Crystalline T FOM: {T_fom_c:.3f}")
        __LOG__.info(f"Phase Target FOM: {P_target_fom:.3f}")
        __LOG__.info(f"Total FOM: {1-fom:.3f}")
        __LOG__.info("=" * 100)

        # Return the calculated 1-FOM and try to maximize 
        return 1-fom

    @classmethod    
    def eval_fom(cls, pos_data: np.array, sim: FDTDSim, sym: bool, disk_opt: bool) -> float:
        """This function calculates the custom FOM for each simulation
        based on the anchor vertices generated. In this case, each particle
        consists of two simulations with parameters being the anchor
        vertices with polar radius and angle. 
        Args:
            pos_data: Particle positions from the PSO algorithm 
            sim: Simulation model object
            sym: If True use C4 symmetry in optimizing the geometry
            disk_opt: If True, the geometry is fixed to a nanodisk (fixed radius for all angles) 

        Returns:
            Numpy array containing the FOM for each particle (2 simulations)
        """
        num_vertices = len(sim.anchor_vertices.keys())
        # Symmetry enabled
        if not sym and not disk_opt:
            # First element in each tuple is radius, second is theta for each anchor vertex
            v_data = pos_data[:-4]
            # Make sure the first vertex is same as last vertex
            v_data = np.append(v_data, v_data[:2]) 
            
            r = v_data[::2] # First element, then every other 
            theta = v_data[1::2] # Second element, then every other 
        # No symmetry 
        elif sym and not disk_opt:
            v_data = pos_data[:-4]
            r = v_data[::2] # First element, then every other 
            theta = v_data[1::2] # Second element, then every other 
        
        # Nanodisk optimization and symmetry doesn't matter 
        else:
            # radius x the number of vertices 
            r = [pos_data[0]] * num_vertices 
            theta = [vertex[1] for vertex in sim.anchor_vertices.values()]

        v_polar = np.c_[r, theta]

        # Pickle the final vertices and layer parameters for use as the starting point for the next optimization
        vertices = OrderedDict(map(lambda name, vertex: (name, vertex), list(sim.anchor_vertices.keys()), v_polar))
        __LOG__.debug(f"Updated vertices: {vertices}")

        layer_thicknesses = pos_data[-4:-1] 
        period = pos_data[-1]

        __LOG__.debug(f"Period: {period/1e-9} nm")

        sim.update_period(period) # Update the period of the structure
        sim.update_anchor_vertices(vertices) # Update the nanodisk geometry
        sim.update_nanodisk_layers(layer_thicknesses) # Update the nanodisk layer geometries
         
        # Run simulations
        __LOG__.debug("Running RCWA a-SbSe simulation...")
        sim.pcm_mat_update(pcm_name="n,k a-SbSe")
        result_a = sim.run_rcwa()

        __LOG__.debug("Running RCWA c-SbSe simulation...")
        # Update the simulation material to c-SbSe
        sim.pcm_mat_update("n,k c-SbSe")
        result_c = sim.run_rcwa()

        # Calculate FOM
        __LOG__.debug("Calculating FOM...")
        cost = cls.fom(result_a, result_c, __PHASE_DIFF_TARGET__)
        __LOG__.debug("=" * 100)

        layer_info = layer_thicknesses / __UNITS__["nano"]
        __LOG__.debug(f"Top PCM Layer Thickness: {layer_info[0]:.1f} nm, Bottom PCM Thickness: {layer_info[1]:.1f} nm, Total Height: {layer_info[2]:.1f} nm")

        return cost

    @classmethod
    def opt_func(cls,particle_vect: np.array, sim_model: FDTDSim, sym: bool, disk_opt: bool) -> np.array:
        """Given a list of particles with parameters radius and theta for each 
        anchor vertex, calculate the FOM for all particles."""
        # For each particle run a simulation and calculate the FOM
        fom_list = [cls.eval_fom(particle_vect[idx], sim_model, sym, disk_opt) for idx in range(particle_vect.shape[0])]

        return np.array(fom_list)
        
    def _run_pso(self) -> Tuple[FDTDSim, np.ndarray, float]:
        # Generating simulation model 
        fdtd_obj = FDTDSim(Path(self.save_path), Path(self.sim_name), self.open_gui, self.c4_symmetry, self.nanodisk_opt)

        __LOG__.debug("Configuring PSO optimizer...")
        # Calculate the dimension of the PSO search space based on number of parameters 
        if not self.c4_symmetry and not self.nanodisk_opt:
            __SEARCH_DIM__ = ((len(fdtd_obj.anchor_vertices.keys()) * 2) - 2) + 4  # +4 comes from nanodisk layer params and period
        elif self.c4_symmetry and not self.nanodisk_opt:
            __SEARCH_DIM__ = ((len(fdtd_obj.anchor_vertices.keys()) * 2)) + 4  # +4 comes from nanodisk layer params and period
        else:
            __SEARCH_DIM__ = 1 + 4 # layer thicknesses (3 variables) + period of the structure + radius of the nanodisk

        # Calculate the min and max (radius and angle) values for anchors
        v_contraints = fdtd_obj.anchor_constraints 
        __LOG__.debug(f"Anchor constraints: {v_contraints}")

        particle_count = __SEARCH_DIM__*__SWARM_SIZE_FACTOR__
        
        # Run global optimization
        if self.nanodisk_opt:
            boundary_strat = __BOUNDARY_STRATEGY__["nanodisk"]
        else:
            boundary_strat = __BOUNDARY_STRATEGY__["default"]

        opt = CGPSO(n_particles=particle_count,
                    dimensions=__SEARCH_DIM__,
                    options=__STARTING_OPTIONS__,
                    bounds=v_contraints,
                    bh_strategy= boundary_strat,
                    oh_strategy=__OPTION_STRATEGY__,                
                    vh_strategy = __VELOCITY_STRATEGY__,
                    velocity_clamp=__VELOCITY_CLAMP__,
                    ftol=__EARLY_TERM_ERR_THRES__,
                    ftol_iter=__EARLY_TERM_ITER_THRES__,
                    min_opt=False)
        
        # Run the optimization
        best_cost, best_pos = opt.optimize(self.opt_func, max_iter=__MAX_ITER__, end_opts=__ENDING_OPTIONS__ , sim_model=fdtd_obj, sym=self.c4_symmetry, disk_opt=self.nanodisk_opt)

        __LOG__.info(f"Optimization has ended, best FOM: {best_cost}")
        __LOG__.info(f"Generating cost history plot...")
        ax, cost_hist = pch(cost_history=opt.cost_history)
        plt.show(block=False)

        return fdtd_obj, best_pos, best_cost, cost_hist

    def _gen_correct_vertices(self) -> dict:
        """This function outputs the correct geometry
        vertices based symmetry condition of the
        optimization"""
        # Update the simulation model with the best particle position 
        num_vertices = len(self.sim_obj.anchor_vertices.keys())
        # Symmetry enabled
        if not self.c4_symmetry and not self.nanodisk_opt:
            # First element in each tuple is radius, second is theta for each anchor vertex
            v_data = self.final_pos[:-4]
            # Make sure the first vertex is same as last vertex
            v_data = np.append(v_data, v_data[:2]) 
            
            r = v_data[::2] # First element, then every other 
            theta = v_data[1::2] # Second element, then every other 
        # No symmetry 
        elif self.c4_symmetry and not self.nanodisk_opt:
            v_data = self.final_pos[:-4]
            r = v_data[::2] # First element, then every other 
            theta = v_data[1::2] # Second element, then every other 
        
        # Nanodisk optimization and symmetry doesn't matter 
        else:
            # radius x the number of vertices 
            r = [self.final_pos[0]] * num_vertices 
            theta = [vertex[1] for vertex in self.sim_obj.anchor_vertices.values()]

        v_polar = np.c_[r, theta]

        # Pickle the final vertices and layer parameters for use as the starting point for the next optimization
        return OrderedDict(map(lambda name, vertex: (name, vertex), list(self.sim_obj.anchor_vertices.keys()), v_polar))
    
    def _final_sanity_check(self, other_info: np.ndarray, geometry: dict) -> bool:
        """This functions runs RCWA simulation on the final best geometry to ensure
        the final best FOM matches the output of the optimizer
        
        Args:
            other_info: A numpy array containing the thicknesses of the layers and period  
            geometry: A dictionary containing the vertices of the best geometry 
        
        Returns:
            True if the FOM matches the best PSO FOM. Otherwise, False. 
        """
        __LOG__.info("Updating simulation model with best geometry...")
        layer_thicknesses = other_info[:-1] 
        period = other_info[-1]
        # Save the simulation file and return the save path
        self.sim_obj.update_nanodisk_layers(layer_thicknesses) # Update the nanodisk layer geometries 
        self.sim_obj.update_anchor_vertices(geometry)
        self.sim_obj.update_s_param(other_info[-2]) # Update the FDTD analysis group with final height 
        self.sim_obj.update_period(period) # Update the final model period 

        # Let's run RCWA on this final structure
        __LOG__.info("Running RCWA a-SbSe simulation for best geometry...")
        self.sim_obj.pcm_mat_update("n,k a-SbSe")  # Change the layers to a-SbSe material
        result_a = self.sim_obj.run_rcwa()

        __LOG__.info("Running RCWA c-SbSe simulation for best geometry...")
        # Update the simulation material to c-SbSe
        self.sim_obj.pcm_mat_update("n,k c-SbSe")
        result_c = self.sim_obj.run_rcwa()

        # Calculate FOM
        __LOG__.info("Calculating FOM for best geometry...")
        cost = self.fom(result_a, result_c, __PHASE_DIFF_TARGET__)
        __LOG__.info("=" * 100)

        self.sim_obj.pcm_mat_update("n,k a-SbSe")  # Change the layers to a-SbSe material before saving
        
        # Default tolerance is 1e-5 
        if np.isclose(cost, self.best_cost):
            return True
        
        return False

    def run_sanity_and_save(self) -> Path:
        """ This function will setup the base simulation file and run the particle swarm optimization based on
        class attributes. 
        Args:
            None
        Returns:
            Saved simulation file path as a pathlib Path object.
        """
        __LOG__.debug(f"Optimized structure parameters: {self.final_pos}")

        vertices = self._gen_correct_vertices() 
        other_params = self.final_pos[-4:] 

        __LOG__.info("Pickling final nanodisk parameters....")
        pickle_path = Path(self.save_path) / "final_pso_geo_params.pkl"
        with Path(pickle_path).open(mode="wb") as f:
            pickle.dump([vertices, other_params, self.cost_hist], f)
        __LOG__.info(f"Pickling done. Pickle saved at: {self.save_path}")

        # Check the status of the final sanity check 
        try:
            if not self._final_sanity_check(other_params, vertices):
                __LOG__.error(f"Final geometry FOM is NOT equal to best FOM of {self.best_cost:.3f} reported by PSO.")

        # Ensures the model is always saved
        finally:
            __LOG__.info("Saving simulation model...")
            return self.sim_obj.save_model()
    

if __name__ == "__main__":
    # Get current timestamp 
    start_t = datetime.now()

    base_path = r"C:\Users\Ravindu\Documents\Ravindu\Simulation_Sandbox\RCWA_Nanodisk_Optimization\Lumerical_Nanodisk_Geometry"
    sim_name= r"rcwa_unit_cell_pso_opt_sim.fsp"
    # Build and run optimization

    try:
        opt = Optimizer(path=base_path, name=sim_name, gui=True, c4=True, disk_opt=True)
        __LOG__.info(f"Final optimized simulation file is saved here: {opt.run_sanity_and_save()}")

    finally:
        import shutil
        from pathlib import Path
        source_path = Path.cwd() / "Python_Code\Lumerical_RCWA_PSO\pso_debug.log"
        dest = shutil.copyfile(source_path, base_path + "\pso_debug.log")
        print(f"Finished copying debug file to simulation model folder: {dest}")

    # Get script end timestamp
    end_t = datetime.now()

    print(f"Script execution time: {(end_t - start_t).total_seconds()} seconds")
    input("Enter something to close this session: ")