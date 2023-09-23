package com.chess.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.Iterables;

public class TestBoard {
    final Board iniBoard = Board.createInitialBoard(); 

    @Test
    public void testCreateInitialBoard() {
        // Make sure white player is the first player
        assertEquals(this.iniBoard.currentPlayer().getAlliance(), Alliance.WHITE);
        assertEquals(this.iniBoard.currentPlayer().getOpponent().getAlliance(), Alliance.BLACK);
        
        // Make sure each player has 16 chess pieces
        assertEquals("White player does NOT have 16 chess pieces!", 
                     Iterables.size(this.iniBoard.getWhitePieces()), 16);
        assertEquals("Black player does NOT have 16 chess pieces!", 
                     Iterables.size(this.iniBoard.getWhitePieces()), 16); 
        
        // Check the alliance of all the player pieces 
        for(Piece piece : this.iniBoard.getBlackPieces()){
            Alliance pieceAlliance = piece.getPieceAlliance(); 
            assertTrue("Black " + piece.toString() + " Alliance is " + pieceAlliance, 
                       pieceAlliance == Alliance.BLACK); 
        }

        for(Piece piece : this.iniBoard.getWhitePieces()){
            Alliance pieceAlliance = piece.getPieceAlliance(); 
            assertTrue("White " + piece.toString() + " Alliance is " + pieceAlliance, 
                       pieceAlliance == Alliance.WHITE); 
        }

        // There are 20 legal moves per player
        assertEquals(this.iniBoard.currentPlayer().getLegalMoves().size(), 20); 
        assertEquals(this.iniBoard.currentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertEquals(Iterables.size(this.iniBoard.getAllLegalMoves()), 40);

        // Check the status of the player and opponent King 
        assertFalse(this.iniBoard.currentPlayer().isInCheck());
        assertFalse(this.iniBoard.currentPlayer().isInCheckMate());
        assertFalse(this.iniBoard.currentPlayer().isCastled());

        assertFalse(this.iniBoard.currentPlayer().getOpponent().isInCheck());
        assertFalse(this.iniBoard.currentPlayer().getOpponent().isInCheckMate());
        assertFalse(this.iniBoard.currentPlayer().getOpponent().isCastled());

        // Check no En Passant pawn on board 
        assertNull("En Passant pawn exists at the start of the chess game!", 
                   this.iniBoard.getEnPassantPawn());
    }
}
