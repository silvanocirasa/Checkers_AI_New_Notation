package main.game;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.util.ArrayList;

public class BoardState {

    public static final int SIDE_LENGTH = 8;
    public static final int NO_SQUARES = SIDE_LENGTH*SIDE_LENGTH; // 8 x 8
    Player[] state;
    // stores the position that was moved to to reach this state
    private int newPos;

    public BoardState(){
        state = new Player[BoardState.NO_SQUARES];
    }

    /**
     * Set up initial board state.
     */
    public static BoardState InitialState(){
        BoardState bs = new BoardState();
        for (int i = 0; i < bs.state.length; i++){
            int y = i/SIDE_LENGTH;
            int x = i % SIDE_LENGTH;
            // place on black squares only
            if ((x + y) % 2 == 1 ){
                // AI pieces in first 3 rows
                if (y < 3){
                    bs.state[i] = Player.AI;
                }
                // Human pieces in last 3 rows
                else if (y > 4){
                    bs.state[i] = Player.HUMAN;
                }
            }
        }
        return bs;
    }

    public BoardState deepCopy(){
        BoardState bs = new BoardState();
        for (int i = 0; i < bs.state.length; i++){
            bs.state[i] = this.state[i];
        }
        return bs;
    }

    /**
     * Get successor states depending on which player's turn it is.
     * @param player
     * @return
     */
    public ArrayList<BoardState> getSuccessors(Player player){
        ArrayList<BoardState> result = new ArrayList<>();
        for (int i = 0; i < this.state.length; i++){
            if(state[i] == player){
                result.addAll(getSuccessors(player, i));
            }
        }
        return result;
    }

    /**
     * Get valid successor states associated with a particular piece on the board
     * @param piece
     * @param position
     * @return
     */
    public ArrayList<BoardState> getSuccessors(Player player, int position){
        if (this.getPlayer(position) != player){
            throw new IllegalArgumentException("No such piece at that position");
        }
        ArrayList<BoardState> result = new ArrayList<>();
        int y = position / SIDE_LENGTH;
        int x = position % SIDE_LENGTH;
        int[] dxs = new int[]{-1,1};
        int dy = -10;
        switch (player){
            case AI:
                dy = 1;
                break;
            case HUMAN:
                dy = -1;
                break;
        }
        for (int dx : dxs){
            int newx = x + dx;
            int newy = y + dy;
            boolean jump = false;
            if (isValid(newy, newx)) {
                // JUMP
                if (getPlayer(newy, newx) == player.getOpposite() ) {
                    jump = true;
                    newx = newx + dx;
                    newy = newy + dy;
                }
            }
            if (isValid(newy, newx)) {
                // PLACE PIECE
                if (getPlayer(newy, newx) == null) {
                    int newpos = 8*newy + newx;
                    BoardState newState = this.deepCopy();
                    // move piece
                    newState.state[position] = null;
                    newState.state[newpos] = player;
                    // store position moved to
                    newState.newPos = newpos;
                    if (jump){
                        // remove captured piece
                        newState.state[newpos - SIDE_LENGTH*dy - dx] = null;
                    }
                    result.add(newState);
                }
            }
        }
        return result;
    }

    /**
     * Gets the destination position of the most recent move to get to this state.
     * @return
     */
    public int getNewPos(){
        return this.newPos;
    }


    /**
     * Get player piece at given position.
     * @param i Position in board.
     * @return
     */
    public Player getPlayer(int i){
        return state[i];
    }

    /**
     * Get piece by grid position
     * @param y
     * @param x
     * @return
     */
    public Player getPlayer(int y, int x){
        return getPlayer(8*y + x);
    }

    /**
     * Check if grid indices are valid
     * @param y
     * @param x
     * @return
     */
    public boolean isValid(int y, int x){
        return (0 <= y) && (y < SIDE_LENGTH) && (0 <= x) && (x < SIDE_LENGTH);
    }

}