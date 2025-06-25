package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameMenuPanel extends JPanel implements MouseListener {

    private class MenuItem {
        String text;
        Rectangle bounds;
        int x, y;

        public MenuItem(String text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
            // Bounds sẽ được tính toán lại trong updateBounds()
            this.bounds = new Rectangle();
        }
    }

    private ArrayList<MenuItem> menuItems = new ArrayList<>();
    private Image chickenImage;
    private Font customFont;
    private Font menuFont;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        g.setColor(Color.WHITE);

        // Draw title centered
        g.setFont(customFont);
        drawCenteredString(g, "CHICKEN INVADERS", panelWidth, 100);

        g.setFont(customFont.deriveFont(28f));
        drawCenteredString(g, "UNIVERSE", panelWidth, 145);

        // Draw chicken image
        if (chickenImage != null) {
            g.drawImage(chickenImage, (panelWidth - 80) / 2, 160, 80, 80, null);
        }

        // Draw menu items
        g.setFont(menuFont);
        for (MenuItem item : menuItems) {
            // Cập nhật bounds dựa trên kích thước thực tế của text
            updateItemBounds(g, item);
            
            int stringWidth = g.getFontMetrics().stringWidth(item.text);
            g.drawString(item.text, item.x - stringWidth / 2, item.y);
        }
    }

    private void updateItemBounds(Graphics g, MenuItem item) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(item.text);
        int textHeight = fm.getHeight();
        
        // Điều chỉnh bounds để vừa với text, với một chút padding
        int padding = 10;
        item.bounds.x = item.x - textWidth/2 - padding;
        item.bounds.y = item.y - textHeight + fm.getDescent() - padding;
        item.bounds.width = textWidth + padding*2;
        item.bounds.height = textHeight + padding*2;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        System.out.println("Click at: " + p.x + "," + p.y);
        
        for (MenuItem item : menuItems) {
            if (item.bounds.contains(p)) {
                System.out.println("Clicked on: " + item.text);
                handleMenuClick(item.text);
                break;
            }
        }
    }

    // Tách logic xử lý click menu ra riêng để dễ debug
    private void handleMenuClick(String menuText) {
        switch (menuText) {
            case "NEW GAME":
                System.out.println("Starting new game...");
                try {
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    GamePanel gamePanel = new GamePanel();
                    topFrame.getContentPane().removeAll();
                    topFrame.setContentPane(gamePanel);
                    topFrame.revalidate();
                    topFrame.repaint();
                    
                    // Đảm bảo focus được set sau khi panel đã được render
                    SwingUtilities.invokeLater(() -> {
                        gamePanel.requestFocusInWindow();
                    });
                } catch (Exception e) {
                    System.err.println("Error starting new game: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            
            case "LOAD GAME":
                System.out.println("Load game...");
                break;
            
            case "QUIT":
                System.exit(0);
                break;
        }
    }

    public GameMenuPanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Đảm bảo mouse listener được add trước khi request focus
        addMouseListener(this);
        
        // Request focus sau khi panel được tạo
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
        });

        try {
            ImageIcon icon = new ImageIcon("res/chicken/chicken_santa.png");
            chickenImage = icon.getImage();

            customFont = Font.createFont(Font.TRUETYPE_FONT, 
                new File("res/font/Super Adorable.ttf")).deriveFont(42f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            System.err.println("Error loading resources: " + e.getMessage());
            customFont = new Font("Arial", Font.BOLD, 42);
        }
        
        // Tạo font cho menu
        menuFont = new Font("Arial", Font.PLAIN, 26);

        int centerX = 400;
        menuItems.add(new MenuItem("NEW GAME", centerX, 300));
        menuItems.add(new MenuItem("LOAD GAME", centerX, 350));
        menuItems.add(new MenuItem("QUIT", centerX, 400));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    private void drawCenteredString(Graphics g, String text, int panelWidth, int y) {
        FontMetrics metrics = g.getFontMetrics();
        int x = (panelWidth - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}