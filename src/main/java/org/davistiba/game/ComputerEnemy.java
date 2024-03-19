package org.davistiba.game;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class ComputerEnemy {

    private final int searchDepth;
    private final StartPlayer player;
    private static final Logger logger = Logger.getLogger(String.valueOf(ComputerEnemy.class));

    public ComputerEnemy() {
        this.searchDepth = Settings.AI_DEPTH;
        this.player = StartPlayer.AI;
    }

    public ComputerEnemy(int searchDepth, StartPlayer player) {
        this.searchDepth = searchDepth;
        this.player = player;
    }

    public GameState makeMove(GameState currentState, StartPlayer currentPlayer) {
        if (currentState.getTurn() == currentPlayer) {
            ArrayList<GameState> possibleMoves = currentState.getSuccessors();
            return findBestMove(possibleMoves);
        } else {
            throw new IllegalStateException("Cannot generate moves for a player when it's not their turn.");
        }
    }

    private GameState findBestMove(ArrayList<GameState> possibleMoves) {
        if (possibleMoves.size() == 1) {
            return possibleMoves.get(0);
        }

        int bestScore = Integer.MIN_VALUE;
        ArrayList<GameState> equalBestMoves = new ArrayList<>();

        for (GameState move : possibleMoves) {
            int score = minimax(move, this.searchDepth);
            if (score > bestScore) {
                bestScore = score;
                equalBestMoves.clear();
            }
            if (score == bestScore) {
                equalBestMoves.add(move);
            }
        }

        if (equalBestMoves.size() > 1) {
            logger.info(player.toString() + " choosing a random best move");
        }

        return selectRandomMove(equalBestMoves);
    }

    private GameState selectRandomMove(ArrayList<GameState> moves) {
        if (moves.isEmpty()) {
            throw new IllegalArgumentException("Cannot randomly choose from an empty list.");
        }
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int randomIndex = rand.nextInt(moves.size());
        return moves.get(randomIndex);
    }

    private int minimax(GameState node, int depth) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        return minimax(node, depth, alpha, beta);
    }

    private int minimax(GameState node, int depth, int alpha, int beta) {
        if (depth == 0 || node.isGameOver()) {
            return node.computeHeuristic(this.player);
        }

        if (node.getTurn() == player) {
            int maxScore = Integer.MIN_VALUE;
            for (GameState child : node.getSuccessors()) {
                maxScore = Math.max(maxScore, minimax(child, depth - 1, alpha, beta));
                alpha = Math.max(alpha, maxScore);
                if (alpha >= beta) {
                    break; // Prune
                }
            }
            return maxScore;
        }

        if (node.getTurn() == player.getOpposite()) {
            int minScore = Integer.MAX_VALUE;
            for (GameState child : node.getSuccessors()) {
                minScore = Math.min(minScore, minimax(child, depth - 1, alpha, beta));
                beta = Math.min(beta, minScore);
                if (alpha >= beta) {
                    break; // Prune
                }
            }
            return minScore;
        }

        throw new IllegalStateException("Error in minimax algorithm");
    }
}
