####################################################################################
#####          INITIAL UNIVERSAL PLOTTING CONFIGURATION                      #######
####################################################################################

# Import pyplot and configure universal plot settings
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.colors import LinearSegmentedColormap
from cycler import cycler 

plt.style.use("dark_background")

__SMALL_SIZE__ = 12
__MEDIUM_SIZE__ = 12
__BIGGER_SIZE__ = 20
__MARKER_SIZE__ = 3

plt.rc('font', size=__MEDIUM_SIZE__)         # controls default text sizes
plt.rc('axes', titlesize=__BIGGER_SIZE__)    # fontsize of the axes title
plt.rc('axes', labelsize=__MEDIUM_SIZE__)    # fontsize of the x and y labels
plt.rc('xtick', labelsize=__SMALL_SIZE__)    # fontsize of the tick labels
plt.rc('ytick', labelsize=__SMALL_SIZE__)    # fontsize of the tick labels
plt.rc('legend', fontsize=__SMALL_SIZE__)    # legend fontsize
plt.rc('figure', titlesize=__BIGGER_SIZE__)  # fontsize of the figure title
plt.rcParams['figure.figsize'] = (10,5)      # set a custom figure size

# Import other packages 
from scipy.interpolate import interp2d
from pathlib import Path
from mat73 import loadmat
import numpy as np 
import pandas as pd

# Create custom colormap
cvals = [0, 0.5, 1]
colors = ["black", [12/255,141/255,245/255,1], "white"]   # Black, Blue, White
norm = plt.Normalize(min(cvals), max(cvals))
color_tuples = list(zip(map(norm, cvals), colors))
__COLOR_MAP__ = LinearSegmentedColormap.from_list("", color_tuples)

####################################################################################
#####                      READ THE SIMULATION DATA FILES                    #######
####################################################################################

BASE_PATH = Path(r".\Data")

ED_Mode_aGST = BASE_PATH / "YZ_E_Field_Data_ED_Mode_1571_aGST.mat"
ED_Mode_cGST = BASE_PATH / "YZ_E_Field_Data_ED_Mode_1571_cGST.mat"

# Read the data files 
ED_Data_aGST = loadmat(str(ED_Mode_aGST))
ED_Data_cGST = loadmat(str(ED_Mode_cGST))

####################################################################################
#####                        ED Mode Field Plots                             #######
####################################################################################

# Define the subplot mosaic 
fig, axs = plt.subplot_mosaic([["left", "right"]],
                              constrained_layout = True)
fig.set_constrained_layout_pads(wspace = 0, hspace = 0)  # Padding between the subplots 

# Parse the data based on the keys provided in the MATLAB .m files
y, z, E2_aGST, index = ED_Data_aGST["y"]/1e-9, ED_Data_aGST["z"]/1e-9, ED_Data_aGST["E2"], np.real(ED_Data_aGST["idy"])
E2_cGST = ED_Data_cGST["E2"]

# Find a normalization value between a-GST and c-GST phases
max_val = np.sqrt(np.amax([np.amax(E2_aGST), np.amax(E2_cGST)]))


####################################################
#####       Amorphous GST field plots          #####
####################################################

# Plot the a-GST ED Response
ax = axs["left"]  # Pick the left subplot 

# Define a meshgrid
Z, Y = np.meshgrid(z, y)
c = ax.pcolormesh(Y, Z, np.sqrt(E2_aGST)/max_val, cmap=__COLOR_MAP__, shading="nearest", vmin=0, vmax=1)
c.set_edgecolor('face') # get rid of the black lines in the SVG output

# Plot the structural outline using the index data 
ax.contour(Y, Z, index, levels = np.unique(index), colors="white", alpha = 0.9, linewidths = 2, linestyles = "dotted")

# Plot the H field lines 
Hy = ED_Data_aGST["Hy"]
Hz = ED_Data_aGST["Hz"]
yi = np.linspace(y.min(), y.max(), y.size)  # Regularly spaced grid as required by the streamplot() function 
zi = np.linspace(z.min(), z.max(), z.size)

Hyi = interp2d(y, z, np.real(Hy.transpose()), kind="cubic")(yi, zi)
Hzi = interp2d(y, z, np.real(Hz.transpose()), kind="cubic")(yi, zi)

ax.streamplot(yi, zi, Hyi, Hzi, color = "red", linewidth=1, density=1, arrowstyle = '->', arrowsize = 1.5)

# Label the axis
ax.set_title("a-GST")
ax.set_xlabel("Y (nm)")
ax.set_ylabel("Z (nm)")
ax.set_xlim([yi.min(), yi.max()])
ax.set_ylim([zi.min(), zi.max()])

####################################################
#####       Crystalline GST field plots        #####
####################################################

# Plot the c-GST ED Response
ax = axs["right"]
Z, Y = np.meshgrid(z, y)
c = ax.pcolormesh(Y, Z, np.sqrt(E2_cGST)/max_val, cmap=__COLOR_MAP__, shading="nearest", vmin=0, vmax=1)
c.set_edgecolor('face') # get rid of the black lines in the SVG output
bar = fig.colorbar(c, ax=ax, orientation="vertical")

# Plot the structural outline
ax.contour(Y, Z, index, levels = np.unique(index), colors="white", alpha = 0.9, linewidths = 2, linestyles = "dotted")

# Plot the H field lines 
Hy = ED_Data_cGST["Hy"]
Hz = ED_Data_cGST["Hz"]
yi = np.linspace(y.min(), y.max(), y.size)  # Regularly spaced grid as required by the streamplot() function 
zi = np.linspace(z.min(), z.max(), z.size)

Hyi = interp2d(y, z, np.real(Hy.transpose()), kind="cubic")(yi, zi)
Hzi = interp2d(y, z, np.real(Hz.transpose()), kind="cubic")(yi, zi)

ax.streamplot(yi, zi, Hyi, Hzi, color = "red", linewidth=1, density=1, arrowstyle = '->', arrowsize = 1.5)

# Label the axis
bar.set_label("$|E|/|E_{max}|$")
ax.set_title("c-GST")
fig.suptitle("ED Field Response ($\lambda = 1571$ nm)")
ax.set_xlabel("Y (nm)")
ax.set_yticks([])   # Disable y ticks 
ax.set_xlim([y.min(), y.max()])
ax.set_ylim([z.min(), z.max()])

# Save the figures as SVG files
plt.savefig(str(BASE_PATH / "ED_E_Field_Maps.svg"))
plt.show()