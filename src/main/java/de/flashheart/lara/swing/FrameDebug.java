package de.flashheart.lara.swing;

import de.flashheart.lara.listeners.GamemodeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by tloehr on 30.06.17.
 */
public class FrameDebug extends JFrame {

    private final GamemodeListener gamemodeListener;
    private final long HEALTH_CHANGE_PER_HIT;

    public FrameDebug(GamemodeListener gamemodeListener, long HEALTH_CHANGE_PER_HIT) throws HeadlessException {
        super("Lara's Target Debug Window");
        this.gamemodeListener = gamemodeListener;
        this.HEALTH_CHANGE_PER_HIT = HEALTH_CHANGE_PER_HIT;
        setLayout(new FlowLayout());
        JButton btn = new JButton("GameModeButton");
        btn.addActionListener(e -> gamemodeListener.gamemodeButtonPressed());
        add(btn);
        JButton btn2 = new JButton("Hit");
        btn2.addActionListener(e -> gamemodeListener.healthChangedBy(HEALTH_CHANGE_PER_HIT));
        add(btn2);
    }
}
