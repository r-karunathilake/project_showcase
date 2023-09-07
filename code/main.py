from PyQt6.QtWidgets import (QApplication, QMainWindow, QLabel,
                             QGridLayout, QWidget, QTabWidget,
                             QVBoxLayout, QHBoxLayout, QToolBar,
                             QStatusBar, QCheckBox, QPushButton,
                             QDialog, QDialogButtonBox, QFrame,
                             QLineEdit, QFormLayout, QComboBox,
                             QDateEdit, QTextEdit)

from PyQt6.QtGui import QIcon, QPalette, QColor, QAction, QKeySequence 
from PyQt6.QtCore import QSize, Qt, QRect, QDate
from pathlib import Path
from datetime import date 

import sys 

"""This file contains the necessary code to build the GUI and functionality to 
extract and parse user input data for the invoice generator"""

APP_NAME = "Invoice Generator"
DEFAULT_WINDOW_SIZE = QSize(500, 500) # (width, height) in pixels 
DRAW_FRAME_BORDER = True 
PROVINCE_ABRV_LIST = sorted(["NL", "PE", "NS", "NB", "QC", "ON", "MB", "SK", "AB", "BC", "YT", "NT", "NU"])

# Custom widget by subclassing QWidget
class Color(QWidget):
    def __init__(self, color: str):
        super().__init__()

        self.setAutoFillBackground(True) # Automatically set background color 
        palette = self.palette() # Global desktop palette by default 
        palette.setColor(QPalette.ColorRole.Window, QColor(color))
        self.setPalette(palette)

class QHLine(QFrame):
    def __init__(self):
        super().__init__()
        self.setFrameShape(QFrame.Shape.HLine)
        self.setFrameShadow(QFrame.Shadow.Sunken)
        self.setGeometry(QRect())

class QVLine(QFrame):
    def __init__(self):
        super().__init__()
        self.setFrameShape(QFrame.Shape.VLine)
        self.setFrameShadow(QFrame.Shadow.Sunken)

class BoxFrame(QFrame):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        if DRAW_FRAME_BORDER:
            self.setFrameShape(QFrame.Shape.Box)
        else:
            self.setFrameShape(QFrame.Shape.NoFrame)
        self.setFrameShadow(QFrame.Shadow.Plain)
        self.setLineWidth(2)

