package kornell.gui.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.logging.client.LogConfiguration;

import kornell.gui.client.util.ClientProperties;

public class Kornell implements EntryPoint {

    Logger logger = Logger.getLogger(Kornell.class.getName());
    ClientFactory clientFactory = GWT.create(ClientFactory.class);

    @Override
    public void onModuleLoad() {
        loggerSoundcheck();
        final long t0 = System.currentTimeMillis();
        ClientProperties.removeCookie(ClientProperties.X_KNL_TOKEN);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                startLMS();
                long t1 = System.currentTimeMillis();
                logger.info("Kornell GWT started in [" + (t1 - t0) + " ms]");
            }
        });
    }

    private void loggerSoundcheck() {
        if (LogConfiguration.loggingIsEnabled()) {
            String msg = "Hello from Kornell Client - ";
            logger.finest(msg + "FINEST");
            logger.finer(msg + "FINER");
            logger.fine(msg + "FINE");
            logger.info("INFO");
            logger.warning(msg + "WARNING");
            logger.severe(msg + "SEVERE");
        }

    }

    private void startLMS() {
        clientFactory.startApp();
        clientFactory.logState();
    }

}
