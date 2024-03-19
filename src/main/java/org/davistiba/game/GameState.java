package org.davistiba.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class GameState {

    // side length of the board
    public static final int SIDE_LENGTH = 10;
    public static final int NUM_SQUARES = SIDE_LENGTH * SIDE_LENGTH; // 10 x 10
    // state of the board
    PieceLogic[] state;
    // origin and destination position of the most recent move
    private int fromPos = -1;
    private int toPos = -1;
    // origin position of double jump move, used to invalidate other moves during multi-move
    private int doublejumpPos = -1;
    // player's turn
    private StartPlayer turn;
    // track number of human/ComputerEnemy pieces on board
    public HashMap<StartPlayer, Integer> pieceCount;
    private HashMap<StartPlayer, Integer> kingCount;

    public GameState() {
        state = new PieceLogic[GameState.NUM_SQUARES];
    }

    /**
     * Set up initial board state.
     */
    public static GameState initialState() {
        GameState bs = new GameState();
        bs.turn = Settings.FIRSTMOVE;
        for (int i = 0; i < bs.state.length; i++) {
            int y = i / SIDE_LENGTH;
            int x = i % SIDE_LENGTH;
            // place on black squares only
            if ((x + y) % 2 == 1) {
                // ComputerEnemy pieces in first 3 rows
                if (y < 3) {
                    bs.state[i] = new PieceLogic(StartPlayer.AI, false);
                }
                // Human pieces in last 3 rows
                else if (y > 4) {
                    bs.state[i] = new PieceLogic(StartPlayer.HUMAN, false);
                }
            }
        }
        // count initial pieces (generalizable, not hard-coded)
        int aiCount = (int) Arrays.stream(bs.state).filter(Objects::nonNull).filter(x -> x.getPlayer() == StartPlayer.AI).count();
        int humanCount = (int) Arrays.stream(bs.state).filter(Objects::nonNull).filter(x -> x.getPlayer() == StartPlayer.HUMAN).count();
        bs.pieceCount = new HashMap<>();
        bs.pieceCount.put(StartPlayer.AI, aiCount);
        bs.pieceCount.put(StartPlayer.HUMAN, humanCount);
        bs.kingCount = new HashMap<>();
        bs.kingCount.put(StartPlayer.AI, 0);
        bs.kingCount.put(StartPlayer.HUMAN, 0);
        return bs;
    }

    private GameState deepCopy() {
        GameState bs = new GameState();
        System.arraycopy(this.state, 0, bs.state, 0, bs.state.length);
        return bs;
    }

    /**
     * Compute heuristic indicating how desirable this state is to a given player.
     *
     * @param player current StartPlayer
     * @return level
     */
    public int computeHeuristic(StartPlayer player) {
        switch (Settings.HEURISTIC) {
            case 1:
                return heuristic1(player);
            case 2:
                return heuristic2(player);
        }
        throw new RuntimeException("Invalid heuristic");
    }

    private int heuristic1(StartPlayer player) {
        // 'infinite' value for winning
        if (this.pieceCount.get(player.getOpposite()) == 0) {
            return Integer.MAX_VALUE;
        }
        // 'negative infinite' for losing
        if (this.pieceCount.get(player) == 0) {
            return Integer.MIN_VALUE;
        }
        // difference between piece counts with kings counted twice
        return pieceScore(player) - pieceScore(player.getOpposite());
    }


    private int heuristic2(StartPlayer player) {
        // 'infinite' value for winning
        if (this.pieceCount.get(player.getOpposite()) == 0) {
            return Integer.MAX_VALUE;
        }
        // 'negative infinite' for losing
        else if (this.pieceCount.get(player) == 0) {
            return Integer.MIN_VALUE;
        } else {
            return pieceScore(player) / pieceScore(player.getOpposite());
        }
    }

    private int pieceScore(StartPlayer player) {
        return this.pieceCount.get(player) + this.kingCount.get(player);
    }


    /**
     * Gets valid successor states for a player
     *
     * @return
     */
    public ArrayList<GameState> getSuccessors() {
        // compute jump successors
        ArrayList<GameState> successors = getSuccessors(true);
        if (Settings.FORCETAKES) {
            if (!successors.isEmpty()) {
                // return only jump successors if available (forced)
                return successors;
            } else {
                // return non-jump successors (since no jumps available)
                return getSuccessors(false);
            }
        } else {
            // return jump and non-jump successors
            successors.addAll(getSuccessors(false));
            return successors;
        }
    }

    /**
     * Get valid jump or non-jump successor states for a player
     *
     * @param jump must jump?
     * @return list of allowable positions
     */
    public ArrayList<GameState> getSuccessors(boolean jump) {
        ArrayList<GameState> result = new ArrayList<>();
        for (int i = 0; i < this.state.length; i++) {
            if (state[i] != null) {
                if (state[i].getPlayer() == turn) {
                    result.addAll(getSuccessors(i, jump));
                }
            }
        }
        return result;
    }

    /**
     * Gets valid successor states for a specific position on the board
     *
     * @param position target
     * @return list of allowable states
     */
    public ArrayList<GameState> getSuccessors(int position) {
        if (Settings.FORCETAKES) {
            // compute jump successors GLOBALLY
            ArrayList<GameState> jumps = getSuccessors(true);
            if (!jumps.isEmpty()) {
                // return only jump successors if available (forced)
                return getSuccessors(position, true);
            } else {
                // return non-jump successors (since no jumps available)
                return getSuccessors(position, false);
            }
        } else {
            // return jump and non-jump successors
            ArrayList<GameState> result = new ArrayList<>();
            result.addAll(getSuccessors(position, true));
            result.addAll(getSuccessors(position, false));
            return result;
        }
    }

    /**
     * Get valid jump or non-jump successor states for a specific piece on the board.
     *
     * @param position target position
     * @param jump must jump?
     * @return valid states
     */
    public ArrayList<GameState> getSuccessors(int position, boolean jump) {
        if (this.getPiece(position).getPlayer() != turn) {
            throw new IllegalArgumentException("No such piece at that position");
        }
        PieceLogic piece = this.state[position];
        if (jump) {
            return jumpSuccessors(piece, position);
        } else {
            return nonJumpSuccessors(piece, position);
        }
    }

    /**
     * Gets valid non-jump moves at a given position for a given piece
     *
     * @param piece current piece
     * @param position target position
     * @return list of valid states
     */
    private ArrayList<GameState> nonJumpSuccessors(PieceLogic piece, int position) {
        ArrayList<GameState> result = new ArrayList<>();
        int x = position % SIDE_LENGTH;
        int y = position / SIDE_LENGTH;
        // loop through allowed movement directions
        for (int dx : piece.getValidMoveX()) {
            for (int dy : piece.getValidMoveY()) {
                int newX = x + dx;
                int newY = y + dy;
                // new position valid?
                if (isValid(newY, newX)) {
                    // new position available?
                    if (getPiece(newY, newX) == null) {
                        int newpos = SIDE_LENGTH * newY + newX;
                        result.add(createNewState(position, newpos, piece, false, dy, dx));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets valid jump moves at a given position for a given piece
     *
     * @param piece current piece
     * @param position target position
     * @return list of valid states
     */
    private ArrayList<GameState> jumpSuccessors(PieceLogic piece, int position) {
        ArrayList<GameState> result = new ArrayList<>();
        // no other jump moves are valid while doing double jump
        if (doublejumpPos > 0 && position != doublejumpPos) {
            return result;
        }
        int x = position % SIDE_LENGTH;
        int y = position / SIDE_LENGTH;
        // loop through allowed movement directions
        for (int dx : piece.getValidMoveX()) {
            for (int dy : piece.getValidMoveY()) {
                int newX = x + dx;
                int newY = y + dy;
                // new position valid?
                if (isValid(newY, newX)) {
                    // new position contain opposite player?
                    if (getPiece(newY, newX) != null && getPiece(newY, newX).getPlayer() == piece.getPlayer().getOpposite()) {
                        newX = newX + dx;
                        newY = newY + dy;
                        // jump position valid?
                        if (isValid(newY, newX)) {
                            // jump position available?
                            if (getPiece(newY, newX) == null) {
                                int newpos = SIDE_LENGTH * newY + newX;
                                result.add(createNewState(position, newpos, piece, true, dy, dx));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private GameState createNewState(int oldPos, int newPos, PieceLogic piece, boolean jumped, int dy, int dx) {
        GameState result = this.deepCopy();
        result.pieceCount = new HashMap<>(pieceCount);
        result.kingCount = new HashMap<>(kingCount);
        // check if king position
        boolean kingConversion = false;
        if (isKingPosition(newPos, piece.getPlayer())) {
            piece = new PieceLogic(piece.getPlayer(), true);
            kingConversion = true;
            // increase king count
            result.kingCount.replace(piece.getPlayer(), result.kingCount.get(piece.getPlayer()) + 1);
        }
        // move piece
        result.state[oldPos] = null;
        result.state[newPos] = piece;
        // store meta data
        result.fromPos = oldPos;
        result.toPos = newPos;
        StartPlayer oppPlayer = piece.getPlayer().getOpposite();
        result.turn = oppPlayer;
        if (jumped) {
            // remove captured piece
            result.state[newPos - SIDE_LENGTH * dy - dx] = null;
            result.pieceCount.replace(oppPlayer, result.pieceCount.get(oppPlayer) - 1);
            // is another jump available? (not allowed if just converted into king)
            if (!result.jumpSuccessors(piece, newPos).isEmpty() && !kingConversion) {
                // don't swap turns
                result.turn = piece.getPlayer();
                // remember double jump position
                result.doublejumpPos = newPos;
            }
        }
        return result;
    }

    private boolean isKingPosition(int pos, StartPlayer player) {
        int y = pos / SIDE_LENGTH;
        if (y == 0 && player == StartPlayer.HUMAN) {
            return true;
        } else return y == SIDE_LENGTH - 1 && player == StartPlayer.AI;
    }

    /**
     * Gets the destination position of the most recent move.
     *
     * @return
     */
    public int getToPos() {
        return this.toPos;
    }

    /**
     * Gets the destination position of the most recent move.
     *
     * @return
     */
    public int getFromPos() {
        return this.fromPos;
    }


    /**
     * Gets the player whose turn it is
     *
     * @return
     */
    public StartPlayer getTurn() {
        return turn;
    }

    /**
     * Is the board in a game over state?
     *
     * @return
     */
    public boolean isGameOver() {
        return (pieceCount.get(StartPlayer.AI) == 0 || pieceCount.get(StartPlayer.HUMAN) == 0);
    }

    /**
     * Get player piece at given position.
     *
     * @param i Position in board.
     * @return
     */
    public PieceLogic getPiece(int i) {
        return state[i];
    }

    /**
     * Get piece by grid position
     *
     * @param y
     * @param x
     * @return
     */
    private PieceLogic getPiece(int y, int x) {
        return getPiece(SIDE_LENGTH * y + x);
    }

    /**
     * Check if grid indices are valid
     *
     * @param y
     * @param x
     * @return
     */
    private boolean isValid(int y, int x) {
        return (0 <= y) && (y < SIDE_LENGTH) && (0 <= x) && (x < SIDE_LENGTH);
    }

}
