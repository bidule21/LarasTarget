package de.flashheart.lara.jobs;

import de.flashheart.lara.tools.RGBValueBean;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.*;

import java.util.ArrayList;


/**
 * Created by tloehr on 14.07.16.
 */
public class PinHandlerRGBJob implements Job, InterruptableJob {


    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Logger logger = Logger.getLogger(getClass().getName());

        try {
            logger.setLevel((Level) context.getScheduler().getContext().get("loglevel"));
            ArrayList<RGBValueBean> rgbValueBeans = (ArrayList<RGBValueBean>) context.getScheduler().getContext().get("rgbValueBeans");

            for (RGBValueBean rgbValueBean : rgbValueBeans) {
                rgbValueBean.showLEDs();
            }
        } catch (SchedulerException e) {
            logger.trace(e);
            System.exit(0);
        } catch (InterruptedException e) {
            throw new JobExecutionException(e);
        }
    }
}
