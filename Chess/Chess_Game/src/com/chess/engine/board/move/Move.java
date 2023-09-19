package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;

abstract public class Move {
    final Board board; 
    final Piece movedPiece;
    final int destinationCoordinate; 

    protected Move(final Board board, final Piece movedPiece,
         final int destinationCoordinate){
            this.board = board;
            this.movedPiece = movedPiece;
            this.destinationCoordinate = destinationCoordinate; 
    }

    public int getDestinationCoordinate() {
        return destinationCoordinate; 
    }

    public Board execute() {
        return null;
    }
}
