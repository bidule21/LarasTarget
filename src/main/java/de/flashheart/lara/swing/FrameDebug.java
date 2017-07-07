package de.flashheart.lara.swing;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import de.flashheart.lara.handlers.GamemodeHandler;
import de.flashheart.lara.listeners.VibesensorListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by tloehr on 30.06.17.
 */
public class FrameDebug extends JFrame {
    private java.util.List<VibesensorListener> listeners = new ArrayList<>();

    public void setGamemodeHandler(GamemodeHandler gamemodeHandler) {
        this.gamemodeHandler = gamemodeHandler;
    }

    private GamemodeHandler gamemodeHandler;
    private JPanel pnlRGB;
//    private final long HEALTH_CHANGE_PER_HIT;

    public FrameDebug() throws HeadlessException {
        super("Lara's Target Debug Window");
//        this.gamemodeHandler = gamemodeHandler;
//        this.HEALTH_CHANGE_PER_HIT = HEALTH_CHANGE_PER_HIT;

        BoxLayout boxLayout = new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS);

        setLayout(boxLayout);
        JButton btn = new JButton("GameModeButton");
        btn.addActionListener(e -> gamemodeHandler.gamemodeButtonPressed());
        add(btn);

        // das hier simuliert einen Treffer
        JButton btn2 = new JButton("Hit");
        btn2.addActionListener(e -> {
            for (VibesensorListener gl : listeners) {
                gl.handleGpioPinDigitalStateChangeEvent(new GpioPinDigitalStateChangeEvent(this, null, PinState.HIGH));
            }
        });
        add(btn2);

        JLabel lbl = new JLabel("RGB LEDs");
        lbl.setForeground(Color.WHITE);
        pnlRGB = new JPanel();
        pnlRGB.add(lbl);
//        pnlRGB.setOpaque(true);
        add(pnlRGB);
    }


    public void setRGB(Color baseColor, int value) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Color color = pnlRGB.getBackground();
                Color newColor = color;
                if (baseColor.equals(Color.RED)) {
                    newColor = new Color(value, color.getGreen(), color.getBlue());
                } else if (baseColor.equals(Color.GREEN)) {
                    newColor = new Color(color.getRed(), value, color.getBlue());
                } else if (baseColor.equals(Color.BLUE)) {
                    newColor = new Color(color.getRed(), color.getGreen(), value);
                }
                pnlRGB.setBackground(newColor);
                revalidate();
                repaint();
            }
        });
    }

}
