package de.flashheart.lara.interfaces;

import java.util.EventListener;

/**
 * Created by tloehr on 25.05.15.
 */
public interface MessageListener extends EventListener{

    void messageReceived(MessageEvent messageEvent);

}
