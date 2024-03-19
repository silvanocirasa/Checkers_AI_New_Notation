package org.davistiba.gui;

import org.davistiba.game.GameState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Button representing a possible move for a player
 */
public class ValidMoveModel extends JButton {

    private final GameState boardstate;

    public ValidMoveModel(GameState state) {
        super();
        this.boardstate = state;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon();
    }

    private static URL getImageResource(final String fileName) {
        return ValidMoveModel.class.getClassLoader().getResource(fileName);
    }

    private void setIcon() {
        BufferedImage buttonIcon = null;
        try {
            if (SettingsView.helpMode) {
                buttonIcon = ImageIO.read(getImageResource("images/dottedcircle.png"));
            } else {
                buttonIcon = ImageIO.read(getImageResource("images/dottedcircleyellow.png"));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (buttonIcon != null) {
            Image resized = buttonIcon.getScaledInstance(SettingsView.ghostButtonWidth, SettingsView.ghostButtonHeight, Image.SCALE_DEFAULT);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }


    public GameState getBoardstate() {
        return boardstate;
    }
}
