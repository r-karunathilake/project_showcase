package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.move.Move;
import com.chess.engine.player.MoveTransition;

public class MiniMax implements MoveStrategy{

    private final BoardEvaluator boardEvaluator;
    private final int minimaxDepth; 

    public MiniMax(int depth){
        this.boardEvaluator = new StandardBoardEvaluator();
        this.minimaxDepth = depth; 
    }

    @Override
    public String toString(){
        return "MiniMax"; 
    }

    @Override
    public Move execute(final Board board) {
        final long startTime = System.currentTimeMillis(); // current time in ms

        Move bestMove = null;
        int bestWhiteOutcome = Integer.MIN_VALUE;
        int bestBlackOutcome = Integer.MAX_VALUE; 
        int currentOutcome;

        System.out.println(board.currentPlayer() + " CALCULATING MOVES with depth = " 
                           + this.minimaxDepth);

        for(final Move move : board.currentPlayer().getLegalMoves()){
            // Make the first move, doesn't matter if WHITE or BLACK 
            final MoveTransition transition = board.currentPlayer().makeMove(move);
            
            if(transition.getMoveStatus().isDone()){
                // If WHITE (Maximize) just moved, the next player is 
                // BLACK (Minimize)
                Board newBoard = transition.getTransitionBoard();
                currentOutcome = board.currentPlayer()
                                      .getAlliance()
                                      .isWhite() ? min(newBoard, this.minimaxDepth - 1) : max(newBoard, this.minimaxDepth - 1);
                 
                if(board.currentPlayer().getAlliance().isWhite() 
                   && currentOutcome >= bestWhiteOutcome){

                    bestWhiteOutcome = currentOutcome;
                    bestMove = move;
                }
                else if(board.currentPlayer().getAlliance().isBlack() 
                        && currentOutcome <= bestBlackOutcome){

                    bestBlackOutcome = currentOutcome;
                    bestMove = move;
                }
            }
        }

        final long executionTime = System.currentTimeMillis() - startTime; 

        return bestMove; 
    }

    public int min(final Board board, int depth){
        if(depth == 0 || isEndGame(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }

        int lowestOutcome = Integer.MAX_VALUE;
        // Loop through all the legal moves and evaluate the smallest outcome 
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition transition = board.currentPlayer().makeMove(move);
            if(transition.getMoveStatus().isDone()){
                final int currentOutcome = max(transition.getTransitionBoard(),
                                               depth -1); 
                if(currentOutcome <= lowestOutcome){
                    lowestOutcome = currentOutcome; 
                }
            }
        }
        return lowestOutcome; 
    }

    public int max(final Board board, int depth){
        if(depth == 0 || isEndGame(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }

        int highestOutcome = Integer.MIN_VALUE;
        // Loop through all the legal moves and evaluate the largest outcome 
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition transition = board.currentPlayer().makeMove(move);
            if(transition.getMoveStatus().isDone()){
                final int currentOutcome = min(transition.getTransitionBoard(),
                                               depth -1); 
                if(currentOutcome >= highestOutcome){
                    highestOutcome = currentOutcome; 
                }
            }
        }
        return highestOutcome; 
    }

    private static boolean isEndGame(final Board board){
        return board.currentPlayer().isInCheckMate() 
               || board.currentPlayer().isInStaleMate();
    }
}
