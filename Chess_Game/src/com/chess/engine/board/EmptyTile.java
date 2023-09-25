package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public final class EmptyTile extends Tile{
    protected EmptyTile(final int tileCoordinate){
        super(tileCoordinate);
    }

    @Override
    public String toString(){
        return "-";
    }

    @Override
    public boolean isTileOccupied(){
        // By definition, tile is empty
        return false;
    }

    @Override
    public Piece getPiece(){
        return null; 
    }
}