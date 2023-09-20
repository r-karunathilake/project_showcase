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

    // TODO: pawns can be captured in an en passant move if moved 2 tiles forward.
    // TODO: pawns may be promoted to any other piece (except a King) when reaching
    //       the other side of the chess board.
    
public class Pawn extends Piece{

    // For a pawn, these are all the coordinate offsets for a given
    // pawn position with the largest degree of freedom (on a 8x8 tile board).
    private final static int[] CANDIDATE_MOVE_OFFSETS = {7, 8, 9, 16};

    // Pawn constructor 
    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance);
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>(); 

        for(final int currentCandidateOffset:CANDIDATE_MOVE_OFFSETS){
            final int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * currentCandidateOffset);
            
            // If the candidate position is outside the board 
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                continue;
            }
            
            // If the pawn is moving forward by 1 and the tile in front of it is NOT occupied.
            if(currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                // TODO: pawns may be promoted to any other piece (except a King) when reaching
                //       the other side of the chess board.
                legalMoves.add(new NormalMove(board, this, candidateDestinationCoordinate));
            }

            // If the pawn is moving forward by 2 and it's the first move
            else if(currentCandidateOffset == 16 && this.isFirstMove() && 
                    ((BoardUtils.SECOND_ROW[this.piecePosition] && this.pieceAlliance.isBlack()) || 
                     (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.pieceAlliance.isWhite()))){
                
                // Calculate the position of the tile in front of the pawn based on the pawns alliance
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);

                // If both tiles in front of the pawn is NOT occupied
                if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() && 
                   !board.getTile(candidateDestinationCoordinate).isTileOccupied()){

                    legalMoves.add(new NormalMove(board, this, candidateDestinationCoordinate));
                }
            }
            // If the pawn is attacking diagonally forward 
            else if(currentCandidateOffset == 7 && // Valid diagonal right move ?
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite())||
                     (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){  
                    
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                
                // If the tile diagonally to the right of the pawn is NOT occupied
                if(!candidateDestinationTile.isTileOccupied()){
                    legalMoves.add(new NormalMove(board, this, candidateDestinationCoordinate));
                }
                else{// Tile is occupied and this is might be an attacking move 
                    final Piece pieceAtCandidateDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtCandidateDestination.getPieceAlliance(); 
                    
                    // If the pawn alliance is NOT equal to the piece at candidate location,
                    // found an enemy piece.  
                    if(this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate,
                                                      pieceAtCandidateDestination));
                    }
                    else{
                        // There is a friendly chess piece on the right diagonal to the pawn.
                        // This is not a valid move (can move diagonally only when attacking
                        // opponent piece).
                        continue;
                    }
                }
            }
            else if(currentCandidateOffset == 9 && // Valid diagonal left move ?
                    !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite())||
                      (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){ 
                
                        final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                
                // If the tile diagonally to the left of the pawn is NOT occupied
                if(!candidateDestinationTile.isTileOccupied()){
                    legalMoves.add(new NormalMove(board, this, candidateDestinationCoordinate));
                }
                else{// Tile is occupied and this is might be an attacking move 
                    final Piece pieceAtCandidateDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtCandidateDestination.getPieceAlliance(); 
                    
                    // If the pawn alliance is NOT equal to the piece at candidate location,
                    // found an enemy piece.  
                    if(this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate,
                                                      pieceAtCandidateDestination));
                    }
                    else{
                        // There is a friendly chess piece on the left diagonal to the pawn.
                        // This is not a valid move (can move diagonally only when attacking
                        // opponent piece).
                        continue;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn newPiece(final Move move) {
       return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
}
