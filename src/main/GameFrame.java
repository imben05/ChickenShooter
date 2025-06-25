package main;

import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        this.setTitle("Bắn Gà - Java");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(new GamePanel());
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
