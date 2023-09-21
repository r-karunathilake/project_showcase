package com.chess.engine.board;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BoardUtils {

    public static final boolean[] FIRST_FILE = initColumn(0);
    public static final boolean[] SECOND_FILE = initColumn(1);
    public static final boolean[] SEVENTH_FILE = initColumn(6);
    public static final boolean[] EIGHTH_FILE = initColumn(7);

    public static final boolean[] EIGHT_RANK = initRow(0); // Tile ID that begins the row
    public static final boolean[] SEVENTH_RANK = initRow(8);
    public static final boolean[] SIXTH_RANK = initRow(16);
    public static final boolean[] FIFTH_RANK = initRow(24); 
    public static final boolean[] FOURTH_RANK = initRow(32); 
    public static final boolean[] THIRD_RANK = initRow(40); 
    public static final boolean[] SECOND_RANK = initRow(48);   
    public static final boolean[] FIRST_RANK = initRow(56);

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_RANK = 8; 

    // Asset paths 
    public static final Path ICON_LIBRARY_PATH = Paths.get("").toAbsolutePath().resolve("assets/");
    
    private BoardUtils(){
        throw new RuntimeException("You cannot instantiate me!"); 
    }

    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_TILES]; // False by default
        do{
            row[rowNumber] = true;
            rowNumber++; 
        }
        while(rowNumber % NUM_TILES_PER_RANK != 0);
        return row;
    }

    private static boolean[] initColumn(int columnNumber) {
        /**This function will calculate the position on the
         * chess board corresponding to the given 'columnNumber'*/
        final boolean[] column = new boolean[64]; // all false by default
        do{
            column[columnNumber] = true;
            columnNumber += 8;
        }while(columnNumber < 64);
        return column;
    }

    public static boolean isValidTileCoordinate(final int coordinate) {
        return coordinate >= 0 && coordinate < 64;
    }
}
