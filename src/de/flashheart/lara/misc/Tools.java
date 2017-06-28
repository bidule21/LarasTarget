package de.flashheart.lara.misc;


import com.pi4j.io.gpio.Pin;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ResourceBundle;

/**
 * Created by tloehr on 01.05.15.
 */
public class Tools {

    public static final String SND_WELCOME = "0140_female1_OnWelcome_1.wav";
    public static final String SND_SIREN = "capture_siren.wav";
    public static final String SND_FLARE = "MP_flare.wav";
    public static final String SND_SHUTDOWN = "Shutdown.wav";
    public static final String SND_MINIONS_SPAWNED = "0112_female1_OnMinionsSpawn_1.wav";
    public static final String SND_VICTORY = "0134_female1_OnVictory_1.wav";
    public static final String SND_DEFEAT = "0071_female1_OnDefeat_1.wav";
    public static final String SND_LOSER = "loser.wav";
    public static final String SND_MIB = "mib.wav";
    public static final String SND_QUEEN = "we-will-rock-you.wav";
    public static final String SND_SKYFALL = "skyfall1.wav";
    public static final String SND_TRANQUILITY = "tranquillity-mono.wav";
    public static final String SND_SIRIUS = "sirius.wav";
    public static final String SND_ZZTOP = "zztop.wav";
    public static final String SND_EVERYBODY_DANCE_NOW = "gonna_make_you_sweat.wav";
    public static final String SND_WHO_WANTS_TO_LIVE_FOREVER = "who-wants-to-live-forever.wav";
    public static final String SND_BE_HAPPY = "be-happy.wav";

    public static final String[] COUNTDOWN = new String[]{"10.wav", "09.wav", "08.wav", "07.wav", "06.wav", "05.wav", "04.wav", "03.wav", "02.wav", "01.wav", "00.wav"};

    public static final String SND_20_MINUTES = "20-minutes.wav";
    public static final String SND_10_MINUTES = "10-minutes.wav";
    public static final String SND_5_MINUTES = "05-minutes.wav";
    public static final String SND_4_MINUTES = "04-minutes.wav";
    public static final String SND_3_MINUTES = "03-minutes.wav";
    public static final String SND_2_MINUTES = "02-minutes.wav";
    public static final String SND_1_MINUTE = "01-minute.wav";
    public static final String SND_30_SECONDS = "30-seconds.wav";
    public static final String SND_20_SECONDS = "20-seconds.wav";
    public static final String SND_10_SECONDS = "10-seconds.wav";


    public static final String[] WINNING_SONGS = new String[]{SND_MIB, SND_QUEEN};
    public static final String[] LOSING_SONGS = new String[]{SND_LOSER};


    public static final Icon icon22ledOrangeOn = new ImageIcon(Tools.class.getResource("/artwork/22x22/ledorange.png"));
    public static final Icon icon22ledOrangeOff = new ImageIcon(Tools.class.getResource("/artwork/22x22/leddarkorange.png"));
    public static final Icon icon22ledPurpleOff = new ImageIcon(Tools.class.getResource("/artwork/22x22/leddarkpurple.png"));
    public static final Icon icon22ledPurpleOn = new ImageIcon(Tools.class.getResource("/artwork/22x22/ledpurple.png"));
    public static final Icon icon22ledBlueOff = new ImageIcon(Tools.class.getResource("/artwork/22x22/leddarkblue.png"));
    public static final Icon icon22ledBlueOn = new ImageIcon(Tools.class.getResource("/artwork/22x22/ledblue.png"));
    public static final Icon icon22ledGreenOff = new ImageIcon(Tools.class.getResource("/artwork/22x22/leddarkgreen.png"));
    public static final Icon icon22ledGreenOn = new ImageIcon(Tools.class.getResource("/artwork/22x22/ledgreen.png"));
    public static final Icon icon22ledYellowOff = new ImageIcon(Tools.class.getResource("/artwork/22x22/leddarkyellow.png"));
    public static final Icon icon22ledYellowOn = new ImageIcon(Tools.class.getResource("/artwork/22x22/ledyellow.png"));
    public static final Icon icon22ledRedOff = new ImageIcon(Tools.class.getResource("/artwork/22x22/leddarkred.png"));
    public static final Icon icon22ledRedOn = new ImageIcon(Tools.class.getResource("/artwork/22x22/ledred.png"));


