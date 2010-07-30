package dribble.dataset;

import dribble.common.*;

import java.util.ArrayList;

/**
 *
 * @author Dribble
 */
public interface Dataset {

    boolean addDrib(Drib m);

    ArrayList<Drib> getDribs(int subjectID, long lat, long longitude, long radius);

    ArrayList<DribSubject> getDribSubjects(long lat, long longitude, long radius);

    boolean deleteDrib(Drib m);

    boolean updateDrib(Drib m);

    Drib getDrib(int subjectID, int DribID);

    boolean deleteOldDribSubjects(long qualifyingTime);

    boolean deleteOldDribs(long qualifyingTime);
}