package com.chess.gui;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.move.Move;
import com.chess.engine.pieces.Piece;
import com.chess.gui.Table.MoveLog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.google.common.primitives.Ints;

public class CapturedPiecesPanel extends JPanel{
    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.RED;
    private static final Dimension PIECE_DIMS = new Dimension(75, 80);

    public CapturedPiecesPanel(){
        super(new BorderLayout());
        setBackground(Color.decode("0xccd9ff"));
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(PIECE_DIMS);
    }

    public void redo (final MoveLog moveLog){
        this.southPanel.removeAll();
        this.northPanel.removeAll();

        final List<Piece> whiteCapturedPieces = new ArrayList<>();
        final List<Piece> blackCapturedPieces = new ArrayList<>();

        for(final Move move : moveLog.getMoves()){
            if(move.isAttack()){
                final Piece capturedPiece = move.getAttackedPiece(); 
                if(capturedPiece.getPieceAlliance().isWhite()){
                    whiteCapturedPieces.add(capturedPiece);
                }
                else if (capturedPiece.getPieceAlliance().isBlack()){
                    blackCapturedPieces.add(capturedPiece);
                }
                else{
                    throw new RuntimeException("Move is NOT made by the white or black player. Not possible!");
                }
            }
        }

        Collections.sort(whiteCapturedPieces, new Comparator<Piece>(){
            @Override
            public int compare(Piece o1, Piece o2){
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        Collections.sort(blackCapturedPieces, new Comparator<Piece>(){
            @Override
            public int compare(Piece o1, Piece o2){
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        // Draw the taken pieces
        for(final Piece capturedPiece : whiteCapturedPieces){
            try {
                // File name e.g. WB.gif (White Bishop) or BN.gif (Black Knight)
                final BufferedImage image = ImageIO.read(new File(BoardUtils.ICON_LIBRARY_PATH.resolve(capturedPiece
                                                                                                                .getPieceAlliance()
                                                                                                                .toString()
                                                                                                                .substring(0, 1) 
                                                                                                                + capturedPiece.toString() 
                                                                                                                + ".gif").toString())); 
                this.northPanel.add(new JLabel(new ImageIcon(image)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(final Piece capturedPiece : blackCapturedPieces){
            try {
                // File name e.g. WB.gif (White Bishop) or BN.gif (Black Knight)
                final BufferedImage image = ImageIO.read(new File(BoardUtils.ICON_LIBRARY_PATH.resolve(capturedPiece
                                                                                                                .getPieceAlliance()
                                                                                                                .toString()
                                                                                                                .substring(0, 1) 
                                                                                                                + capturedPiece.toString() 
                                                                                                                + ".gif").toString())); 
                this.southPanel.add(new JLabel(new ImageIcon(image)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        validate(); 
    }
}


