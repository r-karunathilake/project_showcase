package com.chess.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Builder;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.MoveFactory;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Rook;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;

public class CheckMateTest {

    @Test
    public void testFoolsMate(){
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("f2", "f4");
        legalMoves.put("e7", "e5");
        legalMoves.put("g2", "g4");
        legalMoves.put("d8", "h4");

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
        assertTrue(newBoard.currentPlayer().isInCheckMate());
    }

    @Test
    public void testFoolsMateAI(){  
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("f2", "f4");
        legalMoves.put("e7", "e5");
        legalMoves.put("g2", "g4");

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

        // Ask AI to calculate the next best move 
        final MoveStrategy ai_strat = new MiniMax(4);
        final Move moveAI = ai_strat.execute(newBoard);

        final Move finalMove = MoveFactory.createMove(newBoard, 
                                                      BoardUtils.getCoordinateAtPosition("d8"), 
                                                      BoardUtils.getCoordinateAtPosition("h4"));
        assertEquals(moveAI, finalMove);
    }

    @Test
    public void testScholarsMate(){
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");
        legalMoves.put("d1", "h5");
        legalMoves.put("b8", "c6");
        legalMoves.put("f1", "c4");
        legalMoves.put("g8", "f6");
        legalMoves.put("h5", "f7");

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
        assertTrue(newBoard.currentPlayer().isInCheckMate());
    }

    @Test
    public void testLegalTrap(){
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves 
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");
        legalMoves.put("g1", "f3");
        legalMoves.put("b8", "c6");
        legalMoves.put("f1", "c4");
        legalMoves.put("d7", "d6");
        legalMoves.put("b1", "c3");
        legalMoves.put("c8", "g4");
        legalMoves.put("h2", "h3");
        legalMoves.put("g4", "h5");
        legalMoves.put("f3", "e5");
        legalMoves.put("h5", "d1");
        legalMoves.put("c4", "f7");
        legalMoves.put("e8", "e7");
        legalMoves.put("c3", "d5");

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
        assertTrue(newBoard.currentPlayer().isInCheckMate());
    }

    @Test
    public void testSmotheredMate(){
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves 
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");
        legalMoves.put("g1", "e2");
        legalMoves.put("b8", "c6");
        legalMoves.put("b1", "c3");
        legalMoves.put("c6", "d4");
        legalMoves.put("g2", "g3");
        legalMoves.put("d4", "f3");

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
        assertTrue(newBoard.currentPlayer().isInCheckMate());
    }

    @Test
    public void testHippopotamusMate(){
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves 
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");
        legalMoves.put("g1", "e2");
        legalMoves.put("d8", "h4");
        legalMoves.put("b1", "c3");
        legalMoves.put("b8", "c6");
        legalMoves.put("g2", "g3");
        legalMoves.put("h4", "h5");
        legalMoves.put("d2", "d4");
        legalMoves.put("c6", "d4");
        legalMoves.put("c1", "g5");
        legalMoves.put("d4", "f3");

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
        assertTrue(newBoard.currentPlayer().isInCheckMate());
    }

    @Test
    public void testBlackburneShillingGambit(){
        final Board testBoard = Board.createInitialBoard();

        // Create a hashmap of all possible moves 
        Map<String, String> legalMoves = new LinkedHashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        legalMoves.put("e2", "e4");
        legalMoves.put("e7", "e5");
        legalMoves.put("g1", "f3");
        legalMoves.put("b8", "c6");
        legalMoves.put("f1", "c4");
        legalMoves.put("c6", "d4");
        legalMoves.put("f3", "e5");
        legalMoves.put("d8", "g5");
        legalMoves.put("e5", "f7");
        legalMoves.put("g5", "g2");
        legalMoves.put("h1", "f1");
        legalMoves.put("g2", "e4");
        legalMoves.put("c4", "e2");
        legalMoves.put("d4", "f3");

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
        assertTrue(newBoard.currentPlayer().isInCheckMate());
    }

    @Test
    public void testAnastasiaMate(){
        final Builder builder = new Builder();

        // Black board layout 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("h7"), Alliance.BLACK, false, false));
        builder.setPiece(new Rook(BoardUtils.getCoordinateAtPosition("a8"), Alliance.BLACK));
        builder.setPiece(new Rook(BoardUtils.getCoordinateAtPosition("f8"), Alliance.BLACK));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("a7"), Alliance.BLACK));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("b7"), Alliance.BLACK));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("c7"), Alliance.BLACK));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("f7"), Alliance.BLACK));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("g7"), Alliance.BLACK));
        
        // White board layout 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("g1"), Alliance.WHITE, false, false));
        builder.setPiece(new Knight(BoardUtils.getCoordinateAtPosition("e7"), Alliance.WHITE));
        builder.setPiece(new Rook(BoardUtils.getCoordinateAtPosition("d5"), Alliance.WHITE));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("b3"), Alliance.WHITE));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("a2"), Alliance.WHITE));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("f2"), Alliance.WHITE));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("g2"), Alliance.WHITE));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("h2"), Alliance.WHITE));

        // Set current player 
        builder.nextPlayer(Alliance.WHITE);

        final Board testBoard = builder.build();
        
        String sourceCord = "d5";
        String destCord = "h5";
        final Move move = MoveFactory.createMove(testBoard, 
                                                 BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                 BoardUtils.getCoordinateAtPosition(destCord));

        final MoveTransition transition = testBoard.currentPlayer().makeMove(move);
        assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                   transition.getMoveStatus().isDone());
        assertTrue(transition.getTransitionBoard().currentPlayer().isInCheckMate());
    }
}
