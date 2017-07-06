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

    private RGBBean currentRGBBean;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        if (currentRGBBean != null) currentRGBBean.interrupt();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Logger logger = Logger.getLogger(getClass().getName());

        try {

            ArrayList<RGBBean> list = (ArrayList<RGBBean>) context.getJobDetail().getJobDataMap().get("ledpattern");

            for (RGBBean rgbBean : list) {
                currentRGBBean = rgbBean;
                rgbBean.showLEDs();
            }
            currentRGBBean = null;

        } catch (InterruptedException e) {
            logger.debug("intterupted");
        }
    }



}
