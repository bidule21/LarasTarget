package de.flashheart.lara.swing;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import de.flashheart.lara.listeners.GamemodeListener;
import de.flashheart.lara.listeners.VibesensorListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by tloehr on 30.06.17.
 */
public class FrameDebug extends JFrame {
    private java.util.List<VibesensorListener> listeners = new ArrayList<>();
    private final GamemodeListener gamemodeListener;
//    private final long HEALTH_CHANGE_PER_HIT;

    public FrameDebug(GamemodeListener gamemodeListener) throws HeadlessException {
        super("Lara's Target Debug Window");
        this.gamemodeListener = gamemodeListener;
//        this.HEALTH_CHANGE_PER_HIT = HEALTH_CHANGE_PER_HIT;

        BoxLayout boxLayout = new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS);

        setLayout(boxLayout);
        JButton btn = new JButton("GameModeButton");
        btn.addActionListener(e -> gamemodeListener.gamemodeButtonPressed());
        add(btn);

        // das hier simuliert einen Treffer
        JButton btn2 = new JButton("Hit");
        btn2.addActionListener(e -> {
            for (VibesensorListener gl : listeners) {
                gl.handleGpioPinDigitalStateChangeEvent(new GpioPinDigitalStateChangeEvent(this, null, PinState.HIGH));
            }
        });
        add(btn2);

//        JPanel pnl = new JPanel();
//        pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
//        for (int i = 0; i < 100; i++) {
//            Color color = getColor(new BigDecimal(i));
////            pwmRed.setPwm(color.getRed());
////            pwmGreen.setPwm(color.getGreen());
////            pwmBlue.setPwm(color.getBlue());
//
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    JLabel lbl = new JLabel("COLOR");
//                    lbl.setForeground(color);
//                    pnl.add(lbl);
//                }
//            });
//        }
//
//        add(new JScrollPane(pnl));
    }


    public void addListener(VibesensorListener listener) {
        listeners.add(listener);
    }

    ;

}
