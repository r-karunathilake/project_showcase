package com.chess.engine.pieces;

import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.move.Move;

abstract public class Piece {
    protected final int piecePosition; 
    protected final Alliance pieceAlliance; 
    protected final boolean isFirstMove;

    Piece(final int piecePosition, final Alliance pieceAlliance){
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition; 
        this.isFirstMove = false;
    }

    public Alliance getPieceAlliance(){
        return this.pieceAlliance;
    }

    public boolean isFirstMove(){
        return this.isFirstMove; 
    }
    // Collection of legal moves for this chess piece 
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public Integer getPiecePosition() {
        return this.piecePosition;
    }
}
