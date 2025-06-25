package entity;

import javax.swing.*;
import java.awt.*;

public class Player {
    public int x, y;
    public final int WIDTH = 50, HEIGHT = 50;
    public boolean immune = false;
    public int immuneTimer = 0;
    public Image img;
    public boolean showHealEffect = false;
    public int healEffectTimer = 0;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        ImageIcon icon = new ImageIcon("res/plane/plane.png");
        img = icon.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
    }

    public void setImmune(boolean value) {
        immune = value;
        if (value) immuneTimer = 100; // Khoảng 2 giây miễn nhiễm
    }

    public boolean isImmune() {
        return immune;
    }

    public void updateImmune() {
        if (immune) {
            immuneTimer--;
            if (immuneTimer <= 0) {
                immune = false;
            }
        }

        if (healEffectTimer > 0) {
            healEffectTimer--;
        }
    }

    public void setPosition(int mouseX, int mouseY) {
        this.x = Math.max(0, Math.min(mouseX - WIDTH / 2, 800 - WIDTH));
        this.y = Math.max(0, Math.min(mouseY - HEIGHT / 2, 600 - HEIGHT));
    }

    public void move() {}

    public void draw(Graphics g) {
        // Tạo bản sao của đối tượng Graphics để không ảnh hưởng đến context chung
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // Hiệu ứng nhấp nháy khi miễn nhiễm
            if (immune) {
                // Tính toán xem có hiển thị người chơi không dựa trên thời gian
                boolean shouldDisplay = (immuneTimer / 5) % 2 == 0;

                if (shouldDisplay) {
                    // Vẽ người chơi bình thường, không thay đổi Graphics context
                    g2d.drawImage(img, x, y, WIDTH, HEIGHT, null);
                }
            } else {
                // Nếu không miễn nhiễm, luôn hiển thị người chơi
                g2d.drawImage(img, x, y, WIDTH, HEIGHT, null);
            }

            // Hiệu ứng hồi máu nếu có
            if (healEffectTimer > 0) {
                g2d.setColor(new Color(0, 255, 0, 100));
                g2d.fillOval(x - 10, y - 10, 70, 70);
            }
        } finally {
            // Giải phóng tài nguyên
            g2d.dispose();
        }
    }

    public void triggerHealEffect() {
        showHealEffect = true;
        healEffectTimer = 30; // Khoảng 0.6 giây hiệu ứng
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void keyPressed(java.awt.event.KeyEvent e) {}
    public void keyReleased(java.awt.event.KeyEvent e) {}
}