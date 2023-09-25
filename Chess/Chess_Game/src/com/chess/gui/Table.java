package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.chess.engine.BoardDirection;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.board.move.Move;
import com.chess.engine.board.move.MoveFactory;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Table {
    private final JFrame gameFrame;
    private final GameHistoryPanel historyPanel;
    private final CapturedPiecesPanel capturePanel;
    private final MoveLog moveLog;


    private final static Dimension BOARD_PANEL_DIM = new Dimension(400, 350);
    private final static Dimension FRAME_DIM = new Dimension(600, 600);
    private final static Dimension TILE_PANEL_DIM = new Dimension(10, 10); 
    private final static Color lightTileColor = Color.decode("0xcccccc");
    private final static Color darkTileColor = Color.decode("0x0086b3");

    private final BoardPanel boardPanel;
    private Board chessBoard;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection; 
    private boolean showLegalMoves;
    private int selectedTileId; 

    public Table(){
        // Main window 
        this.gameFrame = new JFrame("Java Chess Engine");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(FRAME_DIM);

        // Create the game history and capture panels 
        this.historyPanel = new GameHistoryPanel();
        this.capturePanel = new CapturedPiecesPanel(); 

        // Load the image from a file 
        setCustomWindowIcon(); 

        // Field initialization
        this.chessBoard = Board.createInitialBoard();
        this.boardDirection = BoardDirection.NORMAL; 
        this.showLegalMoves = false; // Highlight moves false
        this.boardPanel = new BoardPanel(); // Create board panel
        this.moveLog = new MoveLog();

        // Create main window
        this.gameFrame.setJMenuBar(createTableMenuBar());
    
        this.gameFrame.add(this.capturePanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.historyPanel, BorderLayout.EAST); 

        // Center the JFrame on the screen
        this.gameFrame.setLocationRelativeTo(null);
        this.gameFrame.setVisible(true);
    }

    private void setCustomWindowIcon(){
        try {
            // Load the custom icon image from assets
            Path iconPath = BoardUtils.ICON_LIBRARY_PATH.resolve("chess_window_icon.png"); 

            // Check if the icon file exists
            if (iconPath.toFile().exists()) {
                // Create an ImageIcon from the icon image file
                ImageIcon icon = new ImageIcon(iconPath.toString());

                // Set the JFrame's icon to the loaded image
                this.gameFrame.setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createPreferencesMenu() {
        final JMenu prefMenu = new JMenu("Preferences");
        final JMenuItem flipBoardItem = new JMenuItem("Flip Board");
        flipBoardItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e){
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        
        prefMenu.add(flipBoardItem);
        prefMenu.addSeparator(); 

        final JCheckBoxMenuItem legalMoveHighlight = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        legalMoveHighlight.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                showLegalMoves = legalMoveHighlight.isSelected(); 
            }
        });
        prefMenu.add(legalMoveHighlight);
        return prefMenu;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        // Add a way to read game file saves
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("Open the PGN file");
            }
        });
        fileMenu.add(openPGN);
        
        // Add a way to exit the game 
        final JMenuItem existMenuItem = new JMenuItem("Exit");
        existMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        fileMenu.add(existMenuItem);

        return fileMenu; 
    }

    public class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;

        BoardPanel(){
            super(new GridLayout(8, 8));
        

           this.boardTiles = new ArrayList<>();

            // Adding 64 tiles to board panel 
            for(int i = 0; i < BoardUtils.NUM_TILES; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIM);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                if(tilePanel.getTileId() != selectedTileId){
                    tilePanel.clearSelected(); 
                }
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }

        public TilePanel getPanel(int tileId){
            return boardTiles.get(tileId); 
        }
    }

    // Create a log of all the moves executed on the chess board
    public static class MoveLog{
        private final List<Move> moves;

        MoveLog(){
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves(){
            return this.moves;
        }

        public void addMove(final Move move){
            this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear(); 
        }

        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }

        public Move removeMove(int index){
            return this.moves.remove(index);
        }
    }


    public class TilePanel extends JPanel{
        private final int tileId;
        private boolean isSelected;
        private List<JLabel> moveHighlightIcons= new ArrayList<>(); 

        TilePanel(final BoardPanel boardPanel,
                  final int tileId){
            super(new GridBagLayout());
            this.tileId = tileId;
            this.isSelected = false; 

            setPreferredSize(TILE_PANEL_DIM);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            highlightLegalMoves(chessBoard); 

            addMouseListener(new MouseListener(){

                @Override
                public void mouseClicked(final MouseEvent e) { 
                    // Cancel selection if user right click on the mouse
                    if(SwingUtilities.isRightMouseButton(e)){
                        clearTileState();
                    }
                    SwingUtilities.invokeLater(() ->{
                            boardPanel.drawBoard(chessBoard);
                    });

                    if(SwingUtilities.isLeftMouseButton(e)){
                        if(sourceTile == null){
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                
                            // User clicked on an empty tile  
                            if(humanMovedPiece == null){
                                // Nullify the previous 'sourceTile' initialization
                                sourceTile = null;
                                isSelected = false; 
                            }
                            isSelected = true;
                            selectedTileId = tileId; 
                        }
                        else{
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = MoveFactory.createMove(chessBoard, 
                                                                     sourceTile.getTileCoordinate(), 
                                                                     destinationTile.getTileCoordinate()); 
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move); 
                            if(transition.getMoveStatus().isDone()){
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);  
                            }
                            // Clear tile state after executing move
                            clearTileState();
                        }
                        SwingUtilities.invokeLater(() ->{
                            historyPanel.redo(chessBoard, moveLog);
                            capturePanel.redo(moveLog);
                            boardPanel.drawBoard(chessBoard);
                        });
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
            
                }

                @Override
                public void mouseReleased(MouseEvent e) {
            
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }

            }); 
            validate(); 
        }
        public int getTileId() {
            return this.tileId;
        }
        private void clearTileState(){
            sourceTile = null;
            destinationTile = null;
            humanMovedPiece = null;
            isSelected = false; 
            selectedTileId = -1; 
        }

        private void highlightLegalMoves(final Board board){
            if(showLegalMoves){ 
                for(final Move move : pieceLegalMoves(board)){
                    if(move.getDestinationCoordinate() == this.tileId){
                        try{
                            // Load the custom icon image from assets
                            Path iconPath = BoardUtils.ICON_LIBRARY_PATH.resolve("green_dot.png"); 

                            // Check if the icon file exists
                            if (iconPath.toFile().exists()) {
                                // Create an ImageIcon from the icon image file
                                ImageIcon icon = new ImageIcon(iconPath.toString());
                                JLabel pathLabel = new JLabel(icon);
                                // Keep a list labels added for removal later
                                moveHighlightIcons.add(pathLabel); 
                                add(pathLabel);
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if(humanMovedPiece != null && 
            humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                // Handle king castling moves 
                if(humanMovedPiece == board.currentPlayer().getPlayerKing()){
                    Builder<Move> builder = ImmutableList.builder();
                    builder.addAll(humanMovedPiece.calculateLegalMoves(board));
                    builder.addAll(board.currentPlayer().calculateKingCastles(board.currentPlayer().getOpponent().getLegalMoves()));
                    return ImmutableList.copyOf(builder.build()); 
                }
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList(); 
        }

        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){
                Piece pieceOnTile = board.getTile(this.tileId).getPiece(); 

                try {
                    // File name e.g. WB.gif (White Bishop) or BN.gif (Black Knight)
                    final BufferedImage image = ImageIO.read(new File(BoardUtils.ICON_LIBRARY_PATH.resolve(pieceOnTile
                                                                                                                    .getPieceAlliance()
                                                                                                                    .toString()
                                                                                                                    .substring(0, 1) 
                                                                                                                    + pieceOnTile.toString() 
                                                                                                                    + ".gif").toString())); 
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK[this.tileId] || 
            BoardUtils.SIXTH_RANK[this.tileId] ||
            BoardUtils.FOURTH_RANK[this.tileId] ||
            BoardUtils.SECOND_RANK[this.tileId]){

                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            }
            else if(BoardUtils.SEVENTH_RANK[this.tileId] || 
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] ||
                    BoardUtils.FIRST_RANK[this.tileId]){

                setBackground(this.tileId % 2 == 0 ? darkTileColor : lightTileColor);
            }
        }

        private void highlightTileBorder(){
            if(isSelected){
                // Set the border for the JPanel
                this.setBorder(BorderFactory.createLineBorder(Color.RED)); 
            }
            else{
                 // Set the border for the JPanel
                this.setBorder(new EmptyBorder(0, 0, 0, 0)); 
            }
        }

        private void clearhighlightLegalMoves(){
            if(showLegalMoves && !isSelected){
               for(JLabel label : moveHighlightIcons){
                    this.remove(label);
               }
            }
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegalMoves(board);
            highlightTileBorder();
            validate();
            repaint(); 
        }

        public void clearSelected(){
            this.isSelected = false;
        }
    }
}
