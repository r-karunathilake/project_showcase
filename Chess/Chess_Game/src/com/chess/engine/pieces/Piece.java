package com.chess.engine.pieces;

import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

abstract public class Piece {
    protected final int piecePosition; 
    protected final Alliance pieceAlliance; 

    Piece(final int piecePosition, final Alliance pieceAlliance){
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition; 
    }

    public Alliance getPieceAlliance(){
        return this.pieceAlliance;
    }
    // Collection of legal moves for this chess piece 
    public abstract Collection<Move> calculateLegalMoves(final Board board);
}
