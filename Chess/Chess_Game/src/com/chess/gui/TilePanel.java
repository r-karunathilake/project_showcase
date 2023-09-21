package com.chess.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Piece;

public class TilePanel extends JPanel{
    private final int tileId;
    private final static Dimension TILE_PANEL_DIM = new Dimension(10, 10);
    private final static Color lightTileColor = Color.LIGHT_GRAY;
    private final static Color darkTileColor = Color.BLACK;

    TilePanel(final BoardPanel boardPanel,
              final int tileId){
        super(new GridBagLayout());
        this.tileId = tileId;
        setPreferredSize(TILE_PANEL_DIM);
        assignTileColor();
        validate(); 
    }

    private void assignTilePieceIcon(final Board board){
        this.removeAll();
        if(board.getTile(this.tileId).isTileOccupied()){
            Piece pieceOnTile = board.getTile(this.tileId).getPiece(); 

            try {
                // File name e.g. WB.gif (White Bishop) or BN.gif (Black Knight)
                final BufferedImage image = ImageIO.read(new File(BoardUtils.ICON_LIBRARY_PATH.resolve(pieceOnTile
                                                                                                                .getPieceAlliance()
                                                                                                                .toString()
                                                                                                                .substring(0, 1) 
                                                                                                                + pieceOnTile.toString() 
                                                                                                                + ".gif").toString())); 
                add(new JLabel(new ImageIcon(image)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void assignTileColor() {
        if(BoardUtils.EIGHT_RANK[this.tileId] || 
           BoardUtils.SIXTH_RANK[this.tileId] ||
           BoardUtils.FOURTH_RANK[this.tileId] ||
           BoardUtils.SECOND_RANK[this.tileId]){

            setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
        }
        else if(BoardUtils.SEVENTH_RANK[this.tileId] || 
                BoardUtils.FIFTH_RANK[this.tileId] ||
                BoardUtils.THIRD_RANK[this.tileId] ||
                BoardUtils.FIRST_RANK[this.tileId]){

            setBackground(this.tileId % 2 == 0 ? darkTileColor : lightTileColor);
        }
    }
}
