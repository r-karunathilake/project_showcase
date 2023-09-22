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

public class King extends Piece{

    // For a king, these are all the coordinate offsets for a given
    // king position with the largest degree of freedom (on a 8x8 tile board).
    private final static int[] CANDIDATE_MOVE_OFFSETS = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePosition, 
                final Alliance pieceAlliance) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
    }

    // King constructor override 
    public King(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean isFirstMove){

        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public String toString(){
        return PieceType.KING.toString();
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        
        for(final int currentCandidateOffset:CANDIDATE_MOVE_OFFSETS){
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                // Edge cases
                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                   isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                    // This is not a valid candidate position when the king is
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
                    
                    // If the king alliance is NOT equal to the piece at candidate location,
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
        return BoardUtils.FIRST_FILE[currentPos] && (candidateOffset == -9 || candidateOffset == -1 
                                                     || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPos, final int candidateOffset){
        return BoardUtils.EIGHTH_FILE[currentPos] && (candidateOffset == -7 || candidateOffset == 1
                                                      || candidateOffset == 9);
    }

    @Override
    public King newPiece(final Move move) {
       return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
}
