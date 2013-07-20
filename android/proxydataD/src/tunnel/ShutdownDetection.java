package tunnel;

import java.io.File;
import java.util.Date;
import org.apache.log4j.Logger;

public class ShutdownDetection extends Thread {

    private boolean running = false;
    private static long shutdownDetectionInterval = 1000;
    private static File file = new File("stopAuthServer.dat");
    static Logger logger = Logger.getLogger(ShutdownDetection.class.getName());

    public ShutdownDetection() {
        running = true;
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                try {
                    Thread.sleep(shutdownDetectionInterval);
                    file.delete();
                } catch (Exception ex) {
                }
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            if (file.exists()) {
                try {
                    file.delete();
                } catch (Exception e) {
                }
                break;
            }
            try {
                Thread.sleep(shutdownDetectionInterval);
            } catch (Exception e) {
            }
        }
        ClientServerDataProcessor.serverClientRTPProcessor.stopService();
        ClientServerDataProcessor.serverClientDataProcessor.stopService();
        ClientServerDataProcessor.clientServerDataProcessor.stopService();
        logger.debug("Tunnel is stopped at: " + new Date());
        System.exit(0);
    }
}
