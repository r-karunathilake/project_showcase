package com.chess.engine;

public enum MoveStatus {
    DONE{
        @Override
        public boolean isDone(){
            return true;
        }
    }, 
    
    ILLEGAL_MOVE{
        @Override
        public boolean isDone(){
            return false;
        }
    }, 
    PLAYER_IN_CHECK{
        @Override
        public boolean isDone(){
            return false;
        }
    };
    abstract public boolean isDone();
}
