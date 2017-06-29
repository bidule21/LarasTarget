package main.java.de.flashheart.lara;

import java.io.File;

/**
 * Created by tloehr on 17.06.17.
 */
public class Tools {

    public static String getMissionboxDirectory() {
        return (isArm() ? "/home/pi" : System.getProperty("user.home")) + File.separator + "larastarget";
    }

    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isArm() {

        String os = System.getProperty("os.arch").toLowerCase();
        return (os.indexOf("arm") >= 0);

    }
}
