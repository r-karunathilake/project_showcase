package com.chess.engine;

import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

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

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, 
                                   final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
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

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, 
                                   final BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }
    }; 
    abstract public int getDirection();
    abstract public boolean isBlack();
    abstract public boolean isWhite();
    abstract public Player choosePlayer(WhitePlayer whitePlayer,
                                        BlackPlayer blackPlayer);
    abstract public int getOppositeDirection();
}
