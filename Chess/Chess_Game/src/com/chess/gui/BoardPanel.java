package com.chess.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;

public class BoardPanel extends JPanel{
    final List<TilePanel> boardTiles;
    private final static Dimension BOARD_PANEL_DIM = new Dimension(400, 350);

    BoardPanel(final Board chessBoard){
        super(new GridLayout(8, 8));

        this.boardTiles = new ArrayList<>();

        // Adding 64 tiles to board panel 
        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            final TilePanel tilePanel = new TilePanel(this, i, chessBoard);
            this.boardTiles.add(tilePanel);
            add(tilePanel);
        }
        setPreferredSize(BOARD_PANEL_DIM);
        validate();
    }
}
