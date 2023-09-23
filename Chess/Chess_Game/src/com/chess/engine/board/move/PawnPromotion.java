package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;

public class PawnPromotion extends Move{
    final Move decoratedMove;
    final Pawn promotedPawn;

    public PawnPromotion(final Move decoratedMove) {
        super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
        this.decoratedMove = decoratedMove;
        this.promotedPawn = (Pawn) decoratedMove.getMovedPiece(); 
    }

    @Override
    public int hashCode(){
        return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
    }

    @Override
    public boolean equals(final Object other){
        return this == other || (other instanceof PawnPromotion && super.equals(other));
    }
    
    @Override
    public Board execute(){
        final Board pawnMoveBoard = this.decoratedMove.execute();
        final Builder builder = new Builder();

        // Put current players pieces that are not the promoted pawn in their
        // current location on the new board. 
        for(final Piece piece : pawnMoveBoard.currentPlayer().getActivePieces()){
            if(!this.promotedPawn.equals(piece)){
                builder.setPiece(piece);
            }
        }

        // Put opponent players pieces on the new board at their current 
        // positions.
        for(final Piece piece : pawnMoveBoard.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }

        // Promote the piece and change player
        builder.setPiece(this.promotedPawn.getPromotionPiece().newPiece(this));
        builder.nextPlayer(pawnMoveBoard.currentPlayer().getAlliance());
       
        // Return the new board with the promoted piece 
        return builder.build(); 
    }

    @Override
    public boolean isAttack(){
        return this.decoratedMove.isAttack();
    }

    @Override
    public Piece getAttackedPiece(){
        return this.decoratedMove.getAttackedPiece();
    }

    @Override
    public String toString(){
        return ""; 
    }
}
