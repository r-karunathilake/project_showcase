package com.chess.engine.board.move;

import com.chess.engine.board.Board;

public class InvalidMove extends Move{

    protected InvalidMove() {
        super(null, -1);
    }
    
    @Override 
    public Board execute(){
        throw new RuntimeException("Cannot execute an invalid move!");
    }
}
