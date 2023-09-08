from PyQt6.QtWidgets import (QApplication, QMainWindow, QLabel,
                             QWidget, QVBoxLayout, QHBoxLayout, 
                             QPushButton, QFrame,QLineEdit,
                             QFormLayout, QComboBox, QDateEdit,
                             QTextEdit, QTableWidget, QDoubleSpinBox, 
                             QSpinBox,QGroupBox, QTableWidgetItem,
                             QHeaderView, QSizePolicy, QAbstractItemView,
                             QMessageBox, QSpacerItem, QLayout)

from PyQt6.QtCore import QSize, Qt, QRect, QDate
from PyQt6.QtGui import QIcon, QPalette, QColor
from decimal import Decimal
from datetime import date 
from pathlib import Path
from typing import Union

import sys 

"""This file contains the necessary code to build the GUI and functionality to 
extract and parse user input data for the invoice generator"""

APP_NAME = "Invoice Generator"
DEFAULT_WINDOW_SIZE = QSize(500, 500) # (width, height) in pixels 
DRAW_FRAME_BORDER = False 
PROVINCE_ABRV_LIST = sorted(["NL", "PE", "NS", "NB", "QC", "ON", "MB", "SK", "AB", "BC", "YT", "NT", "NU"])
TABLE_HEADERS = ["Quantity", "Item Description", "Unit Price", "Line Total"]

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
        self.create_company_header()
        self.main_lo.addWidget(QHLine())

        # Billing detail section layout 
        self.create_billing_header() 
        self.main_lo.addWidget(QHLine()) 

        # Add table for invoice items
        self.create_item_table()
        self.main_lo.addWidget(QHLine()) 

        # Create footer buttons 
        self.create_footer_btns()

        self.widget = QWidget()
        self.widget.setLayout(self.main_lo)
        self.setCentralWidget(self.widget)

    def create_footer_btns(self) -> None:
        """ This function will create the GUI footer action
            buttons. 
        """
        # Add the footer button to save, reset and generate invoice
        f_group = QGroupBox("Actions")
        f_lo = QHBoxLayout(f_group)
        
        # Generate invoice button 
        btn_gen = QPushButton(QIcon(str(Path.cwd() / "resources/icon_set/icons/blue-document-pdf-text.png")), 'Generate Invoice')
        btn_gen.clicked.connect(self.gen_invoice) # type: ignore
        f_lo.addWidget(btn_gen)

        # Generate new invoice button
        btn_new = QPushButton(QIcon(str(Path.cwd() / "resources/icon_set/icons/blue-document--plus.png")), 'New Invoice')
        btn_new.clicked.connect(self.new_invoice) # type: ignore
        f_lo.addWidget(btn_new)

        # Reset all fields of the invoice generator 
        btn_reset= QPushButton(QIcon(str(Path.cwd() / "resources/icon_set/icons/arrow-circle.png")), 'Reset')
        btn_reset.clicked.connect(self.reset_invoice_gen) # type: ignore
        f_lo.addWidget(btn_reset)

        self.main_lo.addWidget(f_group)

    def new_invoice(self, override_conf: bool) -> None:
        """ This function will reset 'Billing Information', 
            'Invoiced Items' and 'Add New Items' fields of the 
            invoice generator GUI. 
        Args:
            override_conf (bool): if True, no confirmation dialog will be presented. 
        """
        if not override_conf:
            reply = QMessageBox.question(self, 'New Invoice Confirmation', 'Are you sure you want to create a new invoice?', QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
            if reply == QMessageBox.StandardButton.Yes:
                # Clear new item fields
                self.clear_item_form()
                # Clear the table 
                self.clear_table(True)
                # Clear billing information
                self.clear_fields(self.billing_lo)
        else:
            # Clear new item fields
            self.clear_item_form()
            # Clear the table 
            self.clear_table(True)
                # Clear billing information
            self.clear_fields(self.billing_lo)


    def clear_fields(self, layout: QLayout) -> None:
        """ This function will clear the fields from
            the given layout inside the invoice generator GUI.

        Args:
            layout (QLayout): the layout to be cleared. 
        """
        for idx in range(layout.count()):
            frame = layout.itemAt(idx).widget()
            if not frame:
                continue
            for widget in frame.findChildren(QWidget):
                if isinstance(widget, QLineEdit) or isinstance(widget, QComboBox) or isinstance(widget, QTextEdit):
                    widget.clear()
                elif isinstance(widget, QDateEdit):
                    widget.setDate(QDate(*map(int,str(date.today()).split("-"))))

    def reset_invoice_gen(self) -> None:
        """ This function will reset all fields of the invoice 
            generator to default. 
        """
        reply = QMessageBox.question(self, 'Reset Invoice Confirmatoin', 'Are you sure you want to reset all field of the invoice?', QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
        if reply == QMessageBox.StandardButton.Yes:
            # Clear billing and invoiced items 
            self.new_invoice(True)

            # Clear the company information 
            self.clear_fields(self.comp_lo)

            # TODO: Clear the logo 

    def gen_invoice(self) -> None:
        """
        
        """
        # TODO: Open a dialog box and ask the user where to save the PDF and what it should be named
        # TODO: Call the PDF generation class
        # TODO: Once the PDF is generated, automatically open the file explorer where is was saved and open the PDF (on the system default viewer)
        pass

    def create_item_table(self) -> None:
        """ This function creates the portion of the GUI for adding 
            items to the invoice. 
        """
        # Table of the left side and "Add Item" form on the right side 
        self.table_lo = QHBoxLayout()

        # Left side 
        l_group = QGroupBox("Invoiced Items")
        # For a table and row of buttons under the table on the left side 
        l_lo = QVBoxLayout() 
        l_group.setLayout(l_lo)

        self.table = QTableWidget(l_group)
        # Configure the self.table 
        self.table.setColumnCount(len(TABLE_HEADERS)) # Number of columns
        self.table.setHorizontalHeaderLabels(TABLE_HEADERS) # Column labels
        self.table.setAlternatingRowColors(True)
        self.table.setSelectionMode(QAbstractItemView.SelectionMode.ExtendedSelection)
        self.table.setSelectionBehavior(QAbstractItemView.SelectionBehavior.SelectRows)
        header = self.table.horizontalHeader()       
        header.setSectionResizeMode(0, QHeaderView.ResizeMode.ResizeToContents)
        header.setSectionResizeMode(1, QHeaderView.ResizeMode.Stretch)
        header.setSectionResizeMode(2, QHeaderView.ResizeMode.ResizeToContents)
        header.setSectionResizeMode(3, QHeaderView.ResizeMode.ResizeToContents)

        l_lo.addWidget(self.table)

        table_btn_group = QWidget() 
        table_btn_lo = QHBoxLayout()
        table_btn_lo.setContentsMargins(0, 0, 0, 0)
        table_btn_group.setLayout(table_btn_lo)

        # Add table item delete button 
        btn_del = QPushButton(QIcon(str(Path.cwd() / "resources/icon_set/icons/table-delete-row.png")), 'Delete Item')
        btn_del.clicked.connect(self.delete_item) # type: ignore
        table_btn_lo.addWidget(btn_del)
        
        # Add clear table button 
        btn_clear = QPushButton(QIcon(str(Path.cwd() / "resources/icon_set/icons/table--minus.png")), 'Clear Table')
        btn_clear.clicked.connect(self.clear_table) # type: ignore
        table_btn_lo.addWidget(btn_clear)

        l_lo.addWidget(table_btn_group)
        self.table_lo.addWidget(l_group)

        # Right side
        r_group = QGroupBox("Add New Items")
        r_lo = QFormLayout()
        r_lo.setLabelAlignment(Qt.AlignmentFlag.AlignRight)
        r_group.setLayout(r_lo)

        self.table_lo.addWidget(r_group)

        self.quantity = QSpinBox()
        self.quantity.setMinimum(1)
        self.quantity.setMaximum(1000)
        self.item_desc = QTextEdit()
        self.item_desc.setPlaceholderText("Enter Item Details")
        self.item_desc.setSizePolicy(QSizePolicy.Policy.Fixed, 
                                 QSizePolicy.Policy.Expanding)

        self.unit_price = QDoubleSpinBox()
        self.unit_price.setDecimals(2)
        self.unit_price.setMinimum(-100000)
        self.unit_price.setMaximum(100000)

        r_lo.addRow("Quantity:", self.quantity)
        r_lo.addRow("Item Description:", self.item_desc)
        r_lo.addRow("Unit Price ($):", self.unit_price)

        btn_widget = QWidget() 
        btn_lo = QHBoxLayout()
        btn_lo.setContentsMargins(0, 0, 0, 0)
        btn_widget.setLayout(btn_lo)

        btn_add = QPushButton(QIcon(str(Path.cwd() / "resources/icon_set/icons/plus-button.png")), 'Add')
        btn_add.clicked.connect(self.add_item)

        btn_clear = QPushButton(QIcon(str(Path.cwd() / "resources/icon_set/icons/bin-metal.png")), 'Clear')
        btn_clear.clicked.connect(self.clear_item_form)
        
        btn_lo.addWidget(btn_add)
        btn_lo.addWidget(btn_clear)
        r_lo.setWidget(3, QFormLayout.ItemRole.FieldRole, btn_widget)

        self.main_lo.addLayout(self.table_lo)

    def clear_table(self, override_conf:bool) -> None:
        """ This function will clear the whole invoice table.

        Args:
            override_conf (bool): if True, no confirmation dialog will be presented. 
        """
        if not override_conf:
            reply = QMessageBox.question(self, "Clear Table Confirmation",
                                        "Are you sure you want to clear the whole table?",
                                        QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
            if reply == QMessageBox.StandardButton.Yes:
                self.table.setRowCount(0)
        else:
            self.table.setRowCount(0)

    def delete_item(self) -> Union[None, QMessageBox]:
        """ This function deletes the selected items from 
            the invoice table.
        """
        # Figure out which rows are selected 
        selected_rows = set()
        for item in self.table.selectedItems():
            selected_rows.add(item.row())

        # Sort the row indices in decending order (avoids index shifting during deletion)
        selected_rows = sorted(list(selected_rows), reverse=True)
        if len(selected_rows) == 0:
            return QMessageBox.warning(self, 'Warning','Please select a record to delete') # type: ignore

        button = QMessageBox.question(self, 'Clear Row(s) Confirmation', 'Are you sure that you want to delete the selected row?', QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
        if button == QMessageBox.StandardButton.Yes:
            for row in selected_rows:
                self.table.removeRow(row) # type: ignore

    def add_item(self) -> None:
        """ This function adds a new item to the table from
            'Add New Item' form.
        """ 
        row = self.table.rowCount()
        self.table.insertRow(row)
        self.table.setItem(row, 1, QTableWidgetItem(self.item_desc.toPlainText()))
        self.table.setItem(row, 2, QTableWidgetItem(self.unit_price.text()))

        # Calculate the line total 
        price = Decimal(self.unit_price.text())
        quantity = int(self.quantity.text().strip())

        line_total = price * quantity
        self.table.setItem(row, 0, QTableWidgetItem(str(quantity)))
        self.table.setItem(row, 3, QTableWidgetItem(str(line_total)))

        self.clear_item_form()

    def clear_item_form(self) -> None:
        """ This function clears the 'Add New Item' form.""" 
        self.quantity.setValue(1)
        self.unit_price.setValue(0)
        self.item_desc.clear()

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
        billing_group = QGroupBox("Billing Information")
        self.billing_lo = QHBoxLayout()
        billing_group.setLayout(self.billing_lo)
        
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

        # Add billing layout to the main layout 
        self.main_lo.addWidget(billing_group)

    def _build_billing_rframe(self, frame: QFrame) -> None:
        """ This function creates the GUI elements for inserting 
            the billing name and address. 

        Args:
            frame (QFrame): parent frame for the 'QFormLayout' 
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
        self.job_desc.setSizePolicy(QSizePolicy.Policy.Fixed, 
                                 QSizePolicy.Policy.Expanding)
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
        comp_group = QGroupBox("Service Provider Information")
        self.comp_lo = QHBoxLayout()
        comp_group.setLayout(self.comp_lo)

        # Create left and right frames 
        left_frame = BoxFrame() 
        right_frame = BoxFrame()

        self._build_company_rframe(right_frame)
        self._build_company_lframe(left_frame)

        self.comp_lo.addWidget(left_frame)

        # Add a spacer between the left and right widgets 
        spacer = QSpacerItem(10, 10, QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Minimum)
        self.comp_lo.addItem(spacer)

        self.comp_lo.addWidget(right_frame)

        # Add company layout to the main layout 
        self.main_lo.addWidget(comp_group)

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