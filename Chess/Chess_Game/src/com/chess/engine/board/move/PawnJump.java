package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;

public class PawnJump extends Move{
    protected PawnJump(Board board, Piece movedPiece, int destinationCoordinate) {
        super(board, movedPiece, destinationCoordinate);
    }

    @Override
    public Board execute(){
        final Builder builder = new Builder();

        for(final Piece piece : this.board.currentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }

        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }

        final Pawn movedPawn = (Pawn) this.movedPiece.newPiece(this); 
        builder.setPiece(movedPawn);
        builder.setEnPassantPawn(movedPawn);
        builder.nextPlayer(this.board.currentPlayer().getOpponent().getAlliance());
        
        return builder.build();
    }
}