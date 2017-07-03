package de.flashheart.lara.jobs;

import de.flashheart.lara.listeners.GamemodeListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.*;

/**
 * Created by tloehr on 30.06.17.
 */
@DisallowConcurrentExecution
public class GametimeIsUpJob implements Job, InterruptableJob {
    public static final String name = "gametimejob1";
    Logger logger = Logger.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Logger logger = Logger.getLogger(getClass());

        GamemodeListener gamemodeListener = null;
        try {
            logger.setLevel((Level) jobExecutionContext.getScheduler().getContext().get("loglevel"));
            logger.debug("Starting the Game");
            gamemodeListener = (GamemodeListener) jobExecutionContext.getScheduler().getContext().get("gamemodelistener");
            gamemodeListener.targetDefended();
        } catch (SchedulerException e) {
            logger.fatal(e);
            System.exit(0);
        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        // nothing to do here
    }
}
