package com.chess.engine.pieces;

import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.PieceType;
import com.chess.engine.board.Board;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.PawnJump;

abstract public class Piece {
    protected final PieceType pieceType;
    protected final int piecePosition; 
    protected final Alliance pieceAlliance; 
    protected final boolean isFirstMove;
    private final int cachedHashCode;

    Piece(final PieceType pieceType, 
          final int piecePosition, 
          final Alliance pieceAlliance){

        this.pieceType = pieceType;
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition; 
        this.isFirstMove = false;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        final int prime = 31;
        int result = 7; // arbitrary non-zero constant integer 
        result += pieceType.hashCode();
        result = prime * result + pieceAlliance.hashCode();
        result = prime * piecePosition;
        result = prime * result + (isFirstMove ? 1 : 0);
        return result;
    }

    // Object equality behavior 
    @Override
    public boolean equals(final Object other){
        // If the two object refrences are the same
        if(this == other){
            return true;
        }
        
        // Using the 'instanceof' comparison returns 'true' for subclasses
        // as well, which is a desired behavior in this case.
        if(!(other instanceof Piece)){
            return false; 
        }
        
        // At this point, the 'other' object MUST be of class 'Piece'
        final Piece otherPiece = (Piece) other; 
        return (piecePosition == otherPiece.getPiecePosition()) && (pieceType == otherPiece.getPieceType()) &&
                (pieceAlliance == otherPiece.getPieceAlliance()) && (isFirstMove == otherPiece.isFirstMove());
    }

    @Override
    public int hashCode(){
        return this.cachedHashCode;
    }


    public Alliance getPieceAlliance(){
        return this.pieceAlliance;
    }

    public boolean isFirstMove(){
        return this.isFirstMove; 
    }
        
    public Integer getPiecePosition() {
        return this.piecePosition;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    // Collection of legal moves for this chess piece 
    abstract public Collection<Move> calculateLegalMoves(final Board board);
    abstract public Piece newPiece(Move move);
}
