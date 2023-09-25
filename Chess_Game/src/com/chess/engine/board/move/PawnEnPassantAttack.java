package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.board.Builder;
import com.chess.engine.pieces.Piece;

public class PawnEnPassantAttack extends PawnAttackMove{

    public PawnEnPassantAttack(final Board board, 
                               final Piece movedPiece, 
                               final int destinationCoordinate, 
                               final Piece attackedPiece) {
        super(board, movedPiece, destinationCoordinate, attackedPiece);
    }

    @Override
    public boolean equals(final Object other){
        return this == other || (other instanceof PawnEnPassantAttack && super.equals(other));
    }

    @Override
    public Board execute(){
        final Builder builder = new Builder();

        // Update the position of current player pieces that are NOT under attack
        for(final Piece piece : this.board.currentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }

        // Update the position of opponent player pieces that are NOT under attack
        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            if(!piece.equals(this.getAttackedPiece())){
                builder.setPiece(piece);
            }
        }

        builder.setPiece(this.movedPiece.newPiece(this));
        builder.nextPlayer(this.board.currentPlayer().getOpponent().getAlliance());
        builder.setMoveTransition(this); 
        
        return builder.build();
    }
    
}
