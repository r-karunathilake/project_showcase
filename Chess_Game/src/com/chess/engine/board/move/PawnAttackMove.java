package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Piece;

public class PawnAttackMove extends AttackMove{

    public PawnAttackMove(Board board, 
                          Piece movedPiece, 
                          int destinationCoordinate,
                          Piece attackedPiece) {
        super(board, movedPiece, destinationCoordinate, attackedPiece);
    }
    
    @Override
    public boolean equals(final Object other){
        return this == other || (other instanceof PawnAttackMove && super.equals(other));
    }

    @Override
    public String toString(){
        return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) 
               + "x" + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
    }
}
