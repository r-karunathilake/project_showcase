package com.chess.engine.board;

import java.util.HashMap;
import java.util.Map;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap; 

/**This class is an abstraction of the 64 tiles 
present on a chess board.*/
abstract public class Tile {

    protected final int tileCoordinate;
    // Cache of all possible tiles on a chess board 
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    // Constructor 
    protected Tile(final int tileCoordinate){
        this.tileCoordinate = tileCoordinate;
    }
    
    public static Tile createTile(final int tileCoordinate, final Piece piece){
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        } 
        return ImmutableMap.copyOf(emptyTileMap);
    }

    abstract public boolean isTileOccupied();
    abstract public Piece getPiece();

    public int getTileCoordinate() {
        return this.tileCoordinate;
    }
}
