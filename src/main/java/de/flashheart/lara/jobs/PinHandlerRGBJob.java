package de.flashheart.lara.jobs;

import de.flashheart.lara.tools.RGBBean;
import org.apache.log4j.Logger;
import org.quartz.*;

import java.util.ArrayList;


/**
 * Created by tloehr on 14.07.16.
 */
@DisallowConcurrentExecution
public class PinHandlerRGBJob implements Job, InterruptableJob {


    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Logger logger = Logger.getLogger(getClass().getName());

        try {
            ArrayList<RGBBean> rgbBeans = (ArrayList<RGBBean>) context.getJobDetail().getJobDataMap().get("ledpattern");

            for (RGBBean rgbBean : rgbBeans) {
                rgbBean.showLEDs();
            }
        } catch (InterruptedException e) {
            throw new JobExecutionException(e);
        }
    }




}
