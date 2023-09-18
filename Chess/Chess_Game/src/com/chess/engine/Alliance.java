package com.chess.engine;
/**This enumeration class type represents 
which two player sides in a game of chess*/

public enum Alliance {
    WHITE{
        @Override
        public int getDirection() {
           return -1;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isWhite() {
            return true;
        }
    },    
    BLACK{
        @Override
        public int getDirection() {
           return 1;
        }

        @Override
        public boolean isBlack() {
           return true;
        }

        @Override
        public boolean isWhite() {
            return false;
        }
    }; 
    abstract public int getDirection();
    abstract public boolean isBlack();
    abstract public boolean isWhite(); 
}