    public static String xx(String message) {
        String title = catchNull(message);
        try {
            ResourceBundle lang = ResourceBundle.getBundle("Messages");
            title = lang.getString(message);
        } catch (Exception e) {
            // ok, its not a langbundle mainSiren
        }
        return title;
    }

    public static String catchNull(String in) {
        return (in == null ? "" : in.trim());
    }


    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isWindows() {

        String os = System.getProperty("os.name").toLowerCase();
        //windows
        return (os.indexOf("win") >= 0);

    }

    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isMac() {

        String os = System.getProperty("os.name").toLowerCase();
        //Mac
        return (os.indexOf("mac") >= 0);

    }

    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isArm() {

        String os = System.getProperty("os.arch").toLowerCase();
        return (os.indexOf("arm") >= 0);

    }

    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isUnix() {

        String os = System.getProperty("os.name").toLowerCase();
        //linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

    }


    public static String getMissionboxDirectory() {
        return (isArm() ? "/home/pi" : System.getProperty("user.home")) + File.separator + "missionbox";
    }


    public static int parseInt(String input, int min, int max, int previous) {
        int result = previous;
        try {
            result = Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            result = previous;
        }

        if (result < min || result > max) {
            result = previous;
        }

        return result;
    }

    public static long parseLong(String input, long min, long max, long previous) {
        long result = previous;
        try {
            result = Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            result = previous;
        }

        if (result < min || result > max) {
            result = previous;
        }

        return result;
    }

    public static String formatLongTime(long time, String pattern) {
        return time < 0l ? "--" : new DateTime(time, DateTimeZone.UTC).toString(pattern);
    }


    public static String formatLongTime(long time) {
        return formatLongTime(time, "mm:ss,SSS");
    }

    public static String getSoundPath() {
        return getMissionboxDirectory() + File.separator + "sounds";
    }
//
//    public static String getWinningSong() {
//        int rand = ThreadLocalRandom.current().nextInt(0, WINNING_SONGS.length);
//        return WINNING_SONGS[rand];
//    }
//
//    public static String getLosingSong() {
//        int rand = ThreadLocalRandom.current().nextInt(0, LOSING_SONGS.length);
//        return LOSING_SONGS[rand];
//    }


//    public static Animator flashBackground(Animator animator, final JComponent component, final Color flashcolor, int repeatTimes) {
//        if (component == null)
//            return null; // this prevents NULL pointer exceptions when quickly switching the residents after the entry
//        final Color originalColor = component.getBackground();
//
//
//        if (animator == null || !animator.isRunning()) {
//
//            final TimingSource ts = new SwingTimerTimingSource();
//            final boolean wasOpaque = component.isOpaque();
//            Animator.setDefaultTimingSource(ts);
//            ts.init();
//            component.setOpaque(true);
//
//
//            animator = new Animator.Builder().setDuration(750, TimeUnit.MILLISECONDS).setRepeatCount(repeatTimes).setRepeatBehavior(Animator.RepeatBehavior.REVERSE).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {
//                @Override
//                public void begin(Animator source) {
//                }
//
//                @Override
//                public void timingEvent(Animator animator, final double fraction) {
//                    SwingUtilities.invokeLater(() -> {
//                        component.setBackground(interpolateColor(originalColor, flashcolor, fraction));
//                        component.repaint();
//                    });
//                }
//
//                @Override
//                public void end(Animator source) {
//                    component.setOpaque(wasOpaque);
//                    component.repaint();
//                }
//            }).build();
//        } else {
//            animator.stop();
//        }
//        animator.start();
//
//        return animator;
//    }


    public static void printProgBar(int percent) {
        StringBuilder bar = new StringBuilder("[");

        for (int i = 0; i < 50; i++) {
            if (i < (percent / 2)) {
                bar.append("=");
            } else if (i == (percent / 2)) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }

        bar.append("]   " + percent + "%     ");
        System.out.print("\r" + bar.toString());
    }

    /**
     * @param distance a double between 0.0f and 1.0f to express the distance between the source and destination color
     *                 see http://stackoverflow.com/questions/27532/generating-gradients-programatically
     * @return
     */
    public static Color interpolateColor(Color source, Color destination, double distance) {
        int red = (int) (destination.getRed() * distance + source.getRed() * (1 - distance));
        int green = (int) (destination.getGreen() * distance + source.getGreen() * (1 - distance));
        int blue = (int) (destination.getBlue() * distance + source.getBlue() * (1 - distance));
        return new Color(red, green, blue);
    }


