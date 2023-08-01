from pyswarms.backend.operators import compute_objective_function, compute_pbest
from pyswarms.single.local_best import LocalBestPSO as LPSO
from typing import Callable, Tuple
from collections import deque
from gen_logger import Logger
from pathlib import Path

# Note: multiprocessing does not work with the Lumerical API due to 
#       pickling issue.  
import multiprocessing as mp
import numpy as np

# Change the logging level for more information
#   - DEBUG
#   - INFO
#   - WARNING
#   - ERROR
#   - CRITICAL
__LOG__ = Logger(__name__, "INFO", Path.cwd() / "Python_Code/Lumerical_RCWA_PSO").get_logger()

class Custom_LPSO(LPSO):
    """This is a custom class implementing global best particle swarm optimization
    based on the base 'GlobalBestPSO' class
    """

    def __init__(self,
                 n_particles: int,
                 dimensions: int,
                 options: dict,
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
                         bounds,
                         oh_strategy,
                         bh_strategy,
                         velocity_clamp,
                         vh_strategy,
                         center,
                         ftol,
                         ftol_iter,
                         init_pos)

    # Override the parent optimize method with custom method for changing hyperparameters dynamically
    def optimize(self, obj_func: Callable, max_iter: int, end_opts: dict, num_processes: int=None,**kwargs: dict) -> Tuple:
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
        mp_pool = None if num_processes is None else mp.Pool(num_processes)
        self.swarm.pbest_cost = np.full(self.swarm_size[0], np.inf)
        ftol_history = deque(maxlen=self.ftol_iter)

        
        for iter in self.rep.pbar(max_iter, self.name):
            __LOG__.info(f"Iteration: {iter + 1}, Options: {self.swarm.options}")
            
            # Compute cost for current position and personal best 
            self.swarm.current_cost = compute_objective_function(self.swarm, obj_func, pool=mp_pool, **kwargs)
            self.swarm.pbest_pos, self.swarm.pbest_cost = compute_pbest(self.swarm)

            # Set current best cost in neighborhood
            current_best_cost = np.min(self.swarm.best_cost)
            self.swarm.best_pos, self.swarm.best_cost = self.top.compute_gbest(self.swarm, p=self.p, k=self.k)

            self.rep.hook(best_cost=self.swarm.best_cost)

            # Save the history
            hist = self.ToHistory(best_cost=self.swarm.best_cost,
                                  mean_pbest_cost=np.mean(self.swarm.pbest_cost),
                                  mean_neighbor_cost=np.mean(self.swarm.best_cost),
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

        # Obtain the final best cost and position of the particles 
        final_best_cost = self.swarm.best_cost.copy()
        final_best_position = self.swarm.best_pos.copy()

        __LOG__.info(f"Optimization finished | best cost: {final_best_cost}, best position: {final_best_position}")

        if num_processes is not None:
            mp.pool.close()

        return final_best_cost, final_best_position