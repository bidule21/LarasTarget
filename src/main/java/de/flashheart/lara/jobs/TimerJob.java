package de.flashheart.lara.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by tloehr on 30.06.17.
 */
public class TimerJob implements Job {
    long gametimer = 0;


    public TimerJob() {
        this.gametimer = 0l;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
          
    }

}
