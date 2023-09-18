package com.chess.engine.pieces;

import java.util.List;

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

    // List of legal moves for this chess piece 
    public abstract List<Move> calculateLegalMoves(final Board board);
}
