package com.chess;

import com.chess.engine.board.Board;
import com.chess.gui.Table;

public class ChessEngine {
    public static void main(String[] args){
        Board.createInitialBoard(); 
        Table.get().show(); 
    }
}
