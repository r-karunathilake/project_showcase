package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.PieceType;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.board.move.MajorAttackMove;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.NormalMove;
import com.google.common.collect.ImmutableList;

public class Rook extends Piece{
    // For a rook, these are all the coordinate vectors for a given
    // rook position with the largest degree of freedom (on a 8x8 tile board).
    private final static int[] CANDIDATE_MOVE_VECTOR_OFFSETS = {-8, -1, 1, 8};

    // Rook constructor 
    public Rook(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.ROOK, piecePosition, pieceAlliance, true);
    }

    // Rook constructor override 
    public Rook(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove){
    
        super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public String toString(){
        return PieceType.ROOK.toString();
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>(); 

        for(final int currentCandidateOffset:CANDIDATE_MOVE_VECTOR_OFFSETS){
            // Apply the offset to the current position of the rook 
            int candidateDestinationCoordinate = this.piecePosition;

            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                // Edge cases
                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) 
                    || isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                    // This is not a valid candidate position when the rook is
                    // in any of these column on the chess board. 
                    break;
                }

                // Update the candidate move coordinate position since
                // the rook is inside the chess board.
                candidateDestinationCoordinate += currentCandidateOffset;

                // If the candidate position is inside the board 
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                    // If the tile is not occupied, it a legal move
                    if(!candidateDestinationTile.isTileOccupied()){
                        legalMoves.add(new NormalMove(board, this, candidateDestinationCoordinate));
                        // We need to stop looking for legal moves horizontally when we reach
                        // the edge of the chess board. 
                        if((currentCandidateOffset == 1 || currentCandidateOffset == -1) && 
                           (BoardUtils.FIRST_FILE[candidateDestinationCoordinate] || 
                            BoardUtils.EIGHTH_FILE[candidateDestinationCoordinate])){
                            break; 
                        }
                    }
                    // There is another piece at the candidate location 
                    else{
                        final Piece pieceAtCandidateDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtCandidateDestination.getPieceAlliance(); 
                        
                        // If the rook alliance is NOT equal to the piece at candidate location,
                        // found an enemy piece.  
                        if(this.pieceAlliance != pieceAlliance){
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate,
                                                               pieceAtCandidateDestination));
                            // We need to stop looking for legal moves horizontally when we reach
                            // the edge of the chess board. 
                            if((currentCandidateOffset == 1 || currentCandidateOffset == -1) && 
                               (BoardUtils.FIRST_FILE[candidateDestinationCoordinate] || 
                                BoardUtils.EIGHTH_FILE[candidateDestinationCoordinate])){
                                break; 
                            }
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
        return BoardUtils.EIGHTH_FILE[currentPos] && candidateOffset == 1;
    }

    private static boolean isFirstColumnExclusion(int currentPos, int candidateOffset) {
        return BoardUtils.FIRST_FILE[currentPos] && candidateOffset == -1;
    }

    @Override
    public Rook newPiece(final Move move) {
       return new Rook(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
}
 