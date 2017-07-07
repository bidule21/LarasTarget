package de.flashheart.lara.jobs;

import de.flashheart.lara.handlers.GamemodeHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.*;

/**
 * Created by tloehr on 30.06.17.
 */

public class GametimeNotificationJob implements Job, InterruptableJob {
    public static final String name = "gametimerjob1";


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Logger logger = Logger.getLogger(getClass());

        GamemodeHandler gamemodeHandler = null;
        try {
            logger.setLevel((Level) jobExecutionContext.getScheduler().getContext().get("loglevel"));
            logger.debug("gametimerjob1 running");
            gamemodeHandler = (GamemodeHandler) jobExecutionContext.getScheduler().getContext().get("gamemodelistener");
            gamemodeHandler.gametimeNotification();
        } catch (SchedulerException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        // nothing to do here, no sleep() usage
    }

}
