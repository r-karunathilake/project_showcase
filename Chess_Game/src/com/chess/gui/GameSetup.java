package com.chess.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.chess.engine.Alliance;
import com.chess.engine.PlayerType;
import com.chess.engine.player.Player;

public class GameSetup extends JDialog{
    private PlayerType whitePlayerType;
    private PlayerType blackPlayerType;
    private JSpinner aiDepthSpinner;
    
    private static final String PLAYER_TEXT = "Player";
    private static final String COMPUTER_TEXT = "Computer";

    GameSetup(final JFrame frame,
              boolean modal) { //blocks user input to the top window?
    
        super(frame, modal);
        setLocationRelativeTo(frame);

        // Setup the dialog box layout 
        final JPanel setupPanel = new JPanel(new GridLayout(4, 3));

        // White radio buttons 
        final JRadioButton whiteHumanBtn = new JRadioButton(PLAYER_TEXT);
        final JRadioButton whiteComputerBtn = new JRadioButton(COMPUTER_TEXT);
        
        whiteHumanBtn.setActionCommand(PLAYER_TEXT);
        whiteHumanBtn.setSelected(true);

        final ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanBtn);
        whiteGroup.add(whiteComputerBtn);

        // Black radio buttons 
        final JRadioButton blackHumanBtn = new JRadioButton(PLAYER_TEXT);
        final JRadioButton blackComputerBtn = new JRadioButton(COMPUTER_TEXT);
        
        blackHumanBtn.setSelected(true);

        final ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanBtn);
        blackGroup.add(blackComputerBtn);

        getContentPane().add(setupPanel);
        
        setupPanel.add(new JLabel("White Player:"));
        setupPanel.add(whiteHumanBtn);
        setupPanel.add(whiteComputerBtn);

        setupPanel.add(new JLabel("Black Player:"));
        setupPanel.add(blackHumanBtn);
        setupPanel.add(blackComputerBtn);
        
        setupPanel.add(new JLabel("Searching Depth for AI: "));
        this.aiDepthSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 
                                                                  5, 1)); 
        
        setupPanel.add(this.aiDepthSpinner);
        setupPanel.add(new JLabel(""));

        setupPanel.add(new JLabel(""));
        JButton okBtn = new JButton("Ok");
        JButton cancelBtn = new JButton("Cancel");

        // Action listener for the OK button 
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                whitePlayerType = whiteComputerBtn.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
                blackPlayerType = blackComputerBtn.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
                
                // Close the dialog window
                dispose();
            }
        });

        // Action listener for the CANCEL button 
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                // Close the dialog window
                dispose();
            }
        });

        setupPanel.add(cancelBtn);
        setupPanel.add(okBtn);

        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void promptUser(){
        setVisible(true);
        repaint();
    }

    public boolean isAIPlayer(final Player player){
        if(player.getAlliance() == Alliance.WHITE){
            return getWhiteType() == PlayerType.COMPUTER;
        }
        return getBlackType() == PlayerType.COMPUTER;
    }

    private PlayerType getWhiteType() {
        return this.whitePlayerType;
    }

    private PlayerType getBlackType() {
        return this.blackPlayerType;
    }
    public int getAIDepth(){
        return (Integer) this.aiDepthSpinner.getValue();
    }
}

