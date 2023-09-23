package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.PieceType;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.PawnAttackMove;
import com.chess.engine.board.move.PawnEnPassantAttack;
import com.chess.engine.board.move.PawnJump;
import com.chess.engine.board.move.PawnMove;
import com.chess.engine.board.move.PawnPromotion;
import com.google.common.collect.ImmutableList;

public class Pawn extends Piece{

    // For a pawn, these are all the coordinate offsets for a given
    // pawn position with the largest degree of freedom (on a 8x8 tile board).
    private final static int[] CANDIDATE_MOVE_OFFSETS = {7, 8, 9, 16};

    // Pawn constructor 
    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    // Pawn constructor override 
    public Pawn(final int piecePosition, 
                final Alliance pieceAlliance,
                final boolean isFirstMove){

        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
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
                if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                }
                legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
            }

            // If the pawn is moving forward by 2 and it's the first move
            else if(currentCandidateOffset == 16 && this.isFirstMove() && 
                    ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()) || 
                     (BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite()))){
                
                // Calculate the position of the tile in front of the pawn based on the pawns alliance
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);

                // If both tiles in front of the pawn is NOT occupied
                if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() && 
                   !board.getTile(candidateDestinationCoordinate).isTileOccupied()){

                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            // If the pawn is attacking diagonally forward 
            else if(currentCandidateOffset == 7 && // Valid diagonal right move ?
                    !((BoardUtils.EIGHTH_FILE[this.piecePosition] && this.pieceAlliance.isWhite())||
                     (BoardUtils.FIRST_FILE[this.piecePosition] && this.pieceAlliance.isBlack()))){  
                    
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                
                // If the tile diagonally to the right of the pawn is occupied
                if(candidateDestinationTile.isTileOccupied()){
                    // Tile is occupied and this is might be an attacking move 
                    final Piece pieceAtCandidateDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAtDestinationAlliance = pieceAtCandidateDestination.getPieceAlliance();
                    
                    if(this.pieceAlliance != pieceAtDestinationAlliance){
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, 
                                                          pieceAtCandidateDestination)));
                        }
                        else{
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, 
                                                          pieceAtCandidateDestination));
                        } 
                    }
                }
                else if (board.getEnPassantPawn() != null){ // Check if there is an en passant piece on the board
                    // Is the en passant pawn from the opponent to the right of the current player pawn?
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + this.pieceAlliance.getOppositeDirection())){
                        final Piece enPassantPawn = board.getEnPassantPawn();

                        if(this.pieceAlliance != enPassantPawn.getPieceAlliance()){
                            legalMoves.add(new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, enPassantPawn));
                        }
                    }
                }
            }
            else if(currentCandidateOffset == 9 && // Valid diagonal left move ?
                    !((BoardUtils.FIRST_FILE[this.piecePosition] && this.pieceAlliance.isWhite())||
                      (BoardUtils.EIGHTH_FILE[this.piecePosition] && this.pieceAlliance.isBlack()))){ 
                
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                
                // If the tile diagonally to the right of the pawn is occupied
                if(candidateDestinationTile.isTileOccupied()){
                    // Tile is occupied and this is might be an attacking move 
                    final Piece pieceAtCandidateDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAtDestinationAlliance = pieceAtCandidateDestination.getPieceAlliance();
                    
                    if(this.pieceAlliance != pieceAtDestinationAlliance){
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, 
                                                          pieceAtCandidateDestination)));
                        }
                        else{
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, 
                                                              pieceAtCandidateDestination));
                        }
                    }
                }
                else if (board.getEnPassantPawn() != null){ // Check if there is an en passant piece on the board
                    // Is the en passant pawn from the opponent to the left of the current player pawn?
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - this.pieceAlliance.getOppositeDirection())){
                        final Piece enPassantPawn = board.getEnPassantPawn();

                        if(this.pieceAlliance != enPassantPawn.getPieceAlliance()){
                            legalMoves.add(new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, enPassantPawn));
                        }
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

    // Pawn promotion to Queen 
    public Piece getPromotionPiece() {
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }
}
