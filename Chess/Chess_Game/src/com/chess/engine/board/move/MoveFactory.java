package com.chess.engine.board.move;

import java.util.ArrayList;
import java.util.List;

import com.chess.engine.board.Board;

public class MoveFactory {
    private MoveFactory(){
        throw new RuntimeException("You cannot instantiate me!"); 
    }

    public static Move createMove(final Board board, 
                                  final int currentCoordinate,
                                  final int destinationCoordinate){
         
        for(final Move move : board.getAllLegalMoves()){
            if(move.getCurrentCoordinate() == currentCoordinate &&
               move.getDestinationCoordinate() == destinationCoordinate){
                return move;
            }
        }
        return Move.INVALID_MOVE; // Return invalid move object
    }
}
