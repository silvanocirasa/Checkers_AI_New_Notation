package org.davistiba.gui;

public enum PieceColor {
    WHITE,
    BLACK, YELLOW;

    public PieceColor getOpposite() {
        PieceColor result = null;
        if (this == WHITE) {
            result = BLACK;
        } else if (this == BLACK) {
            result = WHITE;
        }
        if (result == null) {
            throw new RuntimeException("Null piece has no opposite");
        }
        return result;
    }
}

