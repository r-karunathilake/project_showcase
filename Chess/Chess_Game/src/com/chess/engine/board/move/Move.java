package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.board.Builder;
import com.chess.engine.pieces.Piece;

abstract public class Move {
    protected final Board board; 
    protected final Piece movedPiece;
    private final int destinationCoordinate; 

    public static final Move INVALID_MOVE = new InvalidMove(); 

    protected Move(final Board board, 
                   final Piece movedPiece,
                   final int destinationCoordinate){
            this.board = board;
            this.movedPiece = movedPiece;
            this.destinationCoordinate = destinationCoordinate; 
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 7; // arbitrary non-zero constant integer 
        result = prime * result + this.destinationCoordinate;
        result = prime * result + movedPiece.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }

        // Using the 'instanceof' comparison returns 'true' for subclasses
        // as well, which is a desired behavior in this case.
        if(!(other instanceof Move)){
            return false; 
        }
        
        // At this point, the 'other' object MUST be of class 'Piece'
        final Move otherMove = (Move) other; 
        return (getDestinationCoordinate() == otherMove.getDestinationCoordinate()) && 
               (getMovedPiece().equals(otherMove.getMovedPiece()));
    }

    public int getDestinationCoordinate() {
        return destinationCoordinate; 
    }

    public Piece getMovedPiece(){
        return movedPiece;
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public Board execute(){
        final Builder builder = new Builder();
        for(final Piece piece : this.board.currentPlayer().getActivePieces()){
            // Place the piece that are NOT being moved at the same location on
            // a new board. 
            if(!this.movedPiece.equals(piece)){ // NOT a reference equality! 
                builder.setPiece(piece);
            }
        }
        // Add the opponent chess pieces to the new board 
        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }
        // Actually move the 'movedPiece' to a new destination coordinate 
        // by creating a new Piece at the destination coordinate
        builder.setPiece(this.movedPiece.newPiece(this));
        // It's the next players turn 
        builder.nextPlayer(this.board.currentPlayer().getOpponent().getAlliance());
        
        // Return the new board 
        return builder.build();
    }

    public int getCurrentCoordinate() {
        return this.getMovedPiece().getPiecePosition();
    }

}
