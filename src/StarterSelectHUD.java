import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class StarterSelectHUD extends HUD {

    int currentStarterDexNumIndex = 0;
    int[] starterDexNums = {1,4,7};
    Timer timer = new Timer();
    boolean delay = false;
    BufferedImage toShow;
    int width;
    int height;
    int x;
    int y;

    public StarterSelectHUD(ControlPanel control) {
        super(control);
        try {
            Thread.sleep(ControlPanel.MENU_DELAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.toShow = Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getFront1();
        this.width = this.toShow.getWidth() * DISPLAY_SCALE;
        this.height = this.toShow.getHeight() * DISPLAY_SCALE;
        this.x = ControlPanel.width / 2 - Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getWidth() * DISPLAY_SCALE / 2;
        this.y = ControlPanel.height / 3 - Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getHeight() * DISPLAY_SCALE / 2;
        TimerTask imageTask = new MyImageTask();
        timer.schedule(imageTask, 0, 300);
    }

    // Animates sprite
    class MyImageTask extends TimerTask {
        @Override
        public void run() {
            double scale = height / toShow.getHeight();
            int orgHeight = height;
            int orgWidth = width;
            if (toShow.equals(Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getFront1())) {
                toShow = Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getFront2();
            } else {
                toShow = Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getFront1();
            }
            width = (int) (toShow.getWidth() * scale);
            height = (int) (toShow.getHeight() * scale);
            x = x + (orgWidth - width) / 2;
            y = y + (orgHeight - height) / 2;
        }
    }

    class DelayTask extends TimerTask {
        @Override
        public void run() {
            try {
                delay = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void incrementStarter() {
        currentStarterDexNumIndex++;
        currentStarterDexNumIndex %= starterDexNums.length;
    }

    public void decrementStarter() {
        currentStarterDexNumIndex += starterDexNums.length - 1;
        currentStarterDexNumIndex %= starterDexNums.length;
    }

    public void paintComponent(Graphics2D g2) {
        g2.drawImage(HUD.spaceBackground, 0, 0, ControlPanel.width, ControlPanel.height, control);
        g2.setColor(ControlPanel.TEXT);
        g2.setFont(font);
        g2.drawImage(toShow, x, y, width, height, control);
        drawCenteredString(g2, new Rectangle(0,ControlPanel.height * 2 / 3, ControlPanel.width,
                ControlPanel.height / 3), Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getName()
                + "\n" + Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getType1() + "\n"
                + Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getType2() + "\n"
                + Pokemon.values()[starterDexNums[currentStarterDexNumIndex]].getAttack().getAttackName(), font);
    }

    public void update(ControlPanel panel) {
        if (!delay) {
            boolean changed = true;
            if (panel.input.isKeyDown(KeyEvent.VK_SPACE) || panel.input.isButtonDown(MouseEvent.BUTTON1)) {
                try {
                    ControlPanel.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ControlPanel.unlockedPokemon[starterDexNums[currentStarterDexNumIndex]] = true;
                ControlPanel.toAdd.add(new LevelSelectHUD(control));
                ControlPanel.toRemove.add(this);
                return;
            } else if (panel.input.isKeyDown(KeyEvent.VK_LEFT)) {
                decrementStarter();
            } else if (panel.input.isKeyDown(KeyEvent.VK_RIGHT)) {
                incrementStarter();
            } else {
                changed = false;
            }
            if (changed) {
                delay = true;
                TimerTask delayTask = new DelayTask();
                timer.schedule(delayTask, ControlPanel.MENU_DELAY);
                TimerTask imageTask = new MyImageTask();
                timer.schedule(imageTask, 0);
            }
        }
    }
}