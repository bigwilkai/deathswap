package me.wilkai.deathswap;

import javax.swing.*;
import java.awt.*;

import net.md_5.bungee.api.chat.TextComponent;

/**
 * This class is not the main class of the plugin, instead it is the main class of the Jar which will be called if the file is executed manually.
 * This file will simply tell the Player that they should put the Plugin inside of their /plugins/ directory.
 */
public class Main {

    /**
     * Entrypoint of the Program if executed by the User.
     * @param args The arguments provided to the Program.
     */
    public static void main(String[] args) {
        final String ansiRed = "\u001b[31m";
        final String ansiYellow = "\u001b[33m";
        final String ansiReset = "\u001b[0m";

        // Prints the message - "In order to use Deathswap put it in your server's /plugins/ folder!" With color at the specified positions.
        String b = ansiRed +
                "In order to use Deathswap put it in your server's " +
                ansiYellow +
                "/plugins/ " +
                ansiRed +
                "folder!" +
                ansiReset;

        System.out.println(b);

        // Gets the Width and Height of the Screen, used to center the Window.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        JFrame frame = new JFrame();
        frame.setTitle("Deathswap");
        frame.setSize(400, 100);
        frame.setLocation((screen.width - frame.getWidth()) / 2, (screen.height - frame.getHeight()) / 2); // Center the Window.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Tells Java to stop the Program if the Window is Closed.

        JLabel label = new JLabel("To use Deathswap put it in your Server's /plugins/ folder!");

        frame.add(label, BorderLayout.CENTER); // Lock the Text to the Center-Left of the Window.
        frame.setVisible(true); // Make the Window visible to the User.
    }
}
