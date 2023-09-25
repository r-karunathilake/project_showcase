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
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");
        legalMoves.put("g1", "f3");
        legalMoves.put("d7", "d6");
        legalMoves.put("f1", "e2");
        legalMoves.put("d6", "d5");
        legalMoves.put("e1", "g1");
        
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
        
        assertTrue(newBoard.whitePlayer().isCastled());
    }

    @Test
    public void testWhiteQueenSideCastle(){
        final Board testBoard = Board.createInitialBoard();
        // Create a hashmap of all possible moves for the white knight
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");
        legalMoves.put("d2", "d3");
        legalMoves.put("d7", "d6");
        legalMoves.put("c1", "d2");
        legalMoves.put("d6", "d5");
        legalMoves.put("d1", "e2");
        legalMoves.put("h7", "h6");
        legalMoves.put("b1", "c3");
        legalMoves.put("h6", "h5");
        legalMoves.put("e1", "c1");
        
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
        
        assertTrue(newBoard.whitePlayer().isCastled());
    }

    @Test
    public void testBlackKingSideCastle(){
        final Board testBoard = Board.createInitialBoard();
        // Create a hashmap of all possible moves for the white knight
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");
        legalMoves.put("d2", "d3");
        legalMoves.put("g8", "f6");
        legalMoves.put("d3", "d4");
        legalMoves.put("f8", "e7");
        legalMoves.put("d4", "d5");
        legalMoves.put("e8", "g8");
        
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
        
        assertTrue(newBoard.blackPlayer().isCastled());
    }

    @Test
    public void testBlackQueenSideCastle(){
        final Board testBoard = Board.createInitialBoard();
        // Create a hashmap of all possible moves for the white knight
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");
        legalMoves.put("d2", "d3");
        legalMoves.put("d8", "e7");
        legalMoves.put("b1", "c3");
        legalMoves.put("b8", "c6");
        legalMoves.put("c1", "d2");
        legalMoves.put("d7", "d6");
        legalMoves.put("f1", "e2");
        legalMoves.put("c8", "d7");
        legalMoves.put("g1", "f3");
        legalMoves.put("e8", "c8");
        
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
        
        assertTrue(newBoard.blackPlayer().isCastled());
    }
}
