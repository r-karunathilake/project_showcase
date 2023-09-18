package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;

public final class AttackMove extends Move{
    final Piece pieceUnderAttack;

    public AttackMove(final Board board, final Piece movedPiece,
               final int destinationCoordinate, final Piece attackedPiece){
        super(board, movedPiece, destinationCoordinate);
        this.pieceUnderAttack = attackedPiece; 
    }
}
