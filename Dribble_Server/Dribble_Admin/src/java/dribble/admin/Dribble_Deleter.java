package dribble.admin;


import java.util.logging.Logger;

import dribble.common.*;
import dribble.dataset.*;

/**
 *
 * @author Dribble
 */
public class Dribble_Deleter extends Thread {

    static final Logger logger = Logger.getLogger("Dribble_Deleter");
    private Dataset dataset;

    public Dribble_Deleter() {
        dataset = new SQLCommunicator();
        logger.info("Deleter connected to DB");
    }

    @Override
    public void run() {

        logger.info("Running deleter");

        int count = 0;

        while (true) {

            long qualifyingTime = System.currentTimeMillis() - 1800000;
            dataset.deleteOldDribSubjects(qualifyingTime);
            dataset.deleteOldDribs(qualifyingTime);

            try {
                this.currentThread().sleep(60000);
            } catch (InterruptedException ie) {
                logger.severe("Interrupted exception");
            }

        }

    }
}
