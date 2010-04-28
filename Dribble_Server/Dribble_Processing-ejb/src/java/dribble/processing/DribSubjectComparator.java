
package dribble.processing;


import dribble.common.DribSubject;

import java.util.Comparator;

/**
 *
 * @author Dribble
 */
public class DribSubjectComparator implements Comparator<DribSubject> {

    public int compare(DribSubject a, DribSubject b) throws ClassCastException {

        return b.getPopularity() - a.getPopularity();
        
    }

}
