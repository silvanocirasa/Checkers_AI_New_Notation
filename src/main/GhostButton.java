package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Button representing a possible move for a player
 */
public class GhostButton extends JButton{

    private BoardState boardstate;
    public final int WIDTH =  65;
    public final int HEIGHT = 50;

    public GhostButton(BoardState state){
        super();
        this.boardstate = state;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon();
    }

    private void setIcon(){
        BufferedImage buttonIcon = null;
        try{
            buttonIcon = ImageIO.read(new File("images/dottedcircle2.png"));
        }
        catch (IOException e){
            System.out.println(e.toString());
        }
        if (buttonIcon != null){
            Image resized = buttonIcon.getScaledInstance(WIDTH,HEIGHT,100);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }


    public BoardState getBoardstate() {
        return boardstate;
    }
}