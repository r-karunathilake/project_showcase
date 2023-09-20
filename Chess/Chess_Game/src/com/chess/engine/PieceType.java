package com.chess.engine;
/**This enumeration class type represents 
all the difference types of chess pieces.*/

public enum PieceType {

    PAWN("P"){
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
           return false;
        }
    },

    KNIGHT("N"){
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
           return false;
        }
    },

    BISHOP("B"){
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
           return false;
        }
    },

    ROOK("R") {
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
           return true;
        }
    },

    QUEEN("Q"){
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
           return false;
        }
    },

    KING("K"){
        @Override
        public boolean isKing() {
            return true;
        }

        @Override
        public boolean isRook() {
           return false;
        }
    };

    private String pieceName;
    PieceType(final String pieceName){
        this.pieceName = pieceName; 
    }

    @Override
    public String toString(){
        return this.pieceName; 
    }

    abstract public boolean isKing();
    abstract public boolean isRook();
}
