package com.chess.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.chess.engine.Alliance;
import com.chess.engine.PieceType;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Builder;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.MoveFactory;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
import com.chess.engine.player.MoveTransition;

public class PieceTest {
    @Test
    public void testWhiteQueenOnEmptyBoard() {
        final Builder builder = new Builder();

        // Need to add King for both sides to create a valid board 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e1"), Alliance.WHITE, true));
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e8"), Alliance.BLACK, true));

        // Add white queen
        builder.setPiece(new Queen(BoardUtils.getCoordinateAtPosition("e4"), Alliance.WHITE, true));

        // Set current player 
        builder.nextPlayer(Alliance.WHITE);

        final Board testBoard = builder.build();

        final Collection<Move> whiteLegalMoves = testBoard.whitePlayer().getLegalMoves(); 
        final Collection<Move> blackLegalMoves = testBoard.blackPlayer().getLegalMoves();
    
        assertEquals(whiteLegalMoves.size(), 26 + 5);
        assertEquals(blackLegalMoves.size(), 5);

        // Create a hashmap of all possible moves for the white queen
        Map<String, String> whiteQueenLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------

        // Vertical moves 
        whiteQueenLegalMoves.put("e4", "e8");
        whiteQueenLegalMoves.put("e4", "e7");
        whiteQueenLegalMoves.put("e4", "e6");
        whiteQueenLegalMoves.put("e4", "e5");
        whiteQueenLegalMoves.put("e4", "e3");
        whiteQueenLegalMoves.put("e4", "e2");

        // Horizontal moves 
        whiteQueenLegalMoves.put("e4", "a4");
        whiteQueenLegalMoves.put("e4", "b4");
        whiteQueenLegalMoves.put("e4", "c4");
        whiteQueenLegalMoves.put("e4", "d4");
        whiteQueenLegalMoves.put("e4", "f4");
        whiteQueenLegalMoves.put("e4", "g4");
        whiteQueenLegalMoves.put("e4", "h4");

        // Diagonal moves
        whiteQueenLegalMoves.put("e4", "f5");
        whiteQueenLegalMoves.put("e4", "g6");
        whiteQueenLegalMoves.put("e4", "h7");

        whiteQueenLegalMoves.put("e4", "d5");
        whiteQueenLegalMoves.put("e4", "c6"); 
        whiteQueenLegalMoves.put("e4", "b7");    
        whiteQueenLegalMoves.put("e4", "a8");
        
        whiteQueenLegalMoves.put("e4", "d3"); 
        whiteQueenLegalMoves.put("e4", "c2");
        whiteQueenLegalMoves.put("e4", "b1"); 
        
        whiteQueenLegalMoves.put("e4", "f3"); 
        whiteQueenLegalMoves.put("e4", "g2"); 
        whiteQueenLegalMoves.put("e4", "h1"); 

        for(Map.Entry<String, String> move : whiteQueenLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       whiteLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }
    }

    @Test
    public void testBlackQueenOnEmptyBoard() {
        final Builder builder = new Builder();

        // Need to add King for both sides to create a valid board 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e1"), Alliance.WHITE, true));
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e8"), Alliance.BLACK, true));

        // Add black queen
        builder.setPiece(new Queen(BoardUtils.getCoordinateAtPosition("e5"), Alliance.BLACK, true));

        // Set current player 
        builder.nextPlayer(Alliance.BLACK);

        final Board testBoard = builder.build();

        final Collection<Move> whiteLegalMoves = testBoard.whitePlayer().getLegalMoves(); 
        final Collection<Move> blackLegalMoves = testBoard.blackPlayer().getLegalMoves();
    
        assertEquals(whiteLegalMoves.size(), 5);
        assertEquals(blackLegalMoves.size(), 26 + 5);

        // Create a hashmap of all possible moves for the white queen
        Map<String, String> blackQueenLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------

        // Vertical moves 
        blackQueenLegalMoves.put("e5", "e7");
        blackQueenLegalMoves.put("e5", "e6");
        blackQueenLegalMoves.put("e5", "e4");
        blackQueenLegalMoves.put("e5", "e3");
        blackQueenLegalMoves.put("e5", "e2");
        blackQueenLegalMoves.put("e5", "e1");

        // Horizontal moves 
        blackQueenLegalMoves.put("e5", "a5");
        blackQueenLegalMoves.put("e5", "b5");
        blackQueenLegalMoves.put("e5", "c5");
        blackQueenLegalMoves.put("e5", "d5");
        blackQueenLegalMoves.put("e5", "f5");
        blackQueenLegalMoves.put("e5", "g5");
        blackQueenLegalMoves.put("e5", "h5");

        // Diagonal moves
        blackQueenLegalMoves.put("e5", "f6");
        blackQueenLegalMoves.put("e5", "g7");
        blackQueenLegalMoves.put("e5", "h8");

        blackQueenLegalMoves.put("e5", "d6");
        blackQueenLegalMoves.put("e5", "c7"); 
        blackQueenLegalMoves.put("e5", "b8");    
        
        blackQueenLegalMoves.put("e5", "d4"); 
        blackQueenLegalMoves.put("e5", "c3");
        blackQueenLegalMoves.put("e5", "b2");
        blackQueenLegalMoves.put("e5", "a1");  
        
        blackQueenLegalMoves.put("e5", "f4"); 
        blackQueenLegalMoves.put("e5", "g3"); 
        blackQueenLegalMoves.put("e5", "h2"); 

        for(Map.Entry<String, String> move : blackQueenLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       blackLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }
    }

