
package dribble.processing;

import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author Dribble
 */
public abstract class DribbleIdentifier {
    
    private static HashSet identifiers = new HashSet();
    private static Random generator = new Random(System.currentTimeMillis());

    public static int getUniqueID() {

        //Generate a new 32 bit random integer
        int id = Math.abs(generator.nextInt());

        //Ensure that the integer is not used
        while(identifiers.contains(id) || id == 0) {
            id = Math.abs(generator.nextInt());
        }

        //Add to the set of current identifiers
        identifiers.add(id);

        //Return the identifier
        return id;

    }

    public static void deleteID(int id) {

        //Remove the specific id from the set
        identifiers.remove(id);
        
    }

}
