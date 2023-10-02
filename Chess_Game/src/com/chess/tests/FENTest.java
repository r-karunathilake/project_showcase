package com.chess.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.MoveFactory;
import com.chess.engine.player.MoveTransition;
import com.chess.pgn.FenUtils;

public class FENTest {
    @Test
    public void testInitialFENString(){
        final Board testBoard = Board.createInitialBoard();

        // Get FEN string
        String fenString = FenUtils.createFENFromGame(testBoard);

        assertEquals(fenString, 
                     "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    @Test 
    public void testPawnMoveFENString(){
        final Board testBoard = Board.createInitialBoard();
        final Move newMove = MoveFactory.createMove(testBoard, 
                                                        BoardUtils.getCoordinateAtPosition("e2"), 
                                                        BoardUtils.getCoordinateAtPosition("e4"));

        final MoveTransition transition = testBoard.currentPlayer().makeMove(newMove);
        assertTrue("Move from " + "e2" + " to " + "e4" 
                   + " NOT found in legal moves!", transition.getMoveStatus().isDone());
        
        Board newBoard = transition.getTransitionBoard(); 

        assertEquals(FenUtils.createFENFromGame(newBoard), 
                     "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
    }

    @Test
    public void testMultiMoveFENString(){
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("c7", "c5");
        legalMoves.put("g1", "f3");

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
        assertEquals(FenUtils.createFENFromGame(newBoard), 
                     "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 0 1");
    }

}
