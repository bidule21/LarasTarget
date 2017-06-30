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

    public FrameDebug(GamemodeListener gamemodeListener) throws HeadlessException {
        super("Lara's Target Debug Window");
        this.gamemodeListener = gamemodeListener;
        setLayout(new BorderLayout());
        JButton btn = new JButton("GameModeButton");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamemodeListener.gamemodeButtonPressed();
            }
        });
    }
}
