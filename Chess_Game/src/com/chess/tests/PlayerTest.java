package com.chess.tests;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.MoveFactory;
import com.chess.engine.player.MoveTransition;

public class PlayerTest {
    @Test
    public void testSimpleMove(){
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves for the white knight
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");

        Board newBoard = testBoard; 
        for(Map.Entry<String, String> move : legalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            final Move newMove = MoveFactory.createMove(newBoard, 
                                                        BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                        BoardUtils.getCoordinateAtPosition(destCord));

            final MoveTransition transition = newBoard.currentPlayer().makeMove(newMove);
            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       transition.getMoveStatus().isDone());
            newBoard = transition.getTransitionBoard(); 
        }
    }

    @Test 
    public void testIllegalMove(){
        String sourceCord = "e2";
        String destCord = "e6"; 
        final Board testBoard = Board.createInitialBoard();
        final Move newMove = MoveFactory.createMove(testBoard, 
                                                    BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                    BoardUtils.getCoordinateAtPosition(destCord));

        final MoveTransition transition = testBoard.currentPlayer().makeMove(newMove);
        assertFalse("Move from " + sourceCord + " to " + destCord + " NOT an illegal move! Something is wrong.", 
                    transition.getMoveStatus().isDone());
    }
}