    @Test
    public void testKnightOnEmptyBoard() {
        final Builder builder = new Builder(); 

        // Need to add King for both sides to create a valid board 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e1"), Alliance.WHITE, true));
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e8"), Alliance.BLACK, true));

        // Add white knight
        builder.setPiece(new Knight(BoardUtils.getCoordinateAtPosition("e4"), Alliance.WHITE, true));

        // Add black knight
        builder.setPiece(new Knight(BoardUtils.getCoordinateAtPosition("e5"), Alliance.BLACK, true));

        // Set current player 
        builder.nextPlayer(Alliance.WHITE);

        final Board testBoard = builder.build();

        final Collection<Move> whiteLegalMoves = testBoard.whitePlayer().getLegalMoves(); 
        final Collection<Move> blackLegalMoves = testBoard.blackPlayer().getLegalMoves();
    
        assertEquals(whiteLegalMoves.size(), 8 + 5);
        assertEquals(blackLegalMoves.size(), 8 + 5);

        // Create a hashmap of all possible moves for the white knight
        Map<String, String> whiteKnightLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        whiteKnightLegalMoves.put("e4", "d6");
        whiteKnightLegalMoves.put("e4", "f6");
        whiteKnightLegalMoves.put("e4", "c5");
        whiteKnightLegalMoves.put("e4", "g5");
        whiteKnightLegalMoves.put("e4", "c3");
        whiteKnightLegalMoves.put("e4", "g3");
        whiteKnightLegalMoves.put("e4", "d2");
        whiteKnightLegalMoves.put("e4", "f2");

        for(Map.Entry<String, String> move : whiteKnightLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       whiteLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }

        // Create a hashmap of all possible moves for the black knight
        Map<String, String> blackKnightLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        blackKnightLegalMoves.put("e5", "d7");
        blackKnightLegalMoves.put("e5", "f7");
        blackKnightLegalMoves.put("e5", "c6");
        blackKnightLegalMoves.put("e5", "g6");
        blackKnightLegalMoves.put("e5", "c4");
        blackKnightLegalMoves.put("e5", "g4");
        blackKnightLegalMoves.put("e5", "d3");
        blackKnightLegalMoves.put("e5", "f3");

        for(Map.Entry<String, String> move : blackKnightLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       blackLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }
    }

    @Test
    public void testKnightFirstFileEdgeCases() {
        final Builder builder = new Builder();

        // Need to add King for both sides to create a valid board 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e1"), Alliance.WHITE, true));
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e8"), Alliance.BLACK, true));

        // Add knights on the first rank
        builder.setPiece(new Knight(BoardUtils.getCoordinateAtPosition("a1"), Alliance.WHITE, true));
        builder.setPiece(new Knight(BoardUtils.getCoordinateAtPosition("a8"), Alliance.BLACK, true));

        // Set current player 
        builder.nextPlayer(Alliance.WHITE);
        final Board testBoard = builder.build();

        final Collection<Move> whiteLegalMoves = testBoard.whitePlayer().getLegalMoves(); 
        final Collection<Move> blackLegalMoves = testBoard.blackPlayer().getLegalMoves();
    
        assertEquals(whiteLegalMoves.size(), 2 + 5);
        assertEquals(blackLegalMoves.size(), 2 + 5);

        // Create a hashmap of all possible moves for the white knight
        Map<String, String> whiteKnightLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        whiteKnightLegalMoves.put("a1", "b3");
        whiteKnightLegalMoves.put("a1", "c2");

        for(Map.Entry<String, String> move : whiteKnightLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       whiteLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }

        // Create a hashmap of all possible moves for the black knight
        Map<String, String> blackKnightLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        blackKnightLegalMoves.put("a8", "b6");
        blackKnightLegalMoves.put("a8", "c7");

        for(Map.Entry<String, String> move : blackKnightLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       blackLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }
    }

