**Optical_Component_Designer**:  this directory contains code for automatic optimization (using a customized third party [particle swarm optimizer](https://pyswarms.readthedocs.io/en/latest/)) with real-time simulation of novel optical components known as metasurfaces. The directory also contains code for proper logging of errors for easy debugging, and [ANSYS Lumerical API](https://optics.ansys.com/hc/en-us/articles/360037824513-Python-API-overview) (`lumapi`) interaction code for proper RCWA simulation setup and data extraction. The code uses the following key Python packages: `matplotlib`, `pyswarms`, `scipy`, `numpy`.

<p align="center">
      <img src="./pso_optimizer_output.gif" width="200" height="200">
</p>
