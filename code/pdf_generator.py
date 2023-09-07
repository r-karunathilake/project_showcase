from fpdf.enums import XPos, YPos, Align
from pathlib import Path
from fpdf import FPDF

BASE_RES_PATH = Path(r"C:\Users\rlakn\Documents\Coding_Lessons\Invoice_Generator_Project\resources")

class PDF(FPDF):
    """This class will override the some default methods of the FPDF class to 
    generate a custom invoice PDF file."""
    def __init__(self, **kwargs) -> None:
        super().__init__(**kwargs)

    def header(self):
        pass

if __name__ == "__main__":
    pass 
