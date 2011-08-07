
package dribble.processing;

import dribble.common.Drib;

import java.util.Comparator;

/**
 *
 * @author Dribble
 */
public class DribComparator implements Comparator<Drib> {

    public int compare(Drib a, Drib b) throws ClassCastException{

        return b.getPopularity() - a.getPopularity();
        
    }

}
