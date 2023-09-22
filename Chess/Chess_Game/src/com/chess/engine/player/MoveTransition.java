package com.chess.engine.player;

import com.chess.engine.MoveStatus;
import com.chess.engine.board.Board;
import com.chess.engine.board.move.Move;

public class MoveTransition {
    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus; 

    // Constructor
    public MoveTransition(final Board transitionBoard,
                          final Move move,
                          final MoveStatus moveStatus){
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus; 
    }

    public MoveStatus getMoveStatus() {
        return moveStatus;
    }

    public Board getTransitionBoard(){
        return this.transitionBoard; 
    }
}
