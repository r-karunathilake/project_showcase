package com.chess.engine;
import java.util.List;

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
    };

    abstract public List<TilePanel> traverse(final List<TilePanel> boardTiles);
    abstract public BoardDirection opposite();
    
}
