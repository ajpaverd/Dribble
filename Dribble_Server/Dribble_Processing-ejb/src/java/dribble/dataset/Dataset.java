package dribble.dataset;

import dribble.common.*;

import java.util.ArrayList;

/**
 *
 * @author Dribble
 */
public interface Dataset {

    boolean addDrib(Drib m);

    ArrayList<Drib> getDribs(DribSubject t, int lat, int longitude, int radius);

    ArrayList<DribSubject> getDribSubjects(int lat, int longitude, int radius);

    boolean deleteDrib(Drib m);

    boolean updateDrib(Drib m);

    Drib getDrib(DribSubject t, int DribID);
}
