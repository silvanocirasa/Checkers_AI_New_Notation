package org.davistiba.gui;

import org.davistiba.game.StartPlayer;

public class SettingsView {

    public static PieceColor AIcolour = PieceColor.BLACK; // Note: starting player gets white pieces
    public static boolean helpMode = true;
    public static boolean hintMode = false;
    public static boolean dragDrop = false;
    public static int AiMinPauseDurationInMs = 800;
    public static int squareSize = 80;
    public static int checkerWidth = 5 * squareSize / 6;
    public static int checkerHeight = 5 * squareSize / 6;
    public static int ghostButtonWidth = 30 * squareSize / 29;
    public static int ghostButtonHeight = 5 * squareSize / 6;

    /**
     * Gets the correct colour (black/white) for the given player
     *
     * @param player current player
     * @return the colour
     */
    public static PieceColor getColour(StartPlayer player) {
        PieceColor result = null;
        if (player == StartPlayer.AI) {
            result = AIcolour;
        } else if (player == StartPlayer.HUMAN) {
            result = AIcolour.getOpposite();
        }
        if (result == null) {
            throw new RuntimeException("Null player has no piece.");
        }
        return result;
    }
}
