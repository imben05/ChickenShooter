package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chicken Invader Universe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setContentPane(new GameMenuPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
