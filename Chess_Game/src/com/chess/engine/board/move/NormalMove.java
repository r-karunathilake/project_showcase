package com.chess.engine.board.move;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Piece;

public final class NormalMove extends Move{
    public NormalMove(final Board board, final Piece movedPiece, 
                      final int destinationCoordinate){
        super(board, movedPiece, destinationCoordinate);
    }

    @Override
    public boolean equals(final Object other){
        return this == other || (other instanceof NormalMove && super.equals(other));
    }

    @Override
    public String toString(){
        return movedPiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
    }
}
