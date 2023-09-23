package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;

public class PawnAttackMove extends AttackMove{

    public PawnAttackMove(Board board, 
                          Piece movedPiece, 
                          int destinationCoordinate,
                          Piece attackedPiece) {
        super(board, movedPiece, destinationCoordinate, attackedPiece);
    }
    
}
