####################################################################################
####################                    SETUP                       ################
####################################################################################

# Import packages and configure universal plot settings
import numpy as np
import scipy.constants as spc

from matplotlib import pyplot as plt
from matplotlib.patches import Ellipse
from asyncio import constants
from pathlib import Path 

# Import types
from typing import Tuple

# Extract the necessary constans from scipy 
kB_eV = spc.physical_constants["Boltzmann constant in eV/K"][0] # eV/K
kB = spc.physical_constants["Boltzmann constant"][0] # J/K
e = spc.physical_constants["elementary charge"][0] # J/eV
pi = spc.pi

__SMALL_SIZE__ = 12
__MEDIUM_SIZE__ = 15
__BIGGER_SIZE__ = 20

plt.rc('font', size=__SMALL_SIZE__)         # controls default text sizes
plt.rc('axes', titlesize=__BIGGER_SIZE__)    # fontsize of the axes title
plt.rc('axes', labelsize=__MEDIUM_SIZE__)    # fontsize of the x and y labels
plt.rc('xtick', labelsize=__SMALL_SIZE__)    # fontsize of the tick labels
plt.rc('ytick', labelsize=__SMALL_SIZE__)    # fontsize of the tick labels
plt.rc('legend', fontsize=__SMALL_SIZE__)    # legend fontsize
plt.rc('figure', titlesize=__BIGGER_SIZE__)  # fontsize of the figure title

plt.rcParams['figure.figsize'] = (10, 5)    # set a custom figure size
plt.rcParams.update({
                        "text.usetex": True, # enable Latex text interpretation 
                        "font.family": "Helvetica",
                    })                       

def convert_TK_to_TeV(T: float) -> float:
    """ This function converts the electron temperature from 
    eV to K"""

    return (kB/e) * T

def evaluate_logTe_from_ND(ne: float, ND: float) -> float:
    """ This function calculates the logarithm of electron temperature
    (in K) given the inputs ne (electron density) and 
    ND (particles in teh Debye sphere)"""

    # Note: numpy does not allow for fractional power of negative values.
    logTe = ND * (2/3) + ne * (1/3) + np.log10(1/kB_eV) - (2/3)*np.log10((4/3) * (7430)**3 * pi)
    return np.log10(convert_TK_to_TeV(10**logTe))

def evaluate_logTe_from_lamD(ne: float, lamD: float) -> float:
    """ This function calculates the logarithm of electron temperature
    (in K) given the inputs ne (electron density), and
    lamD (Debye length)."""
    logTe = 2 * (lamD) - 2 * np.log10(7430) - (np.log10(kB_eV) - ne)
    return  np.log10(convert_TK_to_TeV(10**logTe))

# Plot the broadband transmission results
fig, axs = plt.subplot_mosaic([["top"]],
                              constrained_layout = True)
fig.set_constrained_layout_pads(wspace = 6/72, hspace = 6.5/72)
ax = axs["top"]

####################################################################################
#####           DRAW THE LINES OF CONSTANT DEBYE LENGTH AND SPHERE             #####
####################################################################################

# Electron density [m^-3]
logne_array = np.array((0, 35))

# Constant log10(ND) plot line array
logND_array = [0, 5, 10, 15]

# Save logTe values for label creation and draw the lines of constant ND
constant_ND_vals = {}
for logND in logND_array:
    constant_ND_vals[logND] = evaluate_logTe_from_ND(logne_array, logND)
    ax.plot(logne_array, constant_ND_vals[logND], "tab:red", alpha=0.7)

# Constant Debye plot line array
loglamD_array = [4, 2, 0, -2, -4, -6 , -8, -10, -12]
constant_lamD_vals = {}
for loglamD in loglamD_array:
    constant_lamD_vals[loglamD] = evaluate_logTe_from_lamD(logne_array, loglamD)
    kwargs = {"color": "tab:blue", "alpha": 0.7, "ls": "--"}
    ax.plot(logne_array, constant_lamD_vals[loglamD], **kwargs)

####################################################################################
#####                            DRAW DATA POINTS                              #####
####################################################################################
def add_patch_of_plasma(xy: Tuple, width: int, height: int, label: str) -> None:
    """This function will add a patch of data points (plasma types) to the plot
    at specified position 'xy' with the given dimensions and labels."""

    kwargs = {"facecolor": "gold", "alpha": 0.7}
    patch = Ellipse(xy, width, height, **kwargs)
    ax.add_patch(patch)
    ax.annotate(xy=xy, text=label)

