package main;

import entity.Bullet;
import entity.Chicken;
import entity.DropItem;

import java.io.Serializable;
import java.util.ArrayList;

public class GameSaveData implements Serializable {
    public int level;
    public int lives;
    public int playerX, playerY;
    public ArrayList<Bullet> bullets;
    public ArrayList<Chicken> chickens;
    public ArrayList<DropItem> items;

    public GameSaveData(int level, int lives, int px, int py,
                        ArrayList<Bullet> bullets,
                        ArrayList<Chicken> chickens,
                        ArrayList<DropItem> items) {
        this.level = level;
        this.lives = lives;
        this.playerX = px;
        this.playerY = py;
        this.bullets = bullets;
        this.chickens = chickens;
        this.items = items;
    }
}