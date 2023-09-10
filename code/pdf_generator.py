from fpdf.enums import XPos, YPos, Align
from pathlib import Path
from fpdf import FPDF

BASE_RES_PATH = Path(r"C:\Users\rlakn\Documents\Coding_Lessons\Invoice_Generator_Project\resources")
TITLE = "INVOICE"

class PDF(FPDF):
    """This class will override the some default methods of the FPDF class to 
    generate a custom invoice PDF file."""
    def __init__(self, file_path: Path, invoice_data: dict, *args, **kwargs):
        super().__init__(*args, **kwargs)
        # PDF file save path 
        self.save_path = file_path
        # Data to be written to PDF 
        self.data = invoice_data

    def _draw_line(self) -> None:
        """ This function inserts horizontal
            line across the effective width
            of the page.
        """
        # Set the line color to black (0, 0, 0)
        self.set_draw_color(0, 0, 0)
        # Set the line thickness (in mm)
        self.set_line_width(0.2)
        # Draw horizontal section division line 
        self.line(self.l_margin, self.y, self.l_margin + self.epw, self.y)

    def footer(self):
        # Set position of the footer
        self.set_y(-15) # 15 mm from the bottom
        self.set_font("helvetica", "I", 10)
        # Set color to gray
        self.set_text_color(169, 169, 169)
        # Add page number
        self.cell(0, 10, f"Page {self.page_no()}/{{nb}}", align=Align.C)

    def header(self) -> None:
        """ Add the invoice logo to the header.
        """
        # Header font
        self.set_font("helvetica", "B", 36)
        # Set the text color (R, G, B)
        self.set_text_color(18, 124, 216)

        logo = None
        try:
            if self.data["Logo File"].exists():
                logo = self.data["Logo File"]
                # Add logo to header
                self.img_info = self.image(str(logo), 10, 8, 20)
        # No logo to add 
        except KeyError as e:
            pass

        # Calculate the width of the title
        title_w = self.get_string_width(TITLE) + 6 # +6 for padding for borders
        self.set_x(self.w - (self.r_margin + title_w)) # right align title

        # Header title
        self.cell(title_w, 15, TITLE, border=False, align=Align.R,
                  fill=False, new_x=XPos.RMARGIN)
        # line break
        if logo:
            self.ln(self.img_info.rendered_height)
        else:
            self.ln(13)

        # Draw horizontal line
        self._draw_line()

    def _section_title(self, section: str) -> None:
        """ Write the invoice section title to the PDF. 

        Args:
            section (str): section title as a string 
        """
        self.set_font('Helvetica', 'I', 12)
        # Background color
        self.set_fill_color(200, 220, 255)
        # Title
        self.cell(0, 6, f'{section}', False, align='L', fill=True,
                  new_x=XPos.LMARGIN, new_y=YPos.NEXT)
        # Line break
        self.ln(5)

    def _invoice_details(self) -> None:
        """ This function will add the text to the left-margin of the
            PDF document. 
        """
        self.set_font('Helvetica', '', 12)
        comp_info = self.data["Service Provider Information"]
        
        # Line #1
        r = "Invoice #: " + comp_info['Invoice Number']
        l = comp_info["Company Name"]
        self._add_l_r_text(r, l)

        # Line#2
        r = "Invoice Date: " + comp_info["Invoice Date:"]
        l = f"{comp_info['First Name']} {comp_info['Last Name']}"
        self._add_l_r_text(r, l)

        # Line #3
        r = "GST #: " + comp_info['GST Number']
        l = comp_info["Street"]
        self._add_l_r_text(r, l)

        # Line #4
        r = "Due Date: " + comp_info['Due Date:']
        l = f"{comp_info['City']} {comp_info['Province']} {comp_info['Postal Code']}"
        self._add_l_r_text(r, l)

        # Line #5
        l = f"{comp_info['Phone Number']}"
        self._add_l_r_text("", l)

        # Line #6
        l = f"{comp_info['Email']}"
        self._add_l_r_text("", l)

        self.ln(4)
        self._draw_line()

    def _add_l_r_text(self, r_str:str, l_str: str) -> None: 
        r_x = self.w - (self.get_string_width(r_str) + self.r_margin)
        self.cell(self.get_string_width(l_str), 5, l_str, align="L")
        self.set_x(r_x)
        self.cell(self.get_string_width(r_str), 5, r_str, align="R")
        self.ln(5)

    def write_pdf(self) -> bool:
        # Add a page 
        self.add_page()

        self._section_title("Invoice Details")
        # Write the company information to invoice
        self._invoice_details()

        self._section_title("Billing Details")
        self._section_title("Invoiced Items")

        # Output the PDF 
        self.output(str(self.save_path))
        return True

if __name__ == "__main__":
    pass 
