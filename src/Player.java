import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Player extends Character {

    private double speed;
    private double projectileDelay;
    private boolean attackDelay = false;
    private boolean bombDelay = false;
    private BufferedImage image1;
    private BufferedImage image2;
    private BufferedImage toShow;
    private Pokemon p;
    private Timer timer = new Timer();
    private static Player player;
    private final int BOMB_DAMAGE = 300;
    private static int bossWall;
    private HealthBar health;

    public Player(int x, int y, int width, int height, Color color, Pokemon p, ControlPanel control) {
        super(x, y, width, height, color, p, control);
        this.speed = p.getMovementSpeed();
        this.player = this;
        this.image1 = p.getBack1();
        this.image2 = p.getBack2();
        this.setHitPoints(p.getHitPoints() * 2);
        this.setMaxHitPoints(this.getHitPoints());
        this.p = p;
        this.projectileDelay = p.getAttack().getAttackDelay();
        toShow = image1;
        this.health = new HealthBar(x, y + this.getHeight(), this.getWidth(), 15, color, this, control);
        ControlPanel.toAdd.add(health);
        timer();
    }

    public boolean checkCollision(GameObject obj) {
        return square.intersects(obj.getObj());
    }

    public void update(ControlPanel panel) {
        // No input if theta equals -100
        double theta = -100;
        int xComponent, yComponent;
        // Keyboard inputs
        if (panel.input.keyUsed()) {
            panel.input.mouseMoved = false;
            if ((panel.input.isKeyDown(KeyEvent.VK_D) || panel.input.isKeyDown(KeyEvent.VK_RIGHT)) && (panel.input.isKeyDown(KeyEvent.VK_W) || panel.input.isKeyDown(KeyEvent.VK_UP))) {
                theta = 7 * Math.PI / 4;
            } else if ((panel.input.isKeyDown(KeyEvent.VK_W) || panel.input.isKeyDown(KeyEvent.VK_UP)) && (panel.input.isKeyDown(KeyEvent.VK_A) || panel.input.isKeyDown(KeyEvent.VK_LEFT))) {
                theta = 5 * Math.PI / 4;
            } else if ((panel.input.isKeyDown(KeyEvent.VK_A) || panel.input.isKeyDown(KeyEvent.VK_LEFT)) && (panel.input.isKeyDown(KeyEvent.VK_S) || panel.input.isKeyDown(KeyEvent.VK_DOWN))) {
                theta = 3 * Math.PI / 4;
            } else if ((panel.input.isKeyDown(KeyEvent.VK_S) || panel.input.isKeyDown(KeyEvent.VK_DOWN)) && (panel.input.isKeyDown(KeyEvent.VK_D) || panel.input.isKeyDown(KeyEvent.VK_RIGHT))) {
                theta = Math.PI / 4;
            } else if ((panel.input.isKeyDown(KeyEvent.VK_W) || panel.input.isKeyDown(KeyEvent.VK_UP))) {
                theta = 3 * Math.PI / 2;
            } else if (panel.input.isKeyDown(KeyEvent.VK_D) || panel.input.isKeyDown(KeyEvent.VK_RIGHT)) {
                theta = 0;
            } else if (panel.input.isKeyDown(KeyEvent.VK_S) || panel.input.isKeyDown(KeyEvent.VK_DOWN)) {
                theta = Math.PI / 2;
            } else if (panel.input.isKeyDown(KeyEvent.VK_A) || panel.input.isKeyDown(KeyEvent.VK_LEFT)) {
                theta = Math.PI;
            } else if ((panel.input.isKeyDown(KeyEvent.VK_B) || panel.input.isKeyDown(KeyEvent.VK_Z) || panel.input.isKeyDown(KeyEvent.VK_SHIFT)) && !bombDelay && control.getBombs() > 0) {
                bomb();
            }
            // Mouse inputs taken if the mouse has been moved since a keyboard button was pressed
        } else if (panel.input.isMouseOn() && panel.input.mouseMoved) {
            theta = Math.asin((panel.input.y - (this.getY() + this.height / 2)) / Math.sqrt(Math.pow((panel.input.x - (this.getX() + +this.width / 2)), 2) + Math.pow(((this.getY() + this.height / 2) - panel.input.y), 2)));
            // Throws bomb
            if (panel.input.isButtonDown(MouseEvent.BUTTON3) && !bombDelay && control.getBombs() > 0) {
                bomb();
            }
            // Prevents movement and flashing when mouse is on player
            if (panel.input.isMouseOn() && (this.getX() + this.width / 2 == panel.input.x && this.getY() + this.height / 2 == panel.input.y)) {
                xComponent = 0;
                yComponent = 0;
            }
        }
        // Handles no input
        if (theta == -100) {
            xComponent = 0;
            yComponent = 0;
        }
        else {
            // Determines how much to move a Pokemon
            if (panel.input.keyUsed()) {
                xComponent = (int) (speed * Math.cos(theta));
                yComponent = (int) (speed * Math.sin(theta));
            } else {
                xComponent = (int) Math.min(Math.abs(speed * Math.cos(theta)), Math.abs((this.getX() + this.width / 2) - panel.input.x));
                yComponent = (int) Math.min(Math.abs(speed * Math.sin(theta)), Math.abs((this.getY() + this.height / 2) - panel.input.y));
                // Corrects lost information due to inverse trig functions
                if (panel.input.x < this.getX() + this.width / 2) {
                    xComponent *= -1;
                }
                if (panel.input.y < this.getY() + this.height / 2) {
                    yComponent *= -1;
                }
            }
        }

        // Moves the Pokemon
        this.setX(this.getX() + xComponent);
        if (control.getBossFight() && this.getY() < bossWall) {
            this.setY(this.getY() + 1);
            if (yComponent > 0) {
                this.setY(this.getY() + yComponent);
            }
        } else if (control.getBossFight() && (this.getY() >= bossWall && this.getY() + yComponent < bossWall)) {
            this.setY(bossWall);
        } else {
            this.setY(this.getY() + yComponent);
        }

        // Bounds player within game boards
        if (this.getX() > ControlPanel.width - this.getWidth()) {
            this.setX(ControlPanel.width - this.getWidth());
        } if (this.getX() < 0) {
            this.setX(0);
        } if (this.getY() > ControlPanel.height - this.getHeight()) {
            this.setY(ControlPanel.height - this.getHeight());
        } if (this.getY() < 0) {
            this.setY(0);
        }

        // Fires normal projectile based on player Pokemon species
        if ((panel.input.isKeyDown(KeyEvent.VK_SPACE) || panel.input.isButtonDown(MouseEvent.BUTTON1)) && attackDelay == false) {
            ControlPanel.toAdd.add(new Projectile(this.getX() + this.getWidth() / 2 - ((int) (p.getAttack().getProjectileSize() *
                    ((double) control.getPower() + 2) / 2)) / 2, this.getY(), (int) (p.getAttack().getProjectileSize() *
                    ((double) control.getPower() + 2) / 2), this.getColor(), p.getAttack(), control, false, xComponent, yComponent));
            attackDelay = true;
            TimerTask projectileTask = new MyProjectileTask();
            timer.schedule(projectileTask, (int) projectileDelay);
        }
    }

    // Prevents player from going too close to the boss
    public static void setBossWall(int wall) {
        bossWall = wall;
    }

    // Player takes damage and is checked if alive
    public void takeDamage(int damage) {
        this.setHitPoints(this.getHitPoints() - Math.max(1, damage));
        if (player.getHitPoints() <= 0) {
            player.death();
        }
    }

    // Game over
    public void death() {
        System.out.print("You lose! You scored: " + control.getScore() * 100);
        System.exit(0);
    }

    public void paintComponent(Graphics2D g2) {
        square.setFrame(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        g2.setColor(color);
        g2.fill(square);
        g2.draw(square);
        g2.drawImage(toShow, this.getX(), this.getY(), this.getWidth(), this.getHeight(), control);
    }

    // Creates special bomb projectile
    public void bomb() {
        new Flash(0, 0, ControlPanel.width, ControlPanel.height, ControlPanel.TRANSPARENT, 200, true, type1);
        bombDelay = true;
        TimerTask bombTask = new MyBombTask();
        timer.schedule(bombTask, 1200);
        control.decrementBombs();
        ControlPanel.toAdd.add(new Projectile(BOMB_DAMAGE, type1, 0));
    }

    public static Player getPlayer() {
        return player;
    }

    @Override
    public BufferedImage getToShow() {
        return toShow;
    }

    @Override
    public BufferedImage getImage1() {
        return image1;
    }

    @Override
    public BufferedImage getImage2() {
        return image2;
    }

    public void setToShow(BufferedImage toShow) {
        this.toShow = toShow;
    }

    public void timer() {
        TimerTask imageTask = new MyImageTask();
        timer.schedule(imageTask, 0, 500);
    }

    // Implements cooldown on bombs
    class MyBombTask extends TimerTask {
        @Override
        public void run() {
            bombDelay  = false;
        }
    }

    // Implements cooldown on normal projectiles
    class MyProjectileTask extends TimerTask {
        @Override
        public void run() {
            attackDelay = false;
        }
    }

    // Animates player sprite
    class MyImageTask extends TimerTask {
        @Override
        public void run() {
            double scale = player.getHeight() / player.getToShow().getHeight();
            int orgHeight = player.getHeight();
            int orgWidth = player.getWidth();
            if (player.getToShow().equals(player.getImage1())) {
                player.setToShow(player.getImage2());
            } else {
                player.setToShow(player.getImage1());
            }
            player.setWidth((int) (player.getToShow().getWidth() * scale));
            player.setHeight((int) (player.getToShow().getHeight() * scale));
            player.setX(player.getX() + (orgWidth - player.getWidth()) / 2);
            player.setY(player.getY() + (orgHeight - player.getHeight()) / 2);
        }
    }
}