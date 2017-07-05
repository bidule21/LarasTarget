package de.flashheart.lara.jobs;

import de.flashheart.lara.tools.MyGpioPinPwmOutput;
import de.flashheart.lara.tools.RGBBean;
import org.apache.log4j.Logger;
import org.quartz.*;

import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Created by tloehr on 14.07.16.
 */
@DisallowConcurrentExecution
public class PinHandlerRGBJob implements Job, InterruptableJob {
    MyGpioPinPwmOutput pwmRed, pwmGreen, pwmBlue;

    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Logger logger = Logger.getLogger(getClass().getName());

        try {

            pwmRed = (MyGpioPinPwmOutput) context.getScheduler().getContext().get("pwmRed");
            pwmGreen = (MyGpioPinPwmOutput) context.getScheduler().getContext().get("pwmGreen");
            pwmBlue = (MyGpioPinPwmOutput) context.getScheduler().getContext().get("pwmBlue");

            if (context.getJobDetail().getJobDataMap().get("ledpattern") instanceof String) {
                String pattern = (String) context.getJobDetail().getJobDataMap().get("ledpattern");

                for (RGBBean rgbBean : parsePattern(pattern)) {
                    rgbBean.showLEDs();
                }
            } else {
                ArrayList<RGBBean> list = (ArrayList<RGBBean>) context.getJobDetail().getJobDataMap().get("ledpattern");

                for (RGBBean rgbBean : list) {
                    rgbBean.showLEDs();
                }
            }


        } catch (InterruptedException e) {
            throw new JobExecutionException(e);
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.fatal(e);
            System.exit(0);
        }
    }


    private ArrayList<RGBBean> parsePattern(String pattern) {
        ArrayList<RGBBean> beans = new ArrayList<RGBBean>();

        StringTokenizer st = new StringTokenizer(pattern, ",");

        while (st.hasMoreElements()) {
            int redValue = Integer.parseInt(st.nextToken());
            int greenValue = Integer.parseInt(st.nextToken());
            int blueValue = Integer.parseInt(st.nextToken());
            long ms = Long.parseLong(st.nextToken());
            beans.add(new RGBBean(pwmRed, pwmGreen, pwmBlue, redValue, greenValue, blueValue, ms));
        }

        return beans;
    }


}
