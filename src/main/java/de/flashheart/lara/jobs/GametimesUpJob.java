package de.flashheart.lara.jobs;

import de.flashheart.lara.listeners.GamemodeListener;
import org.apache.log4j.Logger;
import org.quartz.*;

/**
 * Created by tloehr on 30.06.17.
 */
@DisallowConcurrentExecution
public class GametimesUpJob implements Job, InterruptableJob {
    public static final String name = "gametimejob1";
    private final GamemodeListener gamemodeListener;
    Logger logger = Logger.getLogger(getClass());

    public GametimesUpJob(GamemodeListener gamemodeListener) {
        this.gamemodeListener = gamemodeListener;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        gamemodeListener.targetDefended();
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        // nothing to do here
    }
}
