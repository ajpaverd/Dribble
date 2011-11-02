package dribble.dataset;

import com.dribble.common.*;

import java.util.ArrayList;

/**
 *
 * @author Dribble
 */
public interface Dataset {

    boolean addDrib(Drib m);
    
    //boolean addSubject(DribSubject m);

    ArrayList<Drib> getDribs(int subjectID, double lat, double longitude, long radius);

    ArrayList<DribSubject> getDribSubjects(double lat, double longitude, long radius);

    boolean deleteDrib(Drib m);

    boolean updateDrib(Drib m);

    Drib getDrib(int subjectID, int DribID);

    boolean deleteOldDribSubjects(long qualifyingTime);

    boolean deleteOldDribs(long qualifyingTime);
}
