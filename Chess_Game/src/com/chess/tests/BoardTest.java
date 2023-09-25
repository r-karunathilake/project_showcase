package com.chess.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Builder;
import com.chess.engine.board.move.MoveFactory;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.Iterables;

public class BoardTest {
    @Test
    public void testCreateInitialBoard() {
        final Board iniBoard = Board.createInitialBoard(); 

        // Make sure white player is the first player
        assertEquals(iniBoard.currentPlayer().getAlliance(), Alliance.WHITE);
        assertEquals(iniBoard.currentPlayer().getOpponent().getAlliance(), Alliance.BLACK);
        
        // Make sure each player has 16 chess pieces
        assertEquals("White player does NOT have 16 chess pieces!", 
                     Iterables.size(iniBoard.getWhitePieces()), 16);
        assertEquals("Black player does NOT have 16 chess pieces!", 
                     Iterables.size(iniBoard.getWhitePieces()), 16); 
        
        // Check the alliance of all the player pieces 
        for(Piece piece : iniBoard.getBlackPieces()){
            Alliance pieceAlliance = piece.getPieceAlliance(); 
            assertTrue("Black " + piece.toString() + " Alliance is " + pieceAlliance, 
                       pieceAlliance == Alliance.BLACK); 
        }

        for(Piece piece : iniBoard.getWhitePieces()){
            Alliance pieceAlliance = piece.getPieceAlliance(); 
            assertTrue("White " + piece.toString() + " Alliance is " + pieceAlliance, 
                       pieceAlliance == Alliance.WHITE); 
        }

        // There are 20 legal moves per player
        assertEquals(iniBoard.currentPlayer().getLegalMoves().size(), 20); 
        assertEquals(iniBoard.currentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertEquals(Iterables.size(iniBoard.getAllLegalMoves()), 40);

        // Check the status of the player and opponent King 
        assertFalse(iniBoard.currentPlayer().isInCheck());
        assertFalse(iniBoard.currentPlayer().isInCheckMate());
        assertFalse(iniBoard.currentPlayer().isCastled());

        assertFalse(iniBoard.currentPlayer().getOpponent().isInCheck());
        assertFalse(iniBoard.currentPlayer().getOpponent().isInCheckMate());
        assertFalse(iniBoard.currentPlayer().getOpponent().isCastled());

        // Check no En Passant pawn on board 
        assertNull("En Passant pawn exists at the start of the chess game!", 
                   iniBoard.getEnPassantPawn());
    }

    @Test
    public void testKingMoves(){
        final Builder builder = new Builder();
        
        // Create white chess piece layout 
        builder.setPiece(new King(59, Alliance.WHITE, false, false));
        builder.setPiece(new Pawn(51, Alliance.WHITE, true));
        builder.nextPlayer(Alliance.WHITE); 

        // Create black chess piece layout 
        builder.setPiece(new King(3, Alliance.BLACK, false, false));
        builder.setPiece(new Pawn(11, Alliance.BLACK, true));
        
        final Board testBoard = builder.build(); 
        // Following chess board was created (black on top)
        // -  -  -  k  -  -  -  -
        // -  -  -  p  -  -  -  -
        // -  -  -  -  -  -  -  -
        // -  -  -  P  -  -  -  -
        // -  -  -  K  -  -  -  -
        //System.out.println(testBoard);

        // Test cases
        // ----------------------------------------------------------
        assertEquals(testBoard.currentPlayer(), testBoard.whitePlayer());
        assertEquals(testBoard.currentPlayer().getOpponent(), testBoard.blackPlayer());

        // White player test cases 
        assertEquals(testBoard.whitePlayer().getLegalMoves().size(), 6);
        assertFalse(testBoard.currentPlayer().isInCheck());
        assertFalse(testBoard.currentPlayer().isInCheckMate());

        // Black player test cases 
        assertEquals(testBoard.blackPlayer().getLegalMoves().size(), 6);
        assertFalse(testBoard.currentPlayer().getOpponent().isInCheck());
        assertFalse(testBoard.currentPlayer().getOpponent().isInCheckMate());
    }

    // Exception thrown by the Player Class
    @Test(expected=RuntimeException.class) 
    public void testInvalidBoard(){
        // Missing King piece 

        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));
        // White Layout
        builder.setPiece(new Pawn(48, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));
        //white to move
        builder.nextPlayer(Alliance.WHITE);
        //build the board
        builder.build();
    }

    @Test
    public void testAlgebraicNotation() {
        // Eighth rank check 
        assertEquals(BoardUtils.getPositionAtCoordinate(0), "a8");
        assertEquals(BoardUtils.getPositionAtCoordinate(1), "b8");
        assertEquals(BoardUtils.getPositionAtCoordinate(2), "c8");
        assertEquals(BoardUtils.getPositionAtCoordinate(3), "d8");
        assertEquals(BoardUtils.getPositionAtCoordinate(4), "e8");
        assertEquals(BoardUtils.getPositionAtCoordinate(5), "f8");
        assertEquals(BoardUtils.getPositionAtCoordinate(6), "g8");
        assertEquals(BoardUtils.getPositionAtCoordinate(7), "h8");

        // First rank check
        assertEquals(BoardUtils.getPositionAtCoordinate(56), "a1");
        assertEquals(BoardUtils.getPositionAtCoordinate(57), "b1");
        assertEquals(BoardUtils.getPositionAtCoordinate(58), "c1");
        assertEquals(BoardUtils.getPositionAtCoordinate(59), "d1");
        assertEquals(BoardUtils.getPositionAtCoordinate(60), "e1");
        assertEquals(BoardUtils.getPositionAtCoordinate(61), "f1");
        assertEquals(BoardUtils.getPositionAtCoordinate(62), "g1");
        assertEquals(BoardUtils.getPositionAtCoordinate(63), "h1");
    }

    @Test
    public void testBoardState() {
        final Board board = Board.createInitialBoard();
        assertEquals(board.currentPlayer(), board.whitePlayer());

        // Move sequence
        // ----------------------------------------------------------

        // Move white pawn on E2 to E4
        final MoveTransition t1 = board.currentPlayer().makeMove(MoveFactory.createMove(board, 
                                                                                        BoardUtils.getCoordinateAtPosition("e2"), 
                                                                                        BoardUtils.getCoordinateAtPosition("e4"))); 

        // Move black pawn on E7 to E5
        final MoveTransition t2 = t1.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t1.getTransitionBoard(),                                                          
                                                                                   BoardUtils.getCoordinateAtPosition("e7"), 
                                                                                   BoardUtils.getCoordinateAtPosition("e5")));

        // Move white knight on G1 to F3
        final MoveTransition t3 = t2.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t2.getTransitionBoard(), 
                                                                                   BoardUtils.getCoordinateAtPosition("g1"),
                                                                                   BoardUtils.getCoordinateAtPosition("f3")));
        
        // Move black pawn on D7 to D5
        final MoveTransition t4 = t3.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t3.getTransitionBoard(), 
                                                                                   BoardUtils.getCoordinateAtPosition("d7"),
                                                                                   BoardUtils.getCoordinateAtPosition("d5")));
        
        // Move white pawn on E4 to D5 (white takes black pawn on D5)
        final MoveTransition t5 = t4.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t4.getTransitionBoard(), 
                                                                                   BoardUtils.getCoordinateAtPosition("e4"),
                                                                                   BoardUtils.getCoordinateAtPosition("d5")));
        
        // Move black queen on D8 to D5 (black queen takes white pawn on D5)
        final MoveTransition t6 = t5.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t5.getTransitionBoard(), 
                                                                                   BoardUtils.getCoordinateAtPosition("d8"),
                                                                                   BoardUtils.getCoordinateAtPosition("d5")));
        
        // Move white knight on F3 to G5
        final MoveTransition t7 = t6.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t6.getTransitionBoard(), 
                                                                                   BoardUtils.getCoordinateAtPosition("f3"), 
                                                                                   BoardUtils.getCoordinateAtPosition("g5")));

        // Move black pawn on F7 to F6
        final MoveTransition t8 = t7.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t7.getTransitionBoard(), 
                                                                                   BoardUtils.getCoordinateAtPosition("f7"),
                                                                                   BoardUtils.getCoordinateAtPosition("f6")));
        
        // Move white queen on D1 to H5
        final MoveTransition t9 = t8.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t8.getTransitionBoard(), 
                                                                                   BoardUtils.getCoordinateAtPosition("d1"),
                                                                                   BoardUtils.getCoordinateAtPosition("h5")));

        // Move black pawn on G7 to G6
        final MoveTransition t10 = t9.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t9.getTransitionBoard(), 
                                                                                    BoardUtils.getCoordinateAtPosition("g7"),
                                                                                    BoardUtils.getCoordinateAtPosition("g6")));

        // Move white queen on H5 to H4
        final MoveTransition t11 = t10.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t10.getTransitionBoard(), 
                                                                                     BoardUtils.getCoordinateAtPosition("h5"),
                                                                                     BoardUtils.getCoordinateAtPosition("h4")));

        // Move black pawn on F6 to G5 (black pawn takes white knight)
        final MoveTransition t12 = t11.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t11.getTransitionBoard(), 
                                                                                     BoardUtils.getCoordinateAtPosition("f6"),
                                                                                     BoardUtils.getCoordinateAtPosition("g5")));
                                                                            
        // Move white queen on H4 to G5 (white queen takes black pawn)
        final MoveTransition t13 = t12.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t12.getTransitionBoard(), 
                                                                                     BoardUtils.getCoordinateAtPosition("h4"), 
                                                                                     BoardUtils.getCoordinateAtPosition("g5")));

        // Move black queen on D5 to E4
        final MoveTransition t14 = t13.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t13.getTransitionBoard(), 
                                                                                     BoardUtils.getCoordinateAtPosition("d5"), 
                                                                                     BoardUtils.getCoordinateAtPosition("e4")));

        assertTrue(t14.getTransitionBoard().whitePlayer().getActivePieces().size() == calculatedActivesFor(t14.getTransitionBoard(), Alliance.WHITE));
        assertTrue(t14.getTransitionBoard().blackPlayer().getActivePieces().size() == calculatedActivesFor(t14.getTransitionBoard(), Alliance.BLACK));

    }

    private static int calculatedActivesFor(final Board board, final Alliance alliance) {
        int count = 0;
        for (final Piece piece : board.getAllPieces()) {
            if (piece.getPieceAlliance().equals(alliance)) {
                count++;
            }
        }
        return count;
    }
}
