package com.chess.pgn;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Pawn;

public class FenUtils {
    
    private FenUtils(){
        throw new RuntimeException("You cannot instantiate me!"); 
    }

    public static Board createGameFromFEN(final String fenString){
        return null;
    }

    public static String createFENFromGame(final Board board){
        return calculateBoardText(board) + " " + calculateCurrentPlayerText(board) 
               + " " + calculateCastleText(board) + " " 
               + calculateEnPassantSquare(board) + " 0 1";
    }

    private static String calculateBoardText(final Board board) {
        final StringBuilder builder = new StringBuilder();
        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            final String tileText = board.getTile(i).toString();
            builder.append(tileText);

            if((i+1) % 8 == 0 && i < 63){
                builder.append("/");
            }
        }
        String boardString = builder.toString()
                                    .replaceAll("--------", "8")
                                    .replaceAll("-------", "7")
                                    .replaceAll("------", "6")
                                    .replaceAll("-----", "5")
                                    .replaceAll("----", "4")
                                    .replaceAll("---", "3")
                                    .replaceAll("--", "2")
                                    .replaceAll("-", "1");
        
        return boardString;
    }

    private static String calculateEnPassantSquare(final Board board) {
        final Pawn enPassantPawn = board.getEnPassantPawn(); 

        if(enPassantPawn != null){
            // The tile position behind the en passant pawn 
            return BoardUtils.getPositionAtCoordinate(enPassantPawn.getPiecePosition() 
                                                      + (8 * enPassantPawn.getPieceAlliance().getOppositeDirection()));
        }

        return "-";
    }

    private static String calculateCastleText(final Board board) {
        final StringBuilder builder = new StringBuilder();

        if(board.whitePlayer().isKingSideCastleAllowed()){
            builder.append("K");
        }
        if(board.whitePlayer().isQueenSideCastleAllowed()){
            builder.append("Q");
        }

        if(board.blackPlayer().isKingSideCastleAllowed()){
            builder.append("k");
        }
        if(board.blackPlayer().isQueenSideCastleAllowed()){
            builder.append("q");
        }

        final String castleString = builder.toString();
        return castleString.isEmpty() ? "-" : castleString;
    }

    private static String calculateCurrentPlayerText(final Board board) {
        return board.currentPlayer().toString().substring(0, 1).toLowerCase(); 
    }  
}
