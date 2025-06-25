# Chicken Invaders Universe

This is a Java remake of the classic arcade game Chicken Invaders, built using Java Swing.

## 🎮 Features

- 🐔 Shoot waves of chickens with increasing difficulty
- 🧠 Boss fight every 5 levels
- ❤️ Collect health items
- 🔫 Mouse controls for movement and shooting
- ⏸ Pause menu with options (Continue, Save, Quit)

## 🕹 Controls

| Action              | Key / Mouse         |
|---------------------|---------------------|
| Move                | Mouse               |
| Shoot               | Left click or Space |
| Pause/Unpause       | ESC                 |
| Continue (paused)   | C                   |
| Save (paused)       | S                   |
| Quit to menu        | Q                   |

## 🧱 Structure

```
project-root/
├── main/
│   ├── Main.java
│   ├── GamePanel.java
│   └── GameMenuPanel.java
├── entity/
│   ├── Player.java
│   ├── Chicken.java
│   ├── BossChicken.java
│   ├── Bullet.java
│   └── DropItem.java
├── res/
│   ├── chicken/
│   │   └── chicken_santa.png
│   └── font/
│       └── Super Adorable.ttf
```

## 🚀 How to Run

### Requirements:
- Java JDK 8+
- IntelliJ IDEA

### Steps:

```bash
# Compile
javac main/*.java entity/*.java

# Run
java main.Main
```

## 🛠 TODO

- [ ] Implement save/load
- [ ] Add sound effects
- [ ] Add power-ups
- [ ] Improve boss mechanics

## 📜 License

This project is made for educational purposes. Feel free to modify and enhance it.
