package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator{
    private static final int CHECK_SCORE = 50; // Half a pawn score
    private static final int CHECK_MATE_SCORE = 10000;
    private static final int DEPTH_SCORE = 100;
    private static final int CASTLE_SCORE = 60; 

    @Override
    public int evaluate(final Board board, int depth){
        return totalBoardScore(board, depth);
    }

    private static int totalBoardScore(Board board, int depth) {
        return whitePlayerScore(board.whitePlayer(), depth) 
               - blackPlayerScore(board.blackPlayer(), depth);
    }   

    private static int blackPlayerScore(final Player blackPlayer, 
                                        int depth) {
        return pieceValue(blackPlayer) 
               + mobility(blackPlayer) 
               + check(blackPlayer)
               + checkMate(blackPlayer, depth)
               + castled(blackPlayer); 
    }

    private static int whitePlayerScore(final Player whitePlayer, 
                                        int depth){
        return pieceValue(whitePlayer) 
               + mobility(whitePlayer)
               + check(whitePlayer)
               + checkMate(whitePlayer, depth)
               + castled(whitePlayer);
    }

    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_SCORE : 0;
    }
    
    private static int checkMate(Player player, int depth) {
        return player.getOpponent().isInCheck() ? CHECK_MATE_SCORE * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_SCORE * depth; 
    }

    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_SCORE : 0;
    }

    private static int pieceValue(final Player player){
        int pieceValueScore = 0;
        for(final Piece piece : player.getActivePieces()){
            pieceValueScore += piece.getPieceValue(); 
        }
        return pieceValueScore; 
    }

    private static int mobility(final Player player){
        return player.getLegalMoves().size();
    }
}