data_points = {"Fusion Reactor": {"position": (21, 4), "size": (1, 1)},
               "Torus": {"position": (19, 2), "size": (1, 1)},
               "Pinch": {"position": (23, 3), "size": (1, 1)},
               "Ionophere": {"position": (11, 0.05), "size": (1, 1)},
               "Glow Discharge": {"position": (15, np.log(2)), "size": (1, 1)},
               "Flame": {"position": (14, np.log(0.1)), "size": (1, 1)},
               "Cs Plasma": {"position": (17, np.log(0.2)), "size": (1, 1)},
               "Space": {"position": (6, np.log(0.01)), "size": (1, 1)},
               "Galaxy Cluster": {"position": (3, 4), "size": (1, 1)},
               "Supernova Remnant": {"position": (5, 3), "size": (1, 1)},
               "Solar Interior": {"position": (32, 3), "size": (1, 1)},
               "Solar Corona": {"position": (14, 2), "size": (1, 1)},
                }

# Draw the patch of plasmas
for label, point in data_points.items():
    add_patch_of_plasma(point["position"], point["size"][0], point["size"][1], label)

####################################################################################
#####                          LABEL THE LINES AND PLOT                        #####
####################################################################################
def annotate_constant_lines(x: float, y: float, label: str, xylabel_pos=None, color="black") -> None:
    """ Annotate the lines of constant ND and Debye length
    
    The text will aligned with the direction of the line through points (x[0], y[0]) 
    and (x[1], y[1]). If 'xylabel_pos' is NOT provided, the label will be set to the center of the
    line."""

    rotation = np.degrees(np.arctan2(y[1] - y[0], x[1] - x[0]))
    if xylabel_pos is None:
        xylabel_pos = ((x[0] + x[1])/2, ( y[0] + y[1])/2)
    
    text = ax.annotate(text=label, xy=xylabel_pos, ha='center', va='center', backgroundcolor='white', color=color, size=9)
    p1 = ax.transData.transform_point((x[0], y[0]))
    p2 = ax.transData.transform_point((x[1], y[1]))
    dy = (p2[1] - p1[1])
    dx = (p2[0] - p1[0])

    rotn = np.degrees(np.arctan2(dy, dx))
    text.set_rotation(rotn)

# Set axis labels and tick labels in scientific notation 
ax.set_xticklabels(['$10^{{{:d}}}$'.format(logne) for logne in range(0,36,5)])
ax.set_yticklabels(['$10^{{{:d}}}$'.format(logTe) for logTe in range(-4,7,2)])
ax.set_xlabel("Electron Number Density ($m^{-3}$)")
ax.set_ylabel("Electron Temperature (eV)")
ax.set_ylim(-4, 6)
ax.set_xlim(0, 35)

# Add labels to the lines of constant ND
xlabels = [26, 7, 6, 9]
for logND, xlabel in zip(logND_array, xlabels):
    if logND:
        label = '$N_\mathrm{{D}}=10^{{{:d}}}$'.format(logND)
    else:
        label = '$N_\mathrm{{D}}=1$'
        
    if xlabel is not None:
        ylabel = evaluate_logTe_from_ND(xlabel, logND)
        xylabel = (xlabel, ylabel)
    else:
        xylabel = None
    annotate_constant_lines(logne_array, constant_ND_vals[logND], label, xylabel, color='tab:red')

# Add labels to the lines of constant Debye length
xlabels = [2, 4, 10, 15, 15, 20, 24, 26, 30]
for loglamD, xlabel in zip(loglamD_array, xlabels):
    if loglamD:
        s_val = '10^{{{:d}}}\;\mathrm{{m}}'.format(loglamD)
    else:
        s_val = '1'
    label = '$\lambda_{{\mathrm{{D}}e}} = {}$'.format(s_val)
    if xlabel is not None:
        ylabel = evaluate_logTe_from_lamD(xlabel, loglamD)
        xylabel = (xlabel, ylabel)
    else:
        xylabel = None

    annotate_constant_lines(logne_array, constant_lamD_vals[loglamD], label, xylabel, color='tab:blue')

plt.savefig(Path("Plasma_Types_Plot.svg"), format="svg")
plt.show()