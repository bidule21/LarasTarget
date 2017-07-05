package de.flashheart.lara.swing;

import de.flashheart.lara.listeners.GamemodeListener;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

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

        BoxLayout boxLayout = new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS);

        setLayout(boxLayout);
        JButton btn = new JButton("GameModeButton");
        btn.addActionListener(e -> gamemodeListener.gamemodeButtonPressed());
        add(btn);
        JButton btn2 = new JButton("Hit");
        btn2.addActionListener(e -> gamemodeListener.healthChangedBy(HEALTH_CHANGE_PER_HIT));
        add(btn2);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
        for (int i = 0; i < 100; i++) {
            Color color = getColor(new BigDecimal(i));
//            pwmRed.setPwm(color.getRed());
//            pwmGreen.setPwm(color.getGreen());
//            pwmBlue.setPwm(color.getBlue());

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JLabel lbl = new JLabel("COLOR");
                    lbl.setForeground(color);
                    pnl.add(lbl);
                }
            });
        }

        add(new JScrollPane(pnl));
    }

    //https://stackoverflow.com/questions/340209/generate-colors-between-red-and-green-for-a-power-meter
    static int getTrafficlightColor(double value) {
        return java.awt.Color.HSBtoRGB((float) value / 3f, 1f, 1f);
    }


    static Color getColor(BigDecimal power) {
        double pwr = power.divide(new BigDecimal(100), 2, BigDecimal.ROUND_UP).doubleValue();

        double H = pwr * 0.4; // Hue (note 0.4 = Green, see huge chart below)
        double S = 0.9; // Saturation
        double B = 0.9; // Brightness

        return Color.getHSBColor((float) H, (float) S, (float) B);
    }
}
