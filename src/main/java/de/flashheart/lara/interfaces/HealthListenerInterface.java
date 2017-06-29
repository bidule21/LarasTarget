package main.java.de.flashheart.lara.interfaces;

import java.util.EventListener;

/**
 * Created by tloehr on 29.06.17.
 */
public interface HealthListenerInterface extends EventListener {
    void healthChangedBy(long deltaHealth);
}
