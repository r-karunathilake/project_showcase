package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Piece;

public class MajorAttackMove extends AttackMove{
    public MajorAttackMove(final Board board,
                           final Piece pieceMoved,
                           final int destinationCoordinate,
                           final Piece pieceAttacked){
        super(board, pieceMoved, destinationCoordinate, pieceAttacked);
    }

    @Override 
    public boolean equals(final Object other){
        return this == other || (other instanceof MajorAttackMove && super.equals(other));
    }

    @Override
    public String toString(){
        return movedPiece.getPieceType() 
               + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
    }
}
