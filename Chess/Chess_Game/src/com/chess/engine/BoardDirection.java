package com.chess.engine;
import java.util.List;

import com.chess.engine.pieces.Piece;
import com.chess.gui.Table.TilePanel;
import com.google.common.collect.Lists;

public enum BoardDirection {
    NORMAL{
        @Override
        public List<TilePanel> traverse(final List<TilePanel> boardTiles) {
            return boardTiles;
        }

        @Override
        public BoardDirection opposite() {
            return FLIPPED;
        }

        @Override
        public List<Piece> getNorthPanelPieces(List<Piece> capturedPiecesWhite, List<Piece> capturedPiecesBlack) {
            return capturedPiecesWhite;
        }

        @Override
        public List<Piece> getSouthPanelPieces(List<Piece> capturedPiecesWhite, List<Piece> capturedPiecesBlack) {
            return capturedPiecesBlack;
        }
    },
    FLIPPED{
        @Override
        public List<TilePanel> traverse(final List<TilePanel> boardTiles) {
            return Lists.reverse(boardTiles);
        }

        @Override
        public BoardDirection opposite() {
            return NORMAL; 
        }

        @Override
        public List<Piece> getNorthPanelPieces(List<Piece> capturedPiecesWhite, List<Piece> capturedPiecesBlack) {
           return capturedPiecesBlack; 
        }

        @Override
        public List<Piece> getSouthPanelPieces(List<Piece> capturedPiecesWhite, List<Piece> capturedPiecesBlack) {
            return capturedPiecesWhite;
        }
    };

    abstract public List<TilePanel> traverse(final List<TilePanel> boardTiles);
    abstract public BoardDirection opposite();
    abstract public List<Piece> getNorthPanelPieces(final List<Piece> capturedPiecesWhite, 
                                                       final List<Piece> capturedPiecesBlack);
    
    abstract public List<Piece> getSouthPanelPieces(final List<Piece> capturedPiecesWhite, 
                                                       final List<Piece> capturedPiecesBlack);
    
}
