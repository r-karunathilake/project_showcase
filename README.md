# PDF INVOICE GENERATOR 

## Description 
This Python project provides the user with a GUI application to quickly create a standard profesionally formatted PDF invoice for their business. The application also provides the user the ability to import a custom logo into the final PDF invoice.  

## Installation Instructions 

## Usage and Examples
The usage of this application is fairly simple. The application
main window (seen below) contains the fields for creating the invoice. The user is prompted to enter the information as needed.The three buttons at the bottom: 'Generate Invoice', 'New Invoice', and 'Reset' are the three main action buttons. 
![This is the main window of the application](/readme_assets/invoice_generator_window.png "Main Window")

### Generate an Invoice 
Once the user has entered all the necessary information, they may press the 'Generate Invoice' button. Once clicked, the user is requested (through a pop-up window) to select a name for the PDF document and provide a save path.

The user is able to reset all fields of the application by clicking on the 'Reset' action button at the bottom of the main window. Note: this will also clear any imported logos.

The user can clear just the 'Billing Information' and "Invoiced Items" sections of the application by clicking 'New Invoice' action button. 

## License
This project is licensed under the [Creative Commons Attribution 3.0 Unported License (CC BY 3.0)](https://creativecommons.org/licenses/by/3.0/legalcode).

## Dependencies 
This project tested to function properly with the following Python dependencies:

-python = "^3.11"
-pyqt6 = ">=6.4,<7.0"
-fpdf2 = "^2.7.5" 

## Attributions:
The icon set applied to the GUI buttons in this project was created by [Yusuke Kamiyamane](https://p.yusukekamiyamane.com/) licensed under [CC BY 3.0](https://creativecommons.org/licenses/by/3.0/). These icons have not been modified from the original.

