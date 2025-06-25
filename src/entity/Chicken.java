package entity;

import java.awt.*;
import javax.swing.*;

public class Chicken {
    public int x, y;
    private final int WIDTH = 80;
    private final int HEIGHT = 80;
    private Image img;
    private int dx = 2, dy = 1;
    private int hp;
    private final int maxHp = 3;

    public Chicken(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = maxHp;

        // Load ảnh rõ nét kích thước lớn
        ImageIcon icon = new ImageIcon("res/chicken/chicken_santa.png");
        img = icon.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
    }
    public void setHp(int hp) {
        this.hp = hp;
    }
    public void takeDamage(int dmg) {
        hp -= dmg;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void draw(Graphics g) {
        float healthRatio = (float) hp / maxHp;
        if (healthRatio > 0.66f) g.setColor(Color.WHITE);
        else if (healthRatio > 0.33f) g.setColor(Color.ORANGE);
        else g.setColor(Color.RED);

        g.drawImage(img, x, y, null);
        // Nếu muốn dùng oval thay vì ảnh: g.fillOval(x, y, WIDTH, HEIGHT);
    }

    public void move() {
        x += dx;
        y += dy;

        if (x <= 0 || x >= 800 - WIDTH) dx *= -1;
        if (y <= 0 || y >= 600 - HEIGHT) dy *= -1;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
