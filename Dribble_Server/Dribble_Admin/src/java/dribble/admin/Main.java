/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.admin;


import java.util.logging.Logger;

/**
 *
 * @author Dribble
 */
public class Main {

    /**
     * @param args the command line arguments
     */

    static final Logger logger = Logger.getLogger("Dribble_Admin_Main");

    public static void main(String[] args) {

        logger.info("Starting Dribble_Deleter...");

        Dribble_Deleter deleter = new Dribble_Deleter();
        deleter.start();

        logger.info("Dribble_Deleter started");
        

    }

}
