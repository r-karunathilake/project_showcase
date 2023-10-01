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

public class WhitePlayer extends Player{

    public WhitePlayer(final Board board, 
                       final Collection<Move> whiteLegalMoves, 
                       final Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
       return Alliance.WHITE;
    }

    @Override
    public String toString(){
        return this.getAlliance().toString();
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer(); 
    }

    @Override
    public Collection<Move> calculateKingCastles(Collection<Move> opponentLegals) {
        final List<Move> kingCastles = new ArrayList<>();

        // Initial King castling check 
        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            // White king side castling check if necessary tiles are empty 
            if(!this.board.getTile(61).isTileOccupied() && 
               !this.board.getTile(62).isTileOccupied()){
                
                final Tile rookTileExpected = this.board.getTile(63);
                if(rookTileExpected.isTileOccupied() && 
                   rookTileExpected.getPiece().isFirstMove()){

                    if(Player.calculateAttacksOnTile(61, opponentLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(62, opponentLegals).isEmpty() &&
                       // Is the piece on tile 63 actually a Rook?
                       rookTileExpected.getPiece().getPieceType().isRook()){ 
                        
                        kingCastles.add(new KingSideCastleMove(this.board, 
                                                               this.playerKing, 
                                                               62, 
                                                               (Rook) rookTileExpected.getPiece(), 
                                                               rookTileExpected.getTileCoordinate(), 
                                                               61));
                    }
                }
            }

            // White queen side castling check if necessary tiles are empty
            if(!this.board.getTile(59).isTileOccupied() && 
               !this.board.getTile(58).isTileOccupied() &&
               !this.board.getTile(57).isTileOccupied()){
                
                final Tile rookTileExpected = this.board.getTile(56);
                if(rookTileExpected.isTileOccupied() && rookTileExpected.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(59, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(58, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(57, opponentLegals).isEmpty() &&
                        // Is the piece on tile 56 actually a Rook?
                        rookTileExpected.getPiece().getPieceType().isRook()){

                            kingCastles.add(new QueenSideCastleMove(this.board, 
                                                                    this.playerKing, 
                                                                    58, 
                                                                    (Rook) rookTileExpected.getPiece(), 
                                                                    rookTileExpected.getTileCoordinate(),
                                                                    59));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
