####################################################################################
####################                    SETUP                       ################
####################################################################################

# Import pyplot and configure universal plot settings
from matplotlib import pyplot as plt
plt.style.use("dark_background")

__SMALL_SIZE__ = 10
__MEDIUM_SIZE__ = 12
__BIGGER_SIZE__ = 15
__COLOR_MAP__ = "inferno"

plt.rc('font', size=__SMALL_SIZE__)          # controls default text sizes
plt.rc('axes', titlesize=__BIGGER_SIZE__)    # fontsize of the axes title
plt.rc('axes', labelsize=__MEDIUM_SIZE__)    # fontsize of the x and y labels
plt.rc('xtick', labelsize=__SMALL_SIZE__)    # fontsize of the tick labels
plt.rc('ytick', labelsize=__SMALL_SIZE__)    # fontsize of the tick labels
plt.rc('legend', fontsize=__SMALL_SIZE__)    # legend fontsize
plt.rc('figure', titlesize=__BIGGER_SIZE__)  # fontsize of the figure title
plt.rcParams['figure.figsize'] = (10, 5)     # set a custom figure size
plt.rcParams.update({
                        "text.usetex": False, # enable Latex text interpretation 
                        "font.family": "Helvetica",
                    })                       

# Import other packages 
from scipy.interpolate import interp2d
from pathlib import Path
from mat73 import loadmat
import numpy as np 

# Define global path objects to the MATLAB data files
E_File = Path(r"G:\My Drive\Lumerical_Simulations\Metasurface_Optical_Switch\Data\Test_Field_Plot_Data\YZ_E_Field_Data.mat") 
H_File = Path(r"G:\My Drive\Lumerical_Simulations\Metasurface_Optical_Switch\Data\Test_Field_Plot_Data\XZ_H_Field_Data.mat")

# Check if file paths exist 
if not E_File.exists() or not H_File.exists():
    print("Field data files cannot be found! Please check the data file path.")
    raise FileNotFoundError

####################################################################################
####################                   PLOT H FIELD                 ################
####################################################################################

# Read the data files 
E_data = loadmat(str(E_File))
H_data = loadmat(str(H_File))

# Plot the XZ H field 
fig, axs = plt.subplots(1, 2, constrained_layout = True)
fig.set_constrained_layout_pads(wspace = 4/72)
ax = axs[0]

x, z, H2, index = H_data["x"]/1e-9, H_data["z"]/1e-9, H_data["H2"], np.real(H_data["idx"])
Z, X = np.meshgrid(z, x)
c = ax.pcolor(X, Z, H2/np.max(H2), cmap=__COLOR_MAP__)
bar = fig.colorbar(c, ax=ax)

# Plot the structural outline
ax.contour(X, Z, index, levels = 1, colors="white", alpha = 0.9, linewidths = 2, linestyles = "dotted")

# Plot the E field lines 
Ex = H_data["Ex"]
Ez = H_data["Ez"]
xi = np.linspace(x.min(), x.max(), x.size)  # Regularly spaced grid as required by the streamplot() function 
zi = np.linspace(z.min(), z.max(), z.size)

Exi = interp2d(x, z, np.real(Ex.transpose()), kind="cubic")(xi, zi)
Ezi = interp2d(x, z, np.real(Ez.transpose()), kind="cubic")(xi, zi)

ax.streamplot(xi, zi, Exi, Ezi, color = "white", linewidth=1, density=1, arrowstyle = '->', arrowsize = 1.5)

# Label the axis
bar.set_label("$|H|^{2}/|H|^{2}_{max.}$")
ax.set_title("H-Field Intensity")
ax.set_xlabel("X (nm)")
ax.set_ylabel("Z (nm)")

####################################################################################
####################                   PLOT E FIELD                 ################
####################################################################################

# Plot the YZ E Field
ax = axs[1]
y, z, E2, index = E_data["y"]/1e-9, E_data["z"]/1e-9, E_data["E2"], np.real(E_data["idz"])
Z, Y = np.meshgrid(z, y)
c = ax.pcolormesh(Y, Z, E2/np.max(E2), cmap=__COLOR_MAP__, edgecolor="none", shading="nearest")
bar = fig.colorbar(c, ax=ax)

# Plot the structural outline
ax.contour(X, Z, index, levels = 1, colors="white", alpha = 0.9, linewidths = 2, linestyles = "dotted")

# Plot the H field lines 
Hy = E_data["Hy"]
Hz = E_data["Hz"]
yi = np.linspace(y.min(), y.max(), y.size)  # Regularly spaced grid as required by the streamplot() function 
zi = np.linspace(z.min(), z.max(), z.size)

Hyi = interp2d(y, z, np.real(Hy.transpose()), kind="cubic")(yi, zi)
Hzi = interp2d(y, z, np.real(Hz.transpose()), kind="cubic")(yi, zi)

ax.streamplot(yi, zi, Hyi, Hzi, color = "white", linewidth=1, density=1, arrowstyle = '->', arrowsize = 1.5, cmap=__COLOR_MAP__)

# Label the axis
bar.set_label("$|E|^{2}/|E|^{2}_{max.}$")
ax.set_title("E-Field Intensity")
ax.set_xlabel("$Y (nm)$")
ax.set_ylabel("$Z (nm)$")

plt.savefig(Path("ED_and_MD_Field.svg"), format="svg")
plt.show()