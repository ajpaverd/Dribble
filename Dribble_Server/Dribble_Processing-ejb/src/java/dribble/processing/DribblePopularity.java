package dribble.processing;

import com.dribble.common.Drib;
import com.dribble.common.DribSubject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

/**
 *
 * @author Dribble
 */
public abstract class DribblePopularity {

    private static DribComparator dribComparator = new DribComparator();
    private static DribSubjectComparator dribSubjectComparator  = new DribSubjectComparator();

    // TODO use mongo 
    public static ArrayList<DribSubject> rankSubjects(ArrayList<DribSubject> input, double latitude, double longitude) {

        ListIterator<DribSubject> iterator = input.listIterator();
        DribSubject current;

        int posts;
        int views;
        double deltaLatitude;
        double deltaLongitude;
        double deltaLocation;
        double deltaTime;
        double popularityDouble;
        int popularity;

        while (iterator.hasNext() == true) {

            current = iterator.next();

            posts = current.getNumPosts();
            views = current.getNumViews();
            deltaLatitude = latitude - current.getLatitude();
            deltaLongitude = longitude - current.getLongitude();

            // TODO When using mongoDB geonear function returns distance between current location and found location
            //
            //Location difference in kilometers
            deltaLocation = Math.sqrt(Math.pow(deltaLatitude, 2) + Math.pow(deltaLongitude, 2));

            //Time difference in 5 minute multiples
            deltaTime = ((System.currentTimeMillis() - current.getTime()) / (300000.0));

            popularityDouble = (1000 * posts + 1000 * views) * Math.pow(0.5, deltaLocation + deltaTime);
            popularity = (int) Math.ceil(popularityDouble);

            current.setPopularity(popularity);

        }

        //Use a built-in modified version of merge-sort to sort by popularity
        Collections.sort(input, dribSubjectComparator);

        return input;

    }

    public static ArrayList<Drib> rankDribs(ArrayList<Drib> input, double latitude, double longitude) {

        ListIterator<Drib> iterator = input.listIterator();
        Drib current;

        int likeCount;
        double deltaLatitude;
        double deltaLongitude;
        double deltaLocation;
        double deltaTime;
        double popularityDouble;
        int popularity;

        while (iterator.hasNext() == true) {

            current = iterator.next();

            likeCount = current.getLikeCount();
            deltaLatitude = latitude - current.getLatitude();
            deltaLongitude = longitude - current.getLongitude();

            //Location difference in kilometers
            deltaLocation = Math.sqrt(Math.pow(deltaLatitude, 2) + Math.pow(deltaLongitude, 2));

            //Time difference in 5 minute multiples
            deltaTime = ((System.currentTimeMillis() - current.getTime()) / (300000.0));

            popularityDouble = (1000 + 1000 * likeCount) * Math.pow(0.5, deltaLocation + deltaTime);
            popularity = (int) Math.ceil(popularityDouble);

            current.setPopularity(popularity);

        }

        //Use a built-in modified version of merge-sort to sort by popularity
        Collections.sort(input, dribComparator);

        return input;

    }
}
