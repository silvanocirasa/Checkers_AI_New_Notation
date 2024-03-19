package org.davistiba.game;

public class PieceLogic {

    private final StartPlayer player;
    private final boolean king;

    public PieceLogic(StartPlayer player, boolean king) {
        this.player = player;
        this.king = king;
    }

    public boolean isKing() {
        return king;
    }

    public StartPlayer getPlayer() {
        return player;
    }

    /**
     * Get possible x-direction movements
     *
     * @return
     */
    public int[] getValidMoveX() {
        return new int[]{-1, 1};
    }

    /**
     * Get possible y-direction movements
     *
     * @return
     */
    public int[] getValidMoveY() {
        int[] result = new int[]{};
        if (king) {
            result = new int[]{-1, 1};
        } else {
            switch (player) {
                case AI:
                    result = new int[]{1};
                    break;
                case HUMAN:
                    result = new int[]{-1};
                    break;
            }
        }
        return result;
    }

}
