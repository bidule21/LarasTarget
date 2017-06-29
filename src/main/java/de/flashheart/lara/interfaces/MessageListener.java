package main.java.de.flashheart.lara.interfaces;

import main.java.de.flashheart.lara.interfaces.MessageEvent;

import java.util.EventListener;

/**
 * Created by tloehr on 25.05.15.
 */
public interface MessageListener extends EventListener{

    void messageReceived(MessageEvent messageEvent);

}
