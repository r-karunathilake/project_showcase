package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.chess.engine.board.Board;
import com.chess.engine.board.move.Move;
import com.chess.gui.Table.MoveLog;

public class GameHistoryPanel extends JPanel{
    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension H_PANEL_DIM = new Dimension(100, 400);
    
    GameHistoryPanel(){
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(this.model);

        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        this.scrollPane.setColumnHeaderView(table.getTableHeader());
        this.scrollPane.setPreferredSize(H_PANEL_DIM); 
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board,
              final MoveLog moveLog){
        
        int currentRow = 0;
        this.model.clear();

        for(final Move move : moveLog.getMoves()){
            final String moveText = move.toString();
            if(move.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText, currentRow, 0);
            }
            else if(move.getMovedPiece().getPieceAlliance().isBlack()){
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++; // This row is now full
            }
        }

        if(moveLog.getMoves().size() > 0){
            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
            final String moveText = lastMove.toString();

            if(lastMove.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText + calculateCheckAndCheckMate(board), currentRow, 0); 
            }
            else if(lastMove.getMovedPiece().getPieceAlliance().isBlack()){
                this.model.setValueAt(moveText + calculateCheckAndCheckMate(board), currentRow - 1, 1); 
            }
        }

        // Automatically scroll to the bottom when the panel is full 
        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    // Special move characters to append 
    private String calculateCheckAndCheckMate(Board board) {
        // Order of these check are important 
        if(board.currentPlayer().isInCheckMate()){
            return "#"; // Standard convention 
        }
        else if(board.currentPlayer().isInCheck()){
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel{
        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel(){
            this.values = new ArrayList<>();
        }

        public void clear(){
            this.values.clear();
            setRowCount(0);    
        }

        @Override
        public int getRowCount(){
            if(this.values == null){
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount(){
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column){
            final Row currentRow = this.values.get(row);
            if(column == 0){
                return currentRow.getWhiteMove();
            }
            else if(column == 1){
                return currentRow.getBlackMove();
            }
            return null; 
        }

        @Override
        public void setValueAt(final Object aValue,
                               final int row,
                               final int column){
            final Row currentRow;

            if(this.values.size() <= row){
                currentRow = new Row();
                this.values.add(currentRow);
            }
            else{
                currentRow = this.values.get(row);
            }

            if(column == 0){
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            }
            else if(column == 1){
                currentRow.setBlackMove((String) aValue);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column){
            return Move.class;
        }

        @Override
        public String getColumnName(final int column){
            return NAMES[column];
        }
    }

    private static class Row{
        private String whiteMove;
        private String blackMove;

        Row(){
        }

        public String getWhiteMove(){
            return this.whiteMove;
        }

        public String getBlackMove(){
            return this.blackMove;
        }

        public void setWhiteMove(final String move){
            this.whiteMove = move; 
        }

        public void setBlackMove(final String move){
            this.blackMove = move; 
        }

    }
}
