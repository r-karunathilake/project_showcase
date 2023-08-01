from pathlib import Path
from typing import Type 

import logging
import colorama

# Different color escape sequences
__COLORS__ = {"grey": r"\x1b[38;20m",
              "green": r"\x1b[1;32m",
              "yellow": r"\x1b[33;20m",
              "red": r"\x1b[31;20m",
              "bold red": r"\x1b[31;1m",
              "reset": r"\x1b[0m"}

__FORMAT__ = "%(name)s -> %(asctime)s - %(filename)s:%(lineno)d - %(levelname)s - %(message)s"

# Create a custom formatter class with inheritance from the main Formatter class from "logging"
class ColorFormatter(logging.Formatter):
    
    color_formats = {logging.DEBUG: __COLORS__["grey"] + __FORMAT__ + __COLORS__["reset"],
                     logging.INFO: __COLORS__["green"] + __FORMAT__ + __COLORS__["reset"],
                     logging.WARNING: __COLORS__["yellow"] + __FORMAT__ + __COLORS__["reset"],
                     logging.ERROR: __COLORS__["red"] + __FORMAT__ + __COLORS__["reset"],
                     logging.CRITICAL: __COLORS__["bold red"] + __FORMAT__ + __COLORS__["reset"]}

    def format(self, record):
        log_fmt = self.color_formats.get(record.levelno)
        formatter = logging.Formatter(log_fmt, datefmt="%Y-%d-%m %I:%M:%S %p")
        return formatter.format(record)

class Logger:
    def __init__(self, name: str , lvl: str, log_file_path: Path=None) -> None:
        
        """ This function will return a logger with the requested level.

        Args:
            name: logger name (make sure it's unique)
            lvl: logging level for the logger and stream handler.
            log_file_path: Path object representing the absolute path to where the log file will be saved

        Returns:
            None. Create a logger with the requested debug level.
        """
        # Custom log format with color
        color_formatter = ColorFormatter()

        self.logger: Type[logging.Logger] = logging.getLogger(name)
        self.logger.propagate = False
        
        # Configure logger
        self.logger.setLevel(logging.DEBUG)

        # Create file handler and console handler 
        if log_file_path:
            self.logger.addHandler(self._create_file_hdlr(log_file_path, logging.Formatter(__FORMAT__)))
        self.logger.addHandler(self._create_console_hdlr(logging.Formatter(__FORMAT__), getattr(logging, lvl.upper())))

    @classmethod
    def _create_file_hdlr(cls, path: Path, format: logging.Formatter) -> Type[logging.FileHandler]:
        """Creates a file handler object for adding to logger based on given inputs"""
        fh: Type[logging.FileHandler] = logging.FileHandler(path / "pso_debug.log", mode='a')
        fh.setLevel(logging.DEBUG)
        fh.setFormatter(format)

        return fh

    @classmethod
    def _create_console_hdlr(cls, format: logging.Formatter, level: int) -> Type[logging.StreamHandler]:
        """Creates a console handler object for adding to logger based on given inputs"""
        ch: Type[logging.StreamHandler] = logging.StreamHandler()
        ch.setLevel(level)
        ch.setFormatter(format)

        return ch

    def get_logger(self) -> Type[logging.Logger]:
        """Returns a logger"""
        return self.logger


if __name__=="__main__":
    log = Logger("test logger", "info", Path.cwd() / "Python_Code/Lumerical_RCWA_PSO").get_logger()

    print(f"Current working directory {Path.cwd()}")
    log.debug("This is DEBUG message")
    log.info("This is INFO message")
    log.warning("This is WARNING message")
    log.error("This is ERROR message")
    log.critical("This is CRITICAL message")
