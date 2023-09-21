package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;

public class Table {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final Board chessBoard; 

    private final static Dimension FRAME_DIM = new Dimension(600, 600);

    public Table(){
        // Main window 
        this.gameFrame = new JFrame("Java Chess Engine");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(FRAME_DIM);

        // Create the standard chess board
        this.chessBoard = Board.createInitialBoard(); 

        // Load the image from a file 
        setCustomWindowIcon(); 

        // Create menu 
        this.gameFrame.setJMenuBar(createTableMenuBar());

        // Create board panel
        this.boardPanel = new BoardPanel(this.chessBoard); 
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
    
        // Center the JFrame on the screen
        this.gameFrame.setLocationRelativeTo(null);
        this.gameFrame.setVisible(true);
    }

    private void setCustomWindowIcon(){
        try {
            System.out.println(BoardUtils.ICON_LIBRARY_PATH.toString());

            // Load the custom icon image from assets
            Path iconPath = BoardUtils.ICON_LIBRARY_PATH.resolve("chess_window_icon.png"); 

            // Check if the icon file exists
            if (iconPath.toFile().exists()) {
                // Create an ImageIcon from the icon image file
                ImageIcon icon = new ImageIcon(iconPath.toString());

                // Set the JFrame's icon to the loaded image
                this.gameFrame.setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        // Add a way to read game file saves
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("Open the PGN file");
            }
        });
        fileMenu.add(openPGN);
        
        // Add a way to exit the game 
        final JMenuItem existMenuItem = new JMenuItem("Exit");
        existMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        fileMenu.add(existMenuItem);

        return fileMenu; 
    }
}
