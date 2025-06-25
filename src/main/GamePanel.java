package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import entity.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseMotionListener, MouseListener {
    private Timer timer;
    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Chicken> chickens;
    private ArrayList<DropItem> items;

    private int level = 1;
    private int lives = 3;
    private boolean isGameOver = false;
    private boolean gameOverDialogShown = false;
    private boolean isPaused = false;

    private boolean isShooting = false;
    private int shootCooldown = 0;
    private int mouseX = 400, mouseY = 300;

    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        
        // Đảm bảo panel nhận focus ngay khi được tạo
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
        });

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor invisibleCursor = toolkit.createCustomCursor(
                toolkit.createImage(""),
                new Point(0, 0),
                "invisibleCursor"
        );
        this.setCursor(invisibleCursor);

        player = new Player(375, 500);
        bullets = new ArrayList<>();
        chickens = new ArrayList<>();
        items = new ArrayList<>();
        spawnChickens();

        timer = new Timer(20, this);
        timer.start();
    }

    private void spawnChickens() {
        chickens.clear();
        if (level % 5 == 0) {
            chickens.add(new BossChicken(300, 100));
        } else {
            int cols = 6, gapX = 100, gapY = 80;
            for (int i = 0; i < level * 5; i++) {
                int col = i % cols;
                int row = i / cols;
                int x = 50 + col * gapX;
                int y = 50 + row * gapY;
                chickens.add(new Chicken(x, y));
            }
        }
    }

    private void resetLevel() {
        bullets.clear();
        player = new Player(375, 500);
        player.setImmune(true);
        spawnChickens();
        shootCooldown = 0;
        isShooting = false;
    }

    private void showGameOverDialog() {
        if (gameOverDialogShown) return;
        gameOverDialogShown = true;

        int option = JOptionPane.showOptionDialog(
                this,
                "Bạn đã thua!\nBạn có muốn chơi lại không?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Chơi lại", "Thoát"},
                "Chơi lại"
        );

        if (option == JOptionPane.YES_OPTION) {
            isGameOver = false;
            gameOverDialogShown = false;
            level = 1;
            lives = 3;
            resetLevel();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPaused) {
            repaint();
            return;
        }

        player.setPosition(mouseX, mouseY);
        player.updateImmune();

        if (isGameOver) {
            showGameOverDialog();
            repaint();
            return;
        }

        if (!player.isImmune()) {
            Rectangle playerBounds = player.getBounds();
            for (Chicken c : chickens) {
                if (playerBounds.intersects(c.getBounds())) {
                    lives--;
                    player.setImmune(true);
                    if (lives <= 0) {
                        isGameOver = true;
                    } else {
                        // Khi mất máu không reset quái nữa
                        // Chỉ đặt lại vị trí và trạng thái của người chơi
                        player = new Player(375, 500);
                        player.setImmune(true);
                    }
                    return;
                }
            }
        }

        if (isShooting) {
            shootCooldown++;
            if (shootCooldown >= 10) {
                bullets.add(new Bullet(player.x + 20, player.y));
                shootCooldown = 0;
            }
        }

        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            b.move();
            if (b.y < 0) bulletIt.remove();
        }

        Iterator<Chicken> chickenIt = chickens.iterator();
        while (chickenIt.hasNext()) {
            Chicken c = chickenIt.next();
            Iterator<Bullet> bulletIt2 = bullets.iterator();
            while (bulletIt2.hasNext()) {
                Bullet b = bulletIt2.next();
                if (c.getBounds().intersects(b.getBounds())) {
                    c.takeDamage(1);
                    bulletIt2.remove();

                    if (c.isDead()) {
                        chickenIt.remove();
                        if (Math.random() < 0.3) {
                            items.add(new DropItem(c.x, c.y));
                        }
                    }
                    break;
                }
            }
        }

        Iterator<DropItem> itemIt = items.iterator();
        while (itemIt.hasNext()) {
            DropItem item = itemIt.next();
            item.move();

            if (item.isOffScreen()) {
                itemIt.remove();
                continue;
            }

            if (player.getBounds().intersects(item.getBounds())) {
                lives = Math.min(lives + 1, 5);
                player.triggerHealEffect();
                itemIt.remove();
            }
        }

        if (level >= 3) {
            for (Chicken c : chickens) {
                c.move();
            }
        }

        if (chickens.isEmpty()) {
            level++;
            spawnChickens();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
        if (isPaused) drawPauseMenu(g);
    }

    private void drawGame(Graphics g) {
        player.draw(g);
        for (Bullet b : bullets) b.draw(g);
        for (Chicken c : chickens) c.draw(g);
        for (DropItem item : items) item.draw(g);

        g.setColor(Color.WHITE);
        g.drawString("Level: " + level, 700, 20);
        g.drawString("Lives: " + lives, 700, 40);
    }

    private void drawPauseMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(200, 150, 400, 300);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Game Paused", 300, 200);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Press C to Continue", 270, 250);
        g.drawString("Press S to Save Game", 270, 290);
        g.drawString("Press Q to Quit", 270, 330);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            bullets.add(new Bullet(player.x + 20, player.y));
            isShooting = true;
            shootCooldown = 0;
        }
    }

    @Override public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) isShooting = false;
    }
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed: " + e.getKeyCode()); // Debug line
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            isPaused = !isPaused;
            System.out.println("Game " + (isPaused ? "paused" : "resumed")); // Debug line
        } else if (isPaused) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_C:
                    isPaused = false;
                    break;
                case KeyEvent.VK_S:
                    System.out.println("Saving game (not implemented)");
                    break;
                case KeyEvent.VK_Q:
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    GameMenuPanel menu = new GameMenuPanel();
                    topFrame.getContentPane().removeAll();
                    topFrame.setContentPane(menu);
                    topFrame.revalidate();
                    topFrame.repaint();
                    menu.requestFocusInWindow();
                    break;
            }
        } else {
            player.keyPressed(e);
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                bullets.add(new Bullet(player.x + 20, player.y));
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}