    /**
     * läuft rekursiv durch alle Kinder eines Containers und setzt deren Enabled Status auf
     * enabled.
     */
    public static void setXEnabled(JComponent container, boolean enabled) {
        // Bei einer Combobox muss die Rekursion ebenfalls enden.
        // Sie besteht aus weiteren Unterkomponenten
        // "disabled" wird sie aber bereits hier.
        if (container.getComponentCount() == 0 || container instanceof JComboBox) {
            // Rekursionsanker
            container.setEnabled(enabled);
        } else {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    JComponent jc = (JComponent) c[i];
                    setXEnabled(jc, enabled);
                }
            }
        }
    }


//    public static void flashBackground(final JComponent component, final Color flashcolor, int repeatTimes) {
//        // https://github.com/tloehr/Offene-Pflege.de/issues/37
//        if (component == null)
//            return; // this prevents NULL pointer exceptions when quickly switching the residents after the entry
//        flashBackground(component, flashcolor, component.getBackground(), repeatTimes);
//    }

//    public static void flashBackground(final JComponent component, final Color flashcolor, final Color originalColor, int repeatTimes) {
//        if (component == null)
//            return; // this prevents NULL pointer exceptions when quickly switching the residents after the entry
//        //            final Color originalColor = component.getBackground();
//        final TimingSource ts = new SwingTimerTimingSource();
//        final boolean wasOpaque = component.isOpaque();
//        Animator.setDefaultTimingSource(ts);
//        ts.init();
//        component.setOpaque(true);
//        Animator animator = new Animator.Builder().setDuration(750, TimeUnit.MILLISECONDS).setRepeatCount(repeatTimes).setRepeatBehavior(Animator.RepeatBehavior.REVERSE).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {
//            @Override
//            public void begin(Animator source) {
//            }
//
//            @Override
//            public void timingEvent(Animator animator, final double fraction) {
//                SwingUtilities.invokeLater(() -> {
//                    component.setBackground(interpolateColor(originalColor, flashcolor, fraction));
//                    component.repaint();
//                });
//            }
//
//            @Override
//            public void end(Animator source) {
//                component.setOpaque(wasOpaque);
//                component.repaint();
//            }
//        }).build();
//        animator.start();
//    }

//    public static void flashIcon(final AbstractButton btn, final Icon icon) {
//        flashIcon(btn, icon, 2);
//    }
//
//    public static void flashIcon(final AbstractButton btn, final Icon icon, int repeat) {
//
//        if (btn == null)
//            return; // this prevents NULL pointer exceptions when quickly switching the residents after the entry
//
//        int textposition = btn.getHorizontalTextPosition();
//        btn.setHorizontalTextPosition(SwingConstants.LEADING);
//
//        final Icon originalIcon = btn.getIcon();
//        final TimingSource ts = new SwingTimerTimingSource();
//        Animator.setDefaultTimingSource(ts);
//        ts.init();
//
//        Animator animator = new Animator.Builder().setDuration(750, TimeUnit.MILLISECONDS).setRepeatCount(repeat).setRepeatBehavior(Animator.RepeatBehavior.REVERSE).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {
//            Animator.Direction dir;
//
//            public void begin(Animator source) {
//                dir = null;
//            }
//
//            @Override
//            public void timingEvent(Animator animator, final double fraction) {
//
//                if (dir == null || !dir.equals(animator.getCurrentDirection())) {
//
//                    dir = animator.getCurrentDirection();
//
//                    SwingUtilities.invokeLater(() -> {
//
//                        if (animator.getCurrentDirection().equals(Animator.Direction.FORWARD)) {
//                            btn.setIcon(icon);
//                        } else {
//                            btn.setIcon(originalIcon);
//                        }
//
//                        //                    Logger.getLogger(getClass()).debug(fraction);
//                        //                    btn.setIcon();
//                        //                    component.setBackground(interpolateColor(originalColor, flashcolor, fraction));
//                        btn.revalidate();
//                        btn.repaint();
//                    });
//                }
//            }
//
//            @Override
//            public void end(Animator source) {
//                SwingUtilities.invokeLater(() -> {
//                    btn.setHorizontalTextPosition(textposition);
//                    btn.setIcon(originalIcon);
//                    btn.repaint();
//                });
//            }
//        }).build();
//        animator.start();
//
//
//    }


}
