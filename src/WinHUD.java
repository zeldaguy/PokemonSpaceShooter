import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.TimerTask;

public class WinHUD extends HUD {

    WinHUD(ControlPanel control) {
        super(control);
        if (ControlPanel.location.getLevelIndex() < (ControlPanel.unlockedLocation.length - 1)) {
            ControlPanel.unlockedLocation[ControlPanel.location.getLevelIndex() + 1] = true;
        }
        try {
            ControlPanel.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ControlPanel.clear();
        ControlPanel.dead = false;
        ControlPanel.win = false;
        TimerTask delayTask = new DelayTask();
        timer.schedule(delayTask, 500);
        ControlPanel.location.getOutroMusic().play();
        SoundFX.LEVEL_UP.play();
    }
    public void paintComponent(Graphics2D g2) {
        g2.drawImage(HUD.spaceBackground, 0, 0, ControlPanel.width, ControlPanel.height, control);
        g2.setColor(ControlPanel.TEXT);
        g2.setFont(font);
        drawCenteredString(g2, new Rectangle(0,0,ControlPanel.width,ControlPanel.height), "Level Completed!\nYou scored " + control.getScore() + " points!", font);
    }

    public void update(ControlPanel panel) {
        if (!delay) {
            if (ControlPanel.input.isKeyDown(KeyEvent.VK_SPACE) || ControlPanel.input.isButtonDown(MouseEvent.BUTTON1)) {
                ControlPanel.menusToAdd.add(new LocationOutroHUD(control));
                ControlPanel.menusToRemove.add(this);
                SoundFX.MENU_SELECT.play();
            }
        }
    }
}