    @Test
    public void testKnightSecondFileEdgeCases() {
        final Builder builder = new Builder();

        // Need to add King for both sides to create a valid board 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e1"), Alliance.WHITE, true));
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e8"), Alliance.BLACK, true));

        // Add knights on the first rank
        builder.setPiece(new Knight(BoardUtils.getCoordinateAtPosition("b1"), Alliance.WHITE, true));
        builder.setPiece(new Knight(BoardUtils.getCoordinateAtPosition("b8"), Alliance.BLACK, true));

        // Set current player 
        builder.nextPlayer(Alliance.WHITE);
        final Board testBoard = builder.build();

        final Collection<Move> whiteLegalMoves = testBoard.whitePlayer().getLegalMoves(); 
        final Collection<Move> blackLegalMoves = testBoard.blackPlayer().getLegalMoves();
    
        assertEquals(whiteLegalMoves.size(), 3 + 5);
        assertEquals(blackLegalMoves.size(), 3 + 5);

        // Create a hashmap of all possible moves for the white knight
        Map<String, String> whiteKnightLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        whiteKnightLegalMoves.put("b1", "a3");
        whiteKnightLegalMoves.put("b1", "c3");
        whiteKnightLegalMoves.put("b1", "d2");

        for(Map.Entry<String, String> move : whiteKnightLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       whiteLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }

        // Create a hashmap of all possible moves for the black knight
        Map<String, String> blackKnightLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        blackKnightLegalMoves.put("b8", "c6");
        blackKnightLegalMoves.put("b8", "d7");
        blackKnightLegalMoves.put("b8", "a6");

        for(Map.Entry<String, String> move : blackKnightLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       blackLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }
    }

    @Test
    public void testBishopOnEmptyBoard() {
        final Builder builder = new Builder();

        // Need to add King for both sides to create a valid board 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e1"), Alliance.WHITE, true));
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e8"), Alliance.BLACK, true));

        // Add white bishop
        builder.setPiece(new Bishop(BoardUtils.getCoordinateAtPosition("d5"), Alliance.WHITE, true));

        // Set current player 
        builder.nextPlayer(Alliance.WHITE);

        final Board testBoard = builder.build();

        final Collection<Move> whiteLegalMoves = testBoard.whitePlayer().getLegalMoves(); 
        final Collection<Move> blackLegalMoves = testBoard.blackPlayer().getLegalMoves();
    
        assertEquals(whiteLegalMoves.size(), 13 + 5);
        assertEquals(blackLegalMoves.size(), 5);

        // Create a hashmap of all possible moves for the white knight
        Map<String, String> whiteBishopLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        whiteBishopLegalMoves.put("d5", "c6");
        whiteBishopLegalMoves.put("d5", "b1");
        whiteBishopLegalMoves.put("d5", "a8");
        whiteBishopLegalMoves.put("d5", "e6");
        whiteBishopLegalMoves.put("d5", "f7");
        whiteBishopLegalMoves.put("d5", "g8");
        whiteBishopLegalMoves.put("d5", "c4");
        whiteBishopLegalMoves.put("d5", "b3");
        whiteBishopLegalMoves.put("d5", "a2");
        whiteBishopLegalMoves.put("d5", "e4");
        whiteBishopLegalMoves.put("d5", "f3");
        whiteBishopLegalMoves.put("d5", "g2");
        whiteBishopLegalMoves.put("d5", "h1");

        for(Map.Entry<String, String> move : whiteBishopLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       whiteLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }
    }
    
    @Test
    public void testBishopFirstFileEdgeCases() {
        final Builder builder = new Builder();

        // Need to add King for both sides to create a valid board 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e1"), Alliance.WHITE, true));
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e8"), Alliance.BLACK, true));

        // Add white bishop
        builder.setPiece(new Bishop(BoardUtils.getCoordinateAtPosition("a1"), Alliance.WHITE, true));

        // Set current player 
        builder.nextPlayer(Alliance.WHITE);

        final Board testBoard = builder.build();

        final Collection<Move> whiteLegalMoves = testBoard.whitePlayer().getLegalMoves(); 
        final Collection<Move> blackLegalMoves = testBoard.blackPlayer().getLegalMoves();
    
        assertEquals(whiteLegalMoves.size(), 7 + 5);
        assertEquals(blackLegalMoves.size(), 5);

        // Create a hashmap of all possible moves for the white knight
        Map<String, String> whiteBishopLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        whiteBishopLegalMoves.put("a1", "b2");
        whiteBishopLegalMoves.put("a1", "c3");
        whiteBishopLegalMoves.put("a1", "d4");
        whiteBishopLegalMoves.put("a1", "e5");
        whiteBishopLegalMoves.put("a1", "f6");
        whiteBishopLegalMoves.put("a1", "g7");
        whiteBishopLegalMoves.put("a1", "h8");

        for(Map.Entry<String, String> move : whiteBishopLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       whiteLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }
    }

