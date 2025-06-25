package entity;

import java.awt.*;

public class Bullet {
    public int x, y;
    private final int SPEED = 10, WIDTH = 5, HEIGHT = 15;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
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
