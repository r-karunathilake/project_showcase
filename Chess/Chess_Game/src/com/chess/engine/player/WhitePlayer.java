package com.chess.engine.player;

import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.move.Move;
import com.chess.engine.pieces.Piece;

public class WhitePlayer extends Player{

    public WhitePlayer(final Board board, 
                       final Collection<Move> whiteLegalMoves, 
                       final Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
       return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer(); 
    }
}