    @Test
    public void testRookOnEmptyBoard() {
        final Builder builder = new Builder();

        // Need to add King for both sides to create a valid board 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e1"), Alliance.WHITE, true));
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e8"), Alliance.BLACK, true));

        // Add white rook
        builder.setPiece(new Rook(BoardUtils.getCoordinateAtPosition("e4"), Alliance.WHITE, true));

        // Set current player 
        builder.nextPlayer(Alliance.WHITE);

        final Board testBoard = builder.build();

        final Collection<Move> whiteLegalMoves = testBoard.whitePlayer().getLegalMoves(); 
        final Collection<Move> blackLegalMoves = testBoard.blackPlayer().getLegalMoves();
    
        assertEquals(whiteLegalMoves.size(), 13 + 5);
        assertEquals(blackLegalMoves.size(), 5);
    
        // Create a hashmap of all possible moves for the white knight
        Map<String, String> whiteBishopLegalMoves = new HashMap<>(); 

        // Add moves (source coordinate: destination coordinate) to HashMap
        //---------------------------------------------------------------
        whiteBishopLegalMoves.put("e4", "e8");
        whiteBishopLegalMoves.put("e4", "e7");
        whiteBishopLegalMoves.put("e4", "e6");
        whiteBishopLegalMoves.put("e4", "e5");
        whiteBishopLegalMoves.put("e4", "e3");
        whiteBishopLegalMoves.put("e4", "e2");
        whiteBishopLegalMoves.put("e4", "a4");
        whiteBishopLegalMoves.put("e4", "b4");
        whiteBishopLegalMoves.put("e4", "c4");
        whiteBishopLegalMoves.put("e4", "d4");
        whiteBishopLegalMoves.put("e4", "f4");
        whiteBishopLegalMoves.put("e4", "g4");
        whiteBishopLegalMoves.put("e4", "h4");

        for(Map.Entry<String, String> move : whiteBishopLegalMoves.entrySet()){
            String sourceCord = move.getKey();
            String destCord = move.getValue();

            assertTrue("Move from " + sourceCord + " to " + destCord + " NOT found in legal moves!", 
                       whiteLegalMoves.contains(MoveFactory.createMove(testBoard, 
                                                                       BoardUtils.getCoordinateAtPosition(sourceCord), 
                                                                       BoardUtils.getCoordinateAtPosition(destCord))));
        }
    }

    @Test
    public void testPawnPromotion(){
        final Builder builder = new Builder();

        // Need to add King for both sides to create a valid board 
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e2"), Alliance.WHITE, true));
        builder.setPiece(new King(BoardUtils.getCoordinateAtPosition("e7"), Alliance.BLACK, true));

        // Add white and black pawn
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("h7"), Alliance.WHITE, false));
        builder.setPiece(new Pawn(BoardUtils.getCoordinateAtPosition("h2"), Alliance.BLACK, false));

        // Set current player 
        builder.nextPlayer(Alliance.WHITE);

        final Board testBoard = builder.build();

        final Collection<Move> whiteLegalMoves = testBoard.whitePlayer().getLegalMoves(); 
        final Collection<Move> blackLegalMoves = testBoard.blackPlayer().getLegalMoves();
    
        assertEquals(whiteLegalMoves.size(), 1 + 8);
        assertEquals(blackLegalMoves.size(), 1 + 8);

        // White pawn promotion move 
        final Move whitePawnMove = MoveFactory.createMove(testBoard, 
                                                      BoardUtils.getCoordinateAtPosition("h7"), 
                                                      BoardUtils.getCoordinateAtPosition("h8"));
        
        // Execute move
        MoveTransition transitionWhite = testBoard.currentPlayer().makeMove(whitePawnMove);
        assertTrue(transitionWhite.getMoveStatus().isDone());
        
        // Black pawn promotion move 
        final Board newTestBoard = transitionWhite.getTransitionBoard(); // Update the board after white pawn move 
        final Move blackPawnMove = MoveFactory.createMove(newTestBoard, 
                                                          BoardUtils.getCoordinateAtPosition("h2"), 
                                                          BoardUtils.getCoordinateAtPosition("h1"));
        MoveTransition transitionBlack = transitionWhite.getTransitionBoard().currentPlayer().makeMove(blackPawnMove);
        assertTrue(transitionBlack.getMoveStatus().isDone());

        // Check that both pieces were promoted
        assertEquals(transitionBlack.getTransitionBoard().getTile(BoardUtils.getCoordinateAtPosition("h8")).getPiece().getPieceType(), PieceType.QUEEN);
        assertEquals(transitionBlack.getTransitionBoard().getTile(BoardUtils.getCoordinateAtPosition("h1")).getPiece().getPieceType(), PieceType.QUEEN);
    }
}