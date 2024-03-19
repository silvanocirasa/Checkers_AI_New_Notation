package org.davistiba.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Black or white square panel on checkerboard
 */
public class BoardView extends JPanel {
    private PieceColor color;

    public BoardView(int i, int j) {
        this.setPreferredSize(new Dimension(SettingsView.squareSize, SettingsView.squareSize));
        if (((i % 2) + (j % 2)) % 2 == 0) {
            color = PieceColor.WHITE;
        } else {
            color = PieceColor.BLACK;
        }
    }

    public void setHighlighted() {
        color = PieceColor.YELLOW;
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color javaColor;
        switch (color) {
            case WHITE:
                javaColor = Color.WHITE;
                break;
            case BLACK:
                javaColor = Color.BLACK;
                break;
            case YELLOW:
                javaColor = Color.YELLOW;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + color);
        }

        g.setColor(javaColor);
        g.fillRect(0, 0, getWidth(), getHeight());
    }


}