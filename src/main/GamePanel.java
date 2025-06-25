package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import entity.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseMotionListener, MouseListener {
    public Timer timer;
    public Player player;
    public ArrayList<Bullet> bullets;
    public ArrayList<Chicken> chickens;
    public ArrayList<DropItem> items;

    public int level = 1;
    public int lives = 3;
    public boolean isGameOver = false;
    public boolean gameOverDialogShown = false;
    public boolean isPaused = false;

    public boolean isShooting = false;
    public int shootCooldown = 0;
    public int mouseX = 400, mouseY = 300;
    private enum SubState {
        NONE, SAVE_MENU, LOAD_MENU
    }

    private SubState subState = SubState.NONE;
    private int selectedSlot = 0; // 0 - Slot 1, 1 - Slot 2, 2 - Slot 3
    private Rectangle[] slotBounds = new Rectangle[3];

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
        if (isPaused) {
            drawPauseMenu(g);
            if (subState == SubState.SAVE_MENU) {
                drawSaveSlotMenu(g);
            } else if (subState == SubState.LOAD_MENU) {
                drawLoadSlotMenu(g);
            }
        }
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
        g.drawString("Press L to Load Game", 270, 330);
        g.drawString("Press Q to Quit", 270, 370);
    }
    private void drawSaveSlotMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(250, 180, 300, 180);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String[] slots = {"Save Slot 1", "Save Slot 2", "Save Slot 3"};

        for (int i = 0; i < slots.length; i++) {
            int x = 300;
            int y = 220 + i * 40;

            if (i == selectedSlot) g.setColor(Color.YELLOW);
            else g.setColor(Color.WHITE);
            g.drawString(slots[i], x, y);

            // Tính và lưu vùng chọn
            FontMetrics fm = g.getFontMetrics();
            int width = fm.stringWidth(slots[i]);
            int height = fm.getHeight();
            slotBounds[i] = new Rectangle(x, y - height + 5, width, height);
        }

        g.setFont(new Font("Arial", Font.ITALIC, 16));
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Use W/S or Click, Enter to save", 260, 320);
    }

    private void drawLoadSlotMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(250, 180, 300, 180);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String[] slots = {"Load Slot 1", "Load Slot 2", "Load Slot 3"};

        for (int i = 0; i < slots.length; i++) {
            int x = 300;
            int y = 220 + i * 40;

            if (i == selectedSlot) g.setColor(Color.YELLOW);
            else g.setColor(Color.WHITE);
            g.drawString(slots[i], x, y);

            FontMetrics fm = g.getFontMetrics();
            int width = fm.stringWidth(slots[i]);
            int height = fm.getHeight();
            slotBounds[i] = new Rectangle(x, y - height + 5, width, height);
        }

        g.setFont(new Font("Arial", Font.ITALIC, 16));
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Use W/S or Click, Enter to load", 260, 320);
    }

    private void drawSlotButton(Graphics g, String text, int x, int y) {
        g.setColor(Color.GRAY);
        g.fillRect(x - 10, y - 20, 100, 30);
        g.setColor(Color.WHITE);
        g.drawRect(x - 10, y - 20, 100, 30);
        g.drawString(text, x, y);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (subState == SubState.SAVE_MENU) {
            Point p = e.getPoint();
            for (int i = 0; i < slotBounds.length; i++) {
                if (slotBounds[i] != null && slotBounds[i].contains(p)) {
                    selectedSlot = i;
                    doSaveToSlot(i);
                    subState = SubState.NONE;
                    repaint();
                    return;
                }
            }
        }
        if (subState == SubState.LOAD_MENU) {
            Point p = e.getPoint();
            for (int i = 0; i < slotBounds.length; i++) {
                if (slotBounds[i] != null && slotBounds[i].contains(p)) {
                    selectedSlot = i;
                    doLoadFromSlot(i);
                    subState = SubState.NONE;
                    setCursor(getInvisibleCursor());
                    repaint();
                    return;
                }
            }
        }

        if (e.getButton() == MouseEvent.BUTTON1 && !isPaused) {
            bullets.add(new Bullet(player.x + 20, player.y));
            isShooting = true;
            shootCooldown = 0;
        }
    }

    @Override public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) isShooting = false;
    }
    @Override public void mouseClicked(MouseEvent e) {
        if (!isPaused || subState == SubState.NONE) return;

        int mx = e.getX();
        int my = e.getY();

        for (int i = 0; i < 3; i++) {
            int btnX = 290 - 10;
            int btnY = 260 + i * 40 - 20;
            int btnW = 100;
            int btnH = 30;

            Rectangle slotRect = new Rectangle(btnX, btnY, btnW, btnH);
            if (slotRect.contains(mx, my)) {
                if (subState == SubState.SAVE_MENU) {
                    saveToSlot(i + 1);
                } else if (subState == SubState.LOAD_MENU) {
                    loadFromSlot(i + 1);
                }
                subState = SubState.NONE;
                return;
            }
        }
    }
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }


    @Override
    public void keyPressed(KeyEvent e) {
        if (!isPaused && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            isPaused = true;
            subState = SubState.NONE;
            setCursor(Cursor.getDefaultCursor());
            repaint();
            return;
        }

        if (isPaused) {
            if (subState == SubState.NONE) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C:
                        isPaused = false;
                        setCursor(getInvisibleCursor());
                        break;
                    case KeyEvent.VK_S:
                        subState = SubState.SAVE_MENU;
                        selectedSlot = 0;
                        setCursor(Cursor.getDefaultCursor());
                        break;
                    case KeyEvent.VK_L:
                        subState = SubState.LOAD_MENU;
                        selectedSlot = 0;
                        setCursor(Cursor.getDefaultCursor());
                        break;
                    case KeyEvent.VK_Q:
                        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                        topFrame.setContentPane(new GameMenuPanel());
                        topFrame.revalidate();
                        topFrame.repaint();
                        break;
                }
            } else {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        selectedSlot = (selectedSlot + 2) % 3;
                        break;
                    case KeyEvent.VK_S:
                        selectedSlot = (selectedSlot + 1) % 3;
                        break;
                    case KeyEvent.VK_ENTER:
                        if (subState == SubState.SAVE_MENU) {
                            doSaveToSlot(selectedSlot);
                        } else if (subState == SubState.LOAD_MENU) {
                            doLoadFromSlot(selectedSlot);
                        }
                        subState = SubState.NONE;
                        setCursor(getInvisibleCursor());
                        break;
                    case KeyEvent.VK_ESCAPE:
                        subState = SubState.NONE;
                        break;
                }
            }
            repaint();
        } else {
            player.keyPressed(e);
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                bullets.add(new Bullet(player.x + 20, player.y));
            }
        }
    }

    private void saveToSlot(int slot) {
        try (FileWriter fw = new FileWriter("save_slot_" + slot + ".txt")) {
            fw.write(level + "," + lives + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromSlot(int slot) {
        try (BufferedReader br = new BufferedReader(new FileReader("save_slot_" + slot + ".txt"))) {
            String[] data = br.readLine().split(",");
            level = Integer.parseInt(data[0]);
            lives = Integer.parseInt(data[1]);
            spawnChickens();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void doSaveToSlot(int slot) {
        try {
            // Tạo thư mục nếu chưa có
            File dir = new File("saves");
            if (!dir.exists()) dir.mkdirs();

            // Lấy thời gian hiện tại
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String filename = String.format("saves/save_slot%d_%s.dat", slot + 1, timestamp);

            GameSaveData save = new GameSaveData(
                    level, lives, player.x, player.y,
                    bullets, chickens, items
            );

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                oos.writeObject(save);
            }

            JOptionPane.showMessageDialog(this, "Đã lưu vào " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lưu thất bại");
        }
    }


    private Cursor getInvisibleCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return toolkit.createCustomCursor(
                toolkit.createImage(""),
                new Point(0, 0),
                "invisibleCursor"
        );
    }
    private void doLoadFromSlot(int slot) {
        try {
            FileInputStream fis = new FileInputStream("saves/save_slot_" + (slot + 1) + ".dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            GameSaveData data = (GameSaveData) ois.readObject();
            ois.close();
            fis.close();

            // Restore game state
            this.level = data.level;
            this.lives = data.lives;
            this.player = new Player(data.playerX, data.playerY);
            this.bullets = data.bullets;
            this.chickens = data.chickens;
            this.items = data.items;

            JOptionPane.showMessageDialog(this, "Loaded from slot " + (slot + 1));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Load failed or slot is empty!");
        }
    }




    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}