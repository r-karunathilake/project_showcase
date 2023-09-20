package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;

public class PawnMove extends Move{

    protected PawnMove(Board board, 
                       Piece movedPiece, 
                       int destinationCoordinate) {
        super(board, movedPiece, destinationCoordinate);
    }
    
}
