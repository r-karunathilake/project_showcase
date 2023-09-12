👋 Hi, I’m @r-karunathilake, 📫 You can reach me at karunath@ualberta.ca.

This repository was created to hold my personal project files. Each project is separated into directories and a short description of each project is given below. [^1]

### Python Projects
1. **EM_Field_Visualization**: this directory contains Python code for visualizing simulated electromagnetic field data for nanostructures. The code uses the following key Python packages for visualization: `numpy`, `scipy`, `matplotlib`, `pandas`.
   
<p align="center">
      <img src="./EM_Field_Visualization/ED_E_Field_Maps.svg" width="400" height="200">
</p>

2. **Optical_Component_Designer**:  this directory contains code for automatic optimization (using a customized third party [particle swarm optimizer](https://pyswarms.readthedocs.io/en/latest/)) with real-time simulation of novel optical components known as metasurfaces. The directory also contains code for proper logging of errors for easy debugging, and [ANSYS Lumerical API](https://optics.ansys.com/hc/en-us/articles/360037824513-Python-API-overview) (`lumapi`) interaction code for proper RCWA simulation setup and data extraction. The code uses the following key Python packages: `matplotlib`, `pyswarms`, `scipy`, `numpy`.

<p align="center">
      <img src="./Optical_Component_Designer/pso_optimizer_output.gif" width="200" height="200">
</p>

4. **Invoice_Generator**: this directory contains the source code and .exe installer for a desktop application designed to create professional looking PDF invoice documents with ease. During the implementation of this project, I learned nuances of currency calculations as it relates to arithmetic precision, building plaform native application GUIs ([PyQt6](https://pypi.org/project/PyQt6/)) with proper tab orders, and buddies. The code uses the following Python packages: `PyQt6`, and `fpdf2`. 
   
[^1]: More details for some projects can be found in the README.md files under their respective directory. 
