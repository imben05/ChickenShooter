package entity;

import java.awt.*;

public class BossChicken extends Chicken {
    public BossChicken(int x, int y) {
        super(x, y);
        setHp(20); // máu boss nhiều hơn
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillOval(x, y, 100, 100);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 100, 100);
    }
}
