from fpdf.enums import XPos, YPos, Align, MethodReturnValue, TableCellFillMode
from fpdf.fonts import FontFace
from decimal import Decimal, ROUND_DOWN
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
        
        # Set the document title 
        self.set_title(TITLE)
        self.set_author(invoice_data["Service Provider Information"]["Company Name"])

        self.col = 0 # Current column
        self.y0 = 0 # Ordinate of column start 

    def _move_to_column(self, col: int, num_col: int, col_spacing: int) -> float:
        """ This function will move the cursor to the start of 
            requested column.

        Args:
            col (int): current column number to be set. 
        """
        self.col = col
        x = self.l_margin + (col * ((self.epw/num_col) + col_spacing)) 
        self.set_x(x)

        return self.epw/num_col # column width

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
        self.set_text_color(0, 76, 153)

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
        title_w = self.get_string_width(self.title) + 6 # +6 for padding for borders
        self.set_x(self.w - (self.r_margin + title_w)) # right align title

        # Header title
        self.cell(title_w, 15, self.title, border=False, align=Align.R,
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
        self.cell(0, 6, f'{section}', align='L', fill=True,
                  new_x=XPos.LMARGIN, new_y=YPos.NEXT)
        # Line break
        self.ln(5)
        # Save the ordinate position 
        self.y0 = self.get_y() 

    def _write_invoice_details(self) -> None:
        """ This function will write the service provider details
            to the PDF invoice document. 
        """
        self.set_font('Helvetica', '', 12)
        comp_info = self.data["Service Provider Information"]
        
        # Left details 
        y_start = self.get_y()
        col_w = self._move_to_column(0, 2 ,5) # move to first column with 5 mm spacing
        
        l_string = f"""{comp_info["Company Name"]}
{comp_info['First Name']} {comp_info['Last Name']}
{comp_info["Street"]}
{comp_info['City']} {comp_info['Province']} {comp_info['Postal Code']}
{comp_info['Phone Number']}
{comp_info['Email']}"""

        v_space_used_l = self.multi_cell(col_w, 5, l_string, border=False, output=MethodReturnValue.HEIGHT)

        # Right details 
        self.set_y(y_start)
        col_w = self._move_to_column(1, 2 ,5) # move to second column with 5 mm spacing
        
        r_string = f"""Invoice #: {comp_info['Invoice Number']}
                       Invoice Date: {comp_info['Invoice Date:']}
                       GST #: {comp_info['GST Number']}
                       Due Date: {comp_info['Due Date:']}"""
        
        v_space_used_r = self.multi_cell(0, 5, r_string, border=False, align=Align.R, output=MethodReturnValue.HEIGHT)

        # Reset the cursor to the left margin of the page 
        self.set_y(max([y_start + v_space_used_l, y_start + v_space_used_r]))
        self.set_x(self.l_margin) 

        self.ln(3)
        self._draw_line()

    def _write_billing_details(self) -> None:
        """ This function will write the billing details
            to the PDF invoice document. 
        """
        self.set_font('Helvetica', '', 12)
        bill_to = self.data["Billing Information"]["Bill To:"]
        job_desc = self.data["Billing Information"]["Job Description:"]["Enter Job Details"]
        job_loc = self.data["Billing Information"]["Job Location:"]
        
        # Left details 
        y_start = self.get_y()
        self.set_x(self.l_margin)
        
        l_string = f"""Bill To: 
    {bill_to['First Name']} {bill_to['Last Name']}
    {bill_to["Company Name"]}
    {bill_to["Street"]}
    {bill_to['City']} {bill_to['Province']} {bill_to['Postal Code']}
    {bill_to['Phone Number']}
    {bill_to['Email']}"""
        
        v_space_used_l = self.multi_cell(0.3*self.epw, 5, l_string, border=False, output=MethodReturnValue.HEIGHT)

        # Middle job details 
        self.set_y(y_start)
        self.set_x(self.l_margin + 5 + 0.3*self.epw)
        
        m_string = f"""Job Description:
    {job_desc}"""
        
        v_space_used_m = self.multi_cell(0.3*self.epw, 5, m_string, border=False, align=Align.L, output=MethodReturnValue.HEIGHT)

        # Right details 
        self.set_y(y_start)
        self.set_x(self.l_margin + 5 + 0.6*self.epw + 5)
        
        r_string = f"""Job Location:
    {job_loc["Street"]}
    {job_loc['City']} {job_loc['Province']} {job_loc['Postal Code']}"""
        
        v_space_used_r = self.multi_cell((self.epw + self.l_margin) - (self.l_margin + 5 + 0.6*self.epw + 5), 5, r_string, border=False, align=Align.L, output=MethodReturnValue.HEIGHT)

        # Reset the cursor to the left margin of the page 
        self.set_y(max([y_start + v_space_used_l, y_start + v_space_used_m, y_start + v_space_used_r]))
        self.set_x(self.l_margin) 

        self.ln(3)
        self._draw_line()

    def _write_invoiced_items(self) -> None:
        """ This function will write the invoiced items
            to the PDF invoice document table. 
        """
        tax_percentage = self.data["Service Provider Information"]["Tax"]
        sub_total = sum(map(Decimal, [item_row["Line Total"] for item_row in self.data["Invoiced Items"]]))
        total = round(sub_total + ((Decimal(tax_percentage)/100) * sub_total), 2)
        total = total.quantize(Decimal('.01'), rounding=ROUND_DOWN)

        table_data = self.data["Invoiced Items"]
        table_headings = table_data[0].keys()
        table_data = [row.values() for row in table_data]
        
        self.set_draw_color(0, 0, 0)
        self.set_line_width(0.3)
        headings_style = FontFace(emphasis="", color=255, fill_color=(0, 76, 153))
        with self.table(
            borders_layout="INTERNAL",
            cell_fill_color=(128,128,128),
            cell_fill_mode=TableCellFillMode.ROWS,
            col_widths=(10, 55, 15, 20),
            headings_style=headings_style,
            line_height=6,
            text_align=(Align.C, Align.L, Align.R, Align.R),
            width=self.epw,
        ) as table:
            row = table.row()
            for heading in table_headings:
                row.cell(heading, align=Align.C)
            for data_row in table_data:
                row = table.row()
                for value in data_row:
                    row.cell(value)

            # Empty row
            row = table.row()
            row.cell("")
            row.cell("")
            row.cell("")
            row.cell("")

            # Add subtotal 
            row = table.row()
            row.cell("", colspan=2)
            row.cell("Subtotal", align=Align.R)
            row.cell(f"{sub_total}", align=Align.R)

            # Add tax
            row = table.row()
            row.cell("", colspan=2)
            row.cell("Tax (%)", align=Align.R)
            row.cell(f"{tax_percentage}", align=Align.R)

            # Add total
            row = table.row()
            row.cell("", colspan=2)
            row.cell("Total", align=Align.R)
            row.cell(f"{total}", align=Align.R)

    def write_pdf(self) -> bool:
        # Add a page 
        self.add_page()

        self._section_title("Invoice Details")
        # Write the company information to invoice
        self._write_invoice_details()

        self._section_title("Billing Details")
        # Write the billing information to the invoice
        self._write_billing_details()

        self._section_title("Invoiced Items")
        # Write invoiced item table
        self._write_invoiced_items()

        # Output the PDF 
        self.output(str(self.save_path))
        return True

if __name__ == "__main__":
    pass 
