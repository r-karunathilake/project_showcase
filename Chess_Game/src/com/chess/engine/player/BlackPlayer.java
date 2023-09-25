package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.board.move.KingSideCastleMove;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.QueenSideCastleMove;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

public class BlackPlayer extends Player{

    public BlackPlayer(final Board board, 
                       final Collection<Move> whiteLegalMoves, 
                       final Collection<Move> blackLegalMoves) {
                        
        super(board, blackLegalMoves, whiteLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
       return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer(); 
    }

    @Override
    public Collection<Move> calculateKingCastles(Collection<Move> opponentLegals) {
        final List<Move> kingCastles = new ArrayList<>();

        // Initial King castling check 
        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            // Black king side castling check if necessary tiles are empty 
            if(!this.board.getTile(5).isTileOccupied() && 
               !this.board.getTile(6).isTileOccupied()){
                
                final Tile rookTileExpected = this.board.getTile(7);
                if(rookTileExpected.isTileOccupied() && 
                   rookTileExpected.getPiece().isFirstMove()){

                    if(Player.calculateAttacksOnTile(5, opponentLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(6, opponentLegals).isEmpty() &&
                       // Is the piece on tile 7 actually a Rook?
                       rookTileExpected.getPiece().getPieceType().isRook()){ 
                        
                        kingCastles.add(new KingSideCastleMove(this.board, 
                                                               this.playerKing, 
                                                               6, 
                                                               (Rook) rookTileExpected.getPiece(), 
                                                               rookTileExpected.getTileCoordinate(), 
                                                               5));
                    }
                }
            }

            // Black queen side castling check if necessary tiles are empty
            if(!this.board.getTile(1).isTileOccupied() && 
               !this.board.getTile(2).isTileOccupied() &&
               !this.board.getTile(3).isTileOccupied()){
                
                final Tile rookTileExpected = this.board.getTile(0);
                if(rookTileExpected.isTileOccupied() && rookTileExpected.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(1, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(2, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
                        // Is the piece on tile 0 actually a Rook?
                        rookTileExpected.getPiece().getPieceType().isRook()){

                            kingCastles.add(new QueenSideCastleMove(this.board, 
                                                                    this.playerKing, 
                                                                    2, 
                                                                    (Rook) rookTileExpected.getPiece(), 
                                                                    rookTileExpected.getTileCoordinate(), 
                                                                    3));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
