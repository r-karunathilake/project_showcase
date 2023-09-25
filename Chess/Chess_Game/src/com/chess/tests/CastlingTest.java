package com.chess.tests;

import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.MoveFactory;
import com.chess.engine.player.MoveTransition;

public class CastlingTest {
    @Test
    public void testWhiteKingSideCastling(){
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves for the white knight
        Map<String, String> whiteBishopLegalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        whiteBishopLegalMoves.put("e2", "e4");
        whiteBishopLegalMoves.put("e7", "e5");
        whiteBishopLegalMoves.put("g1", "f3");
        whiteBishopLegalMoves.put("d7", "d6");
        whiteBishopLegalMoves.put("f1", "e2");
        whiteBishopLegalMoves.put("d6", "d5");
        whiteBishopLegalMoves.put("e1", "g1");
        
        Board newBoard = testBoard; 

        for(Map.Entry<String, String> move : whiteBishopLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            final Move newMove = MoveFactory.createMove(newBoard, 
                                                        BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                        BoardUtils.getCoordinateAtPosition(destCord));

            final MoveTransition transition = newBoard.currentPlayer().makeMove(newMove);
            assertTrue(transition.getMoveStatus().isDone());
            newBoard = transition.getTransitionBoard(); 
        }
        
        assertTrue(newBoard.whitePlayer().isCastled());
    }
}
