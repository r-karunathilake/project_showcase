package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public class QueenSideCastleMove extends CastleMove {

    public QueenSideCastleMove(Board board, Piece movedPiece, int destinationCoordinate, Rook castleRook,
                                  int castleRookStart, int castleRookDestination) {
        
        super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
    }

    @Override
    public boolean equals(final Object other){
        return this == other || (other instanceof QueenSideCastleMove 
                                 && super.equals(other));
    }
        
    @Override
    public String toString(){
        return "0-0-0";
    }
}
