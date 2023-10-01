package com.chess;

import com.chess.engine.board.Board;
import com.chess.gui.Table;

public class JChess {
    public static void main(String[] args){
        Board board = Board.createInitialBoard(); 
        System.out.println(board);
        Table.get().show(); 
    }
}
