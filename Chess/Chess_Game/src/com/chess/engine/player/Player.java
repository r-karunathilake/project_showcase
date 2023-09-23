package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.MoveStatus;
import com.chess.engine.board.Board;
import com.chess.engine.board.move.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

abstract public class Player {
    protected final Board board;
    protected final King playerKing;
    private final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves){
        
        this.board = board;
        this.playerKing = findKing();
        this.legalMoves = legalMoves; 
        // False, if there are no attacks currently on the chess tile containing the King piece
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnTile(Integer piecePosition, Collection<Move> enemyMoves) {
        final List<Move> attackMoves = new ArrayList<>(); 

        for(final Move move : enemyMoves){
            if(piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    private King findKing() {
        for(final Piece piece : getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Unable to find King on the board! Not a valid board.");
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck && !hasEscapeMoves(); 
    }

    public boolean isInStaleMate(){
        // Player is currently NOT in check, but also has NO escape moves. 
        return !this.isInCheck() && !hasEscapeMoves();
    }

    private boolean hasEscapeMoves() {
        for(final Move move:this.legalMoves){
            // Build a theoretical move for the King to escape 
            final MoveTransition transition = makeMove(move); 

            // Is the theoretical move valid and executable ?
            if(transition.getMoveStatus().isDone()){
                // There is a move such that the King piece can 
                // escape the opponent player attack. 
                return true; 
            }
        }
        return false;
    }

    public boolean isInCheckMate(){
        return false;
    }

    public boolean isCastled(){
        return false;
    }

    public MoveTransition makeMove(final Move move){
        if(!isMoveLegal(move)){
            // Return a move transition with the OLD board 
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        // Create new board after the move 
        final Board transitionBoard = move.execute(); // Note: current player switches
                                                                                                          // Get the opponent due to player switch after move execution  
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                                                                           transitionBoard.currentPlayer().getLegalMoves());
        // Can't make a move that exposes your King to check                                              // The new player attacks on the old player
        if(!kingAttacks.isEmpty()){
            // Return a move transition with the OLD board
            return new MoveTransition(this.board, move, MoveStatus.PLAYER_IN_CHECK);
        }

        // Return a move transition with a NEW board
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE); 
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    private Piece getPlayerKing() {
        return this.playerKing;
    }

    abstract public Collection<Piece> getActivePieces();
    abstract public Alliance getAlliance();
    abstract public Player getOpponent();
    
    abstract protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);
}
