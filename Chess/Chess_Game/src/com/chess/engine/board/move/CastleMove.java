package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.board.Builder;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class CastleMove extends Move{

    protected final Rook castleRook;
    protected final int castleRookStart;
    protected final int castleRookDestination;
    
    protected CastleMove(final Board board, 
                         final Piece movedPiece, 
                         final int destinationCoordinate,
                         final Rook castleRook,
                         final int castleRookStart,
                         final int castleRookDestination) {

        super(board, movedPiece, destinationCoordinate);

        this.castleRook = castleRook;
        this.castleRookStart = castleRookStart;
        this.castleRookDestination = castleRookDestination; 
    }

    public Rook getCastleRook(){
        return this.castleRook;
    }

    @Override
    public boolean isCastlingMove(){
        return true;
    }

    @Override
    public Board execute(){
        final Builder builder = new Builder();

        for(final Piece piece : this.board.currentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
                builder.setPiece(piece);
            }
        }

        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }

        builder.setPiece(this.movedPiece.newPiece(this));
        builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance()));
        builder.nextPlayer(this.board.currentPlayer().getOpponent().getAlliance());

        return builder.build();

    }
}
