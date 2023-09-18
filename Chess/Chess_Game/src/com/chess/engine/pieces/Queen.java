package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.board.move.AttackMove;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.NormalMove;
import com.google.common.collect.ImmutableList;

public class Queen extends Piece{

    // For a queen, these are all the coordinate vectors for a given
    // queen position with the largest degree of freedom (on a 8x8 tile board).
    private final static int[] CANDIDATE_MOVE_VECTOR_OFFSETS = {-9, -8, -7, -1, 1, 7, 8, 9};

    Queen(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>(); 

        for(final int currentCandidateOffset:CANDIDATE_MOVE_VECTOR_OFFSETS){
            // Apply the offset to the current position of the queen 
            int candidateDestinationCoordinate = this.piecePosition;

            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                // Update the candidate move coordinate position since
                // the queen is inside the chess board.
                candidateDestinationCoordinate += currentCandidateOffset;

                // If the candidate position is inside the board 
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                    // Edge cases
                    if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) 
                       || isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                        // This is not a valid candidate position when the queen is
                        // in any of these column on the chess board. 
                        continue;
                    }
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                    // If the tile is not occupied, it a legal move
                    if(!candidateDestinationTile.isTileOccupied()){
                        legalMoves.add(new NormalMove(board, this, candidateDestinationCoordinate));
                    }
                    // There is another piece at the candidate location 
                    else{
                        final Piece pieceAtCandidateDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtCandidateDestination.getPieceAlliance(); 
                        
                        // If the queen alliance is NOT equal to the piece at candidate location,
                        // found an enemy piece.  
                        if(this.pieceAlliance != pieceAlliance){
                            legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate,
                                                            pieceAtCandidateDestination));
                        }
                        // Break out of the while-loop as there are no more moves 
                        // at this direction vector since the path is blocked by 
                        // another piece. Note: this break occurs irrespective of the 
                        // blocking piece allegiance.  
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static boolean isEighthColumnExclusion(int currentPos, int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPos] && (candidateOffset == -7 || candidateOffset == 9 || candidateOffset == 1);
    }

    private static boolean isFirstColumnExclusion(int currentPos, int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPos] && (candidateOffset == 7 || candidateOffset == -9 || candidateOffset == -1);
    }
}