# Customize main window by subclassing QMainWindow 
class MainWindow(QMainWindow):
    def __init__(self, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)
    
        self.setWindowTitle(APP_NAME) # Name of the GUI window
        self.setWindowIcon(QIcon(str(Path.cwd() / "resources/wolf.png"))) # Add window icon 

        # Main window layout 
        self.main_lo = QVBoxLayout()
        self.main_lo.setContentsMargins(10, 10, 10, 10)
        self.main_lo.setSpacing(10)

        # Header layout 
        self.header_lo = QHBoxLayout()
        self.create_header()
        # Add header layout to the main layout 
        self.main_lo.addLayout(self.header_lo) 
        self.main_lo.addWidget(QHLine())

        # Company detail section layout 
        self.company_lo = QHBoxLayout()
        self.create_company_header()

        # Add company layout to the main layout 
        self.main_lo.addLayout(self.company_lo)
        self.main_lo.addWidget(QHLine())

        # Billing detail section layout 
        self.billing_lo = QHBoxLayout() # Job description and location go here as well
        self.create_billing_header() 

        # Add billing layout to the main layout 
        self.main_lo.addLayout(self.billing_lo)
        self.main_lo.addWidget(QHLine()) 



        # Add table of invoice items
        # TODO: call function to setup the table here 


        self.widget = QWidget()
        self.widget.setLayout(self.main_lo)
        self.setCentralWidget(self.widget)

    def create_header(self) -> None:
        """ This function creates the header GUI for the billing invoice 
            generator.
        """
        import_logo_btn = QPushButton("Import Logo")
        # TODO: Link the import logo to a pop-up dialog for file system searching

        # "INVOICE" label
        invoice_label = QLabel(text="INVOICE")
        
        self.header_lo.addWidget(import_logo_btn, alignment=Qt.AlignmentFlag.AlignLeft)
        self.header_lo.addWidget(invoice_label, alignment=Qt.AlignmentFlag.AlignRight)

    def create_billing_header(self) -> None:
        """ This function creates the GUI elements for inserting 
            billing information.        
        """
        # Create left, middle, and right frames 
        left_frame = BoxFrame()
        mid_frame = BoxFrame()  
        right_frame = BoxFrame()

        # Add widgets to the left frame (billing details)
        self._build_billing_lframe(left_frame)
        self._build_billing_mframe(mid_frame)
        self._build_billing_rframe(right_frame)

        self.billing_lo.addWidget(left_frame)
        self.billing_lo.addWidget(mid_frame)
        self.billing_lo.addWidget(right_frame)

    def _build_billing_rframe(self, frame: QFrame) -> None:
        """_summary_

        Args:
            frame (QFrame): _description_
        """
        r_lo = QFormLayout(frame)
        r_lo.setLabelAlignment(Qt.AlignmentFlag.AlignRight)
        r_lo.setFormAlignment(Qt.AlignmentFlag.AlignLeft)

        # Line #1
        self.bill_street_name = QLineEdit()
        self.bill_street_name.setPlaceholderText("Street")
        r_lo.setWidget(0, QFormLayout.ItemRole.LabelRole, QLabel("Job Location:"))
        r_lo.setWidget(0, QFormLayout.ItemRole.FieldRole, self.bill_street_name)

        # Line #2
        sub_widget = QWidget() 
        sub_h_lo = QHBoxLayout()
        sub_h_lo.setContentsMargins(0, 0, 0, 0)
        sub_widget.setLayout(sub_h_lo)

        self.bill_city = QLineEdit()
        self.bill_city.setPlaceholderText("City")
        sub_h_lo.addWidget(self.bill_city)

        self.bill_prov = QComboBox()
        self.bill_prov.setPlaceholderText("Province")
        self.bill_prov.adjustSize()
        self.bill_prov.addItems(PROVINCE_ABRV_LIST)
        sub_h_lo.addWidget(self.bill_prov)
    
        self.bill_postal_code = QLineEdit()
        self.bill_postal_code.setPlaceholderText("Postal Code")
        sub_h_lo.addWidget(self.bill_postal_code)
        r_lo.setWidget(2, QFormLayout.ItemRole.FieldRole, sub_widget)

    def _build_billing_mframe(self, frame: QFrame) -> None:
        """ This function creates the GUI elements for inserting
            job description.

        Args:
            frame (QFrame): parent frame for the 'QFormLayout'
        """
        mid_lo = QFormLayout(frame)
        mid_lo.setLabelAlignment(Qt.AlignmentFlag.AlignRight)
        mid_lo.setFormAlignment(Qt.AlignmentFlag.AlignLeft)

        self.job_desc = QTextEdit()
        self.job_desc.setPlaceholderText("Enter Job Details")
        mid_lo.setWidget(0, QFormLayout.ItemRole.LabelRole, QLabel("Job Description:"))
        mid_lo.setWidget(0, QFormLayout.ItemRole.FieldRole, self.job_desc)

    def _build_billing_lframe(self, frame: QFrame) -> None:
        """ This function creates the GUI elements for inserting
            billing information for the customer.
        Args:
            frame (QFrame): parent frame for the 'QFormLayout' 
        """
        l_lo = QFormLayout(frame)
        l_lo.setLabelAlignment(Qt.AlignmentFlag.AlignRight)
        l_lo.setFormAlignment(Qt.AlignmentFlag.AlignLeft)

        # Line #1
        sub_widget = QWidget() 
        sub_h_lo = QHBoxLayout()
        sub_h_lo.setContentsMargins(0, 0, 0, 0)
        sub_widget.setLayout(sub_h_lo)

        self.bill_first_name = QLineEdit()
        self.bill_first_name.setPlaceholderText("First Name")
        self.bill_last_name = QLineEdit()
        self.bill_last_name.setPlaceholderText("Last Name")
        sub_h_lo.addWidget(self.bill_first_name)
        sub_h_lo.addWidget(self.bill_last_name)
        l_lo.setWidget(0, QFormLayout.ItemRole.LabelRole, QLabel("Bill To:"))
        l_lo.setWidget(0, QFormLayout.ItemRole.FieldRole, sub_widget)

        # Line #2
        self.bill_comp_name = QLineEdit()
        self.bill_comp_name.setPlaceholderText("Company Name")
        l_lo.setWidget(1, QFormLayout.ItemRole.FieldRole, self.bill_comp_name)

        # Line #3 
        self.bill_street_name = QLineEdit()
        self.bill_street_name.setPlaceholderText("Street")
        l_lo.setWidget(2, QFormLayout.ItemRole.FieldRole, self.bill_street_name)

        # Line #4
        sub_widget = QWidget() 
        sub_h_lo = QHBoxLayout()
        sub_h_lo.setContentsMargins(0, 0, 0, 0)
        sub_widget.setLayout(sub_h_lo)

        self.bill_city = QLineEdit()
        self.bill_city.setPlaceholderText("City")
        sub_h_lo.addWidget(self.bill_city)

        self.bill_prov = QComboBox()
        self.bill_prov.setPlaceholderText("Province")
        self.bill_prov.adjustSize()
        self.bill_prov.addItems(PROVINCE_ABRV_LIST)
        sub_h_lo.addWidget(self.bill_prov)
    
        self.bill_postal_code = QLineEdit()
        self.bill_postal_code.setPlaceholderText("Postal Code")
        sub_h_lo.addWidget(self.bill_postal_code)
        l_lo.setWidget(3, QFormLayout.ItemRole.FieldRole, sub_widget)

        # Line #5 
        self.bill_phone = QLineEdit()
        self.bill_phone.setPlaceholderText("Phone Number")
        l_lo.setWidget(4, QFormLayout.ItemRole.FieldRole, self.bill_phone)

        # Line #6 
        self.bill_email = QLineEdit()
        self.bill_email.setPlaceholderText("Email")
        l_lo.setWidget(5, QFormLayout.ItemRole.FieldRole, self.bill_email)

    def create_company_header(self) -> None:
        """ This function creates the GUI elements for inserting 
            company information.        
        """
        # Create left and right frames 
        left_frame = BoxFrame() 
        right_frame = BoxFrame()

        self._build_company_rframe(right_frame)
        self._build_company_lframe(left_frame)

        self.company_lo.addWidget(left_frame)
        self.company_lo.addWidget(right_frame)

    def _build_company_rframe(self, frame: QFrame) -> None:
        """ This function creates the GUI elements for the right hand
            side of the company information section.        
        """
                
        # Add widgets to left frame (invoice details)
        r_lo = QFormLayout(frame)
        r_lo.setLabelAlignment(Qt.AlignmentFlag.AlignRight)
        r_lo.setFormAlignment(Qt.AlignmentFlag.AlignLeft)

        # Line #1
        self.invoice_num = QLineEdit()
        r_lo.setWidget(0, QFormLayout.ItemRole.LabelRole, QLabel("Invoice #:"))
        r_lo.setWidget(0, QFormLayout.ItemRole.FieldRole, self.invoice_num)

        # Line #2
        self.invoice_date = QDateEdit()
        self.invoice_date.setCalendarPopup(True)
        # Add today's date 
        self.invoice_date.setDate(QDate(*map(int,str(date.today()).split("-"))))
        r_lo.setWidget(1, QFormLayout.ItemRole.LabelRole, QLabel("Invoice Date:"))
        r_lo.setWidget(1, QFormLayout.ItemRole.FieldRole, self.invoice_date)

        # Line #3
        self.gst_num = QLineEdit()
        r_lo.setWidget(2, QFormLayout.ItemRole.LabelRole, QLabel("GST #:"))
        r_lo.setWidget(2, QFormLayout.ItemRole.FieldRole, self.gst_num)

        # Line #4
        self.due_date = QDateEdit()
        self.due_date.setCalendarPopup(True)
        # Add today's date 
        self.due_date.setDate(QDate(*map(int,str(date.today()).split("-"))))
        r_lo.setWidget(3, QFormLayout.ItemRole.LabelRole, QLabel("Invoice Date:"))
        r_lo.setWidget(3, QFormLayout.ItemRole.FieldRole, self.due_date)

    def _build_company_lframe(self, frame: QFrame) -> None:
        """ This function creates the GUI elements for the left hand
            side of the company information section.    

        Args:
            frame (QFrame): parent frame for the 'QFormLayout'
        """
        # Add widgets to right frame (Company name and address)
        l_lo = QFormLayout(frame)

        # Line #1
        self.comp_name = QLineEdit()
        self.comp_name.setPlaceholderText("Company Name")
        l_lo.setWidget(0, QFormLayout.ItemRole.SpanningRole, self.comp_name)

        # Line #2
        self.comp_first_name = QLineEdit()
        self.comp_first_name.setPlaceholderText("First Name")
        self.comp_last_name = QLineEdit()
        self.comp_last_name.setPlaceholderText("Last Name")
        l_lo.setWidget(1, QFormLayout.ItemRole.LabelRole, self.comp_first_name)
        l_lo.setWidget(1, QFormLayout.ItemRole.FieldRole, self.comp_last_name)

        # Line #3 
        self.comp_street_name = QLineEdit()
        self.comp_street_name.setPlaceholderText("Street")
        l_lo.setWidget(2, QFormLayout.ItemRole.SpanningRole, self.comp_street_name)

        # Line #4
        sub_widget = QWidget() 
        sub_h_lo = QHBoxLayout()
        sub_h_lo.setContentsMargins(0, 0, 0, 0)
        sub_widget.setLayout(sub_h_lo)

        self.comp_city = QLineEdit()
        self.comp_city.setPlaceholderText("City")
        sub_h_lo.addWidget(self.comp_city)

        self.comp_prov = QComboBox()
        self.comp_prov.setPlaceholderText("Province")
        self.comp_prov.adjustSize()
        self.comp_prov.addItems(PROVINCE_ABRV_LIST)
        sub_h_lo.addWidget(self.comp_prov)
    
        self.comp_postal_code = QLineEdit()
        self.comp_postal_code.setPlaceholderText("Postal Code")
        sub_h_lo.addWidget(self.comp_postal_code)
        l_lo.setWidget(3, QFormLayout.ItemRole.SpanningRole, sub_widget)

        # Line #5 
        self.comp_phone = QLineEdit()
        self.comp_phone.setPlaceholderText("Phone Number")
        l_lo.setWidget(4, QFormLayout.ItemRole.SpanningRole, self.comp_phone)

        # Line #6 
        self.comp_email = QLineEdit()
        self.comp_email.setPlaceholderText("Email")
        l_lo.setWidget(5, QFormLayout.ItemRole.SpanningRole, self.comp_email)

if __name__ == "__main__":
    # TODO: Introduce arg parser here
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show() # Windows are hidden by default 

    sys.exit(app.exec()) # Start event loop 