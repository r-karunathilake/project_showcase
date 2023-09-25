package com.chess.engine.board;

import java.util.HashMap;
import java.util.Map;

import com.chess.engine.Alliance;
import com.chess.engine.board.move.Move;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;

// Builder class 
public class Builder {
    private Map<Integer, Piece> boardConfig;
    Alliance nextPlayer;
    Pawn enPassantPawn; 
    Move transitionMove; 
    
    // Builder constructor
    public Builder(){
        this.boardConfig = new HashMap<>(); 
    }

    public Builder setPiece(final Piece piece){
        this.boardConfig.put(piece.getPiecePosition(), piece);
        return this;
    }

    public Builder nextPlayer(final Alliance nextPlayer){
        this.nextPlayer = nextPlayer;
        return this;
    }

    public Map<Integer, Piece> getBoardConfig(){
        return boardConfig;
    }

    public Builder setEnPassantPawn(Pawn enPassantPawn) {
        this.enPassantPawn = enPassantPawn;
        return this;
    }

    public Builder setMoveTransition(final Move transitionMove){
        this.transitionMove = transitionMove;
        return this;
    }
    
    public Board build(){
        return new Board(this);
    }
}
