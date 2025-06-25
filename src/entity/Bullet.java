package entity;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Bullet implements Serializable {
    public int x, y;
    private final int SPEED = 10, WIDTH = 5, HEIGHT = 15;
    private transient Image image;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void loadImage() {
        image = new ImageIcon("res/bullet.png").getImage();
    }

    public void move() {
        y -= SPEED;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
