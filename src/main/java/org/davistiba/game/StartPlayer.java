package org.davistiba.game;

public enum StartPlayer {
    AI,
    HUMAN;

    public StartPlayer getOpposite() {
        StartPlayer result = null;
        if (this == AI) {
            result = HUMAN;
        } else if (this == HUMAN) {
            result = AI;
        }
        if (result == null) {
            throw new RuntimeException("Null player has no opposite.");
        }
        return result;
    }
}
