package org.davistiba;

import org.davistiba.gui.GUIControl;

import javax.swing.*;

/**
 * Checkers game!
 */
public class Main {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // native OS Theme
        SwingUtilities.invokeLater(GUIControl::new);
    }
}
