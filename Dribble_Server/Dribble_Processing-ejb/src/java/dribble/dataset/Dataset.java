package dribble.dataset;

import dribble.common.*;

import java.util.ArrayList;

/**
 *
 * @author Dribble
 */
public interface Dataset {

    boolean addDrib(Drib m);

    ArrayList<Drib> getDribs(DribSubject t, double lat, double longitude, double radius);

    ArrayList<DribSubject> getDribSubjects(double lat, double longitude, double radius);

    boolean deleteDrib(Drib m);

    boolean updateDrib(Drib m);

    Drib getDrib(DribSubject t, int DribID);
}
