package org.davistiba.game;

import java.util.ArrayList;
import java.util.Stack;

public class Game {

    private final Stack<GameState> state;
    private final int memory;
    private final ComputerEnemy ai;
    private boolean playerWon;

    public Game() {
        memory = Settings.UNDO_MEMORY;
        state = new Stack<>();
        state.push(GameState.initialState());
        ai = new ComputerEnemy();
    }

    public void playerMove(GameState newState) {
        if (!isGameOver() && state.peek().getTurn() == StartPlayer.HUMAN) {
            updateState(newState);
        }
    }

    public Messages playerMove(int fromPos, int dx, int dy) {
        int toPos = fromPos + dx + GameState.SIDE_LENGTH * dy;
        if (toPos > getState().state.length) {
            return Messages.NOT_ON_BOARD;
        }
        // check for forced jumped
        ArrayList<GameState> jumpSuccessors = this.state.peek().getSuccessors(true);
        boolean jumps = !jumpSuccessors.isEmpty();
        if (jumps) {
            for (GameState succ : jumpSuccessors) {
                if (succ.getFromPos() == fromPos && succ.getToPos() == toPos) {
                    updateState(succ);
                    return Messages.SUCCESS;
                }
            }
            return Messages.FORCED_JUMP;
        }
        // check diagonal
        if (Math.abs(dx) != Math.abs(dy)) {
            return Messages.NOT_DIAGONAL;
        }
        // check for move onto piece
        if (this.getState().state[toPos] != null) {
            return Messages.NO_FREE_SPACE;
        }
        // check for non-jump moves
        ArrayList<GameState> nonJumpSuccessors = this.state.peek().getSuccessors(fromPos, false);
        for (GameState succ : nonJumpSuccessors) {
            if (succ.getFromPos() == fromPos && succ.getToPos() == toPos) {
                updateState(succ);
                return Messages.SUCCESS;
            }
        }
        if (dy > 1) {
            return Messages.NO_BACKWARD_MOVES_FOR_SINGLES;
        }
        if (Math.abs(dx) == 2) {
            return Messages.ONLY_SINGLE_DIAGONALS;
        }
        return Messages.UNKNOWN_INVALID;
    }

    public Messages moveFeedbackClick(int pos) {
        ArrayList<GameState> jumpSuccessors = this.state.peek().getSuccessors(true);
        if (!jumpSuccessors.isEmpty()) {
            return Messages.FORCED_JUMP;
        } else {
            return Messages.PIECE_BLOCKED;
        }

    }

    public ArrayList<GameState> getValidMoves(int pos) {
        return state.peek().getSuccessors(pos);
    }

    public void aiMove() {
        // update state with ComputerEnemy move
        if (!isGameOver() && state.peek().getTurn() == StartPlayer.AI) {
            GameState newState = ai.makeMove(this.state.peek(), StartPlayer.AI);
            updateState(newState);
        }
    }

    private void updateState(GameState newState) {
        state.push(newState);
        if (state.size() > memory) {
            state.remove(0);
        }
    }

    public GameState getState() {
        return state.peek();
    }


    public StartPlayer getTurn() {
        return state.peek().getTurn();
    }

    public boolean isGameOver() {
        boolean isOver = state.peek().isGameOver();
        if (isOver) {
            // get win / lose status
            playerWon = state.peek().pieceCount.get(StartPlayer.AI) == 0;
        }
        return isOver;
    }

    public String getGameOverMessage() {
        String result = "Game Over. ";
        if (playerWon) {
            result += "YOU WON!";
        } else {
            result += "YOU LOST!";
        }
        return result;
    }

    public void undo() {
        if (state.size() > 2) {
            state.pop();
            while (state.peek().getTurn() == StartPlayer.AI) {
                state.pop();
            }
        }
    }

}
