package entity;

import java.awt.*;
import javax.swing.*;

public class DropItem {
    public int x, y;
    private final int SIZE = 40;
    private Image img;

    public DropItem(int x, int y) {
        this.x = x;
        this.y = y;

        // Load hình đùi gà từ thư mục res
        img = new ImageIcon("res/item/item_chicken_leg.png").getImage()
                .getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);
    }

    public void move() {
        y += 3; // tốc độ rơi
    }

    public void draw(Graphics g) {
        g.drawImage(img, x, y, null);
    }

    public boolean isOffScreen() {
        return y > 600;
    }

    // ✅ Sửa tại đây
    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }
}
