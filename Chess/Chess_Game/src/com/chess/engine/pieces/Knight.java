package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.PieceType;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.board.move.AttackMove;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.NormalMove;
import com.google.common.collect.ImmutableList;

public class Knight extends Piece{

    // For a knight, these are all the coordinate offsets for a given
    // knight position with the largest degree of freedom (on a 8x8 tile board).
    private final static int[] CANDIDATE_MOVE_OFFSETS = {-17, -15, -10, -6, 6, 10, 15, 17};
   
    // Constructor 
    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, true);
    }

    // King constructor override 
    public Knight(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean isFirstMove){

        super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>(); 

        for(final int currentCandidateOffset:CANDIDATE_MOVE_OFFSETS){
            // Apply the offset to the current position of the knight 
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset; 
            // If the candidate position is inside the board 
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                // Edge cases
                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset)   ||
                   isSecondColumnExclusion(this.piecePosition, currentCandidateOffset)  ||
                   isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                   isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                    // This is not a valid candidate position when the knight is
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
                    
                    // If the knights alliance is NOT equal to the piece at candidate location,
                    // found an enemy piece.  
                    if(this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate,
                                                      pieceAtCandidateDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static boolean isFirstColumnExclusion(final int currentPos, final int candidateOffset){
        return BoardUtils.FIRST_FILE[currentPos] && (candidateOffset == -17 || candidateOffset == -10 
                                                       || candidateOffset == 6 || candidateOffset == 15);
    }

    private static boolean isSecondColumnExclusion(final int currentPos, final int candidateOffset){
        return BoardUtils.SECOND_FILE[currentPos] && (candidateOffset == -10 || candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(final int currentPos, final int candidateOffset){
        return BoardUtils.SEVENTH_FILE[currentPos] && (candidateOffset == -6 || candidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(final int currentPos, final int candidateOffset){
        return BoardUtils.EIGHTH_FILE[currentPos] && (candidateOffset == -15 || candidateOffset == -6 
                                                        || candidateOffset == 10 || candidateOffset == 17);
    }

    @Override
    public Knight newPiece(final Move move) {
       return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
}
