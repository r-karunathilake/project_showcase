package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;

public class AttackMove extends Move{
    private final Piece pieceUnderAttack;

    public AttackMove(final Board board, final Piece movedPiece,
               final int destinationCoordinate, final Piece attackedPiece){
        super(board, movedPiece, destinationCoordinate);
        this.pieceUnderAttack = attackedPiece; 
    }

    @Override
    public int hashCode(){
        return this.pieceUnderAttack.hashCode() + super.hashCode(); 
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }

        if(!(other instanceof AttackMove)){
            return false;
        }

        final AttackMove otherAttackMove = (AttackMove) other;
        return super.equals(otherAttackMove) && 
               getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
    }

    @Override
    public Board execute() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public boolean isAttack(){
        return true; 
    }

    @Override
    public Piece getAttackedPiece(){
        return this.pieceUnderAttack; 
    } 
}
