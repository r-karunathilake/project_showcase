from pyswarms.backend.operators import compute_objective_function, compute_pbest
from pyswarms.single.global_best import GlobalBestPSO as GPSO
from collections import deque, OrderedDict
from setup_rcwa_sim_model import FDTDSim 
from typing import Callable, Tuple
from gen_logger import Logger
from pathlib import Path 

# Note: multiprocessing does not work with the Lumerical API due to 
#       pickling issue.  
from pathos.multiprocessing import ProcessingPool as Pool
import numpy as np

# Change the logging level for more information
#   - DEBUG
#   - INFO
#   - WARNING
#   - ERROR
#   - CRITICAL
__LOG__ = Logger(__name__, "INFO", Path.cwd() / "Python_Code/Lumerical_RCWA_PSO").get_logger()

class Custom_GPSO(GPSO):
    """This is a custom class implementing global best particle swarm optimization
    based on the base 'GlobalBestPSO' class
    """

    def __init__(self,
                 n_particles: int,
                 dimensions: int,
                 options: dict,
                 min_opt: bool=True,
                 bounds: tuple=None,
                 oh_strategy: dict=None,
                 bh_strategy: str="periodic",
                 velocity_clamp: tuple=None,
                 vh_strategy: str="unmodified",
                 center: float=1.00,
                 ftol: float=-np.inf,
                 ftol_iter: int=1,
                 init_pos: np.ndarray=None) -> None:
        """
        Args:
            oh_strategy: strategy of how to dynamically change the hyperparameters ('exp_decay', 'nonlin_mod', 'lin_variation')
            options: Starting value for the hyperparameters 
        """
        
        # Make this child inherit all methods and properties from the parent
        super().__init__(n_particles,
                         dimensions,
                         options,
                         min_opt,
                         bounds,
                         oh_strategy,
                         bh_strategy,
                         velocity_clamp,
                         vh_strategy,
                         center,
                         ftol,
                         ftol_iter,
                         init_pos)
        
        # Minimization or maximization PSO problem 
        self.min_opt = min_opt

    # Override the parent optimize method with custom method for changing hyperparameters dynamically
    def optimize(self, obj_func: Callable, max_iter: int, end_opts: dict, num_processes: int=None, **kwargs: dict) -> Tuple:
        """This function will create a custom optimization loop with dynamically
        changing hyperparameters (c1, c2, w) for particle optimization algorithm.
        
        Args:
            obj_func: function to be optimized 
            max_inter: maximum number of iterations for the PSO algorithm
            end_opts: Ending value for the hyperparameters 
            num_processes: Number of processes to use for parallel particle evaluation
            kwargs: arguments to be passed to the function 'obj_func' during cost calculation 

        Return:
            Tuple containing the final FOM from PSO and 
        """
        __LOG__.debug(f"Calling objective function with arguments: {kwargs}")
        __LOG__.debug(f"Optimizing for {max_iter} iterations starting with {self.options}")

        # Populate memory handlers 
        self.bh.memory = self.swarm.position
        self.vh.memory = self.swarm.position 

        # Setup multiprocessing for parallel evaluation
        mp_pool = None if num_processes is None else Pool(num_processes)
        if not self.min_opt:
            self.swarm.pbest_cost = np.full(self.swarm_size[0], -np.inf)
        else:
            self.swarm.pbest_cost = np.full(self.swarm_size[0], np.inf)
        ftol_history = deque(maxlen=self.ftol_iter)
        
        for iter in self.rep.pbar(max_iter, self.name):
            __LOG__.info(f"Iteration: {iter + 1}, Options: {self.swarm.options}, Best Cost: {self.swarm.best_cost:.3f}")
            # Compute cost for current particles in the iteration
            self.swarm.current_cost = compute_objective_function(self.swarm, obj_func, pool=mp_pool, **kwargs)

            # Compute best personal position and cost for all particles in this iteration
            self.swarm.pbest_pos, self.swarm.pbest_cost = compute_pbest(self.swarm, self.min_opt) 

            # Previous global best cost (initially inf for iteration 0) 
            current_best_cost = self.swarm.best_cost

            # Calculate any changes to the global best cost and position 
            self.swarm.best_pos, self.swarm.best_cost = self.top.compute_gbest(self.swarm, self.min_opt)

            self.rep.hook(best_cost=self.swarm.best_cost)

            # Save the global best geometry every 5 iterations
            __LOG__.info(f"Taking a snapshot of current geometry in iteration {iter+1}") 
            if (kwargs and iter % 5 == 0) or (kwargs and iter + 1 == max_iter): 
                
                self.take_global_best_geometry_snapshot(kwargs["sim_model"], self.swarm.best_pos, iter+1)

            # Save the history
            hist = self.ToHistory(best_cost=self.swarm.best_cost,
                                  mean_pbest_cost=np.mean(self.swarm.pbest_cost),
                                  mean_neighbor_cost=self.swarm.best_cost,
                                  position=self.swarm.position,
                                  velocity=self.swarm.velocity)

            self._populate_history(hist)

            # Verify stop criteria based on the relative acceptable cost ftol
            relative_measure = self.ftol * (1 + np.abs(current_best_cost))
            delta = (np.abs(self.swarm.best_cost - current_best_cost) < relative_measure)
            if iter < self.ftol_iter:
                ftol_history.append(delta)
            else:
                ftol_history.append(delta)
                if all(ftol_history):
                    break

            # Perform options update with end points 
            self.swarm.options = self.oh(self.options, iternow=iter, itermax=max_iter, end_opts=end_opts)

            # Update the velocity and position of particles 
            self.swarm.velocity = self.top.compute_velocity(self.swarm, self.velocity_clamp, self.vh, self.bounds)
            self.swarm.position = self.top.compute_position(self.swarm, self.bounds, self.bh)

            __LOG__.debug(f"New layer parameters for iteration {iter+1}: {self.swarm.position[:, -4:]/1e-9}")

        # Obtain the final best cost and position of the particles 
        final_best_cost = self.swarm.best_cost.copy()
        final_best_position = self.swarm.best_pos.copy()

        __LOG__.info(f"Optimization finished | best cost: {final_best_cost}, best position: {final_best_position}")

        # TODO: Multiprocessing does not work with Lumerical API 
        if num_processes is not None:
            mp_pool.close()

        return final_best_cost, final_best_position
    
    def take_global_best_geometry_snapshot(self, fdtd_obj: FDTDSim, pos_data: np.array, iter_num: int) -> None:
        """This function was created to test if the particle shape is updated correctly 
        after each iteration of the particle swarm algorithm.
        
        Args:
            fdtd_obj: simulation object for Lumerical
            pos_data: best global best position vector
            
        Returns:
            None
        """
        num_vertices = len(fdtd_obj.anchor_vertices.keys())
        if not fdtd_obj.symmetry_enabled and not fdtd_obj.opt_nanodisk:
            # Make sure the first vertex is same as last vertex
            vertex_data = list(pos_data)[:-4]
            vertex_data = vertex_data + vertex_data[:2]

            r = vertex_data[::2] # First element, then every other 
            theta = vertex_data[1::2] # Second element, then every other 
        elif fdtd_obj.symmetry_enabled and not fdtd_obj.opt_nanodisk:
            v_data = pos_data[:-4]
            r = v_data[::2] # First element, then every other 
            theta = v_data[1::2] # Second element, then every other
        # Nanodisk optimization and symmetry doesn't matter 
        else:
            # radius x the number of vertices 
            r = [pos_data[0]] * num_vertices 
            theta = [vertex[1] for vertex in fdtd_obj.anchor_vertices.values()]
 
        v_polar = np.c_[r, theta]
        vertices = OrderedDict(map(lambda name, vertex: (name, vertex), list(fdtd_obj.anchor_vertices.keys()), v_polar))

        fdtd_obj.update_anchor_vertices(vertices) # Update the geometry
        fdtd_obj.update_nanodisk_layers(pos_data[-4:-1]) # Update the layer thicknesses 
        fdtd_obj.update_period(pos_data[-1]) # Update the period of the structure 

        base_path = r"C:\Users\Ravindu\Documents\Ravindu\Simulation_Sandbox\RCWA_Nanodisk_Optimization\Lumerical_Nanodisk_Geometry"
        sim_name= f"geometry_snapshot_iteration_{iter_num}.fsp"
        fdtd_obj.fdtd.save(str(Path(base_path) / Path(sim_name)))
        