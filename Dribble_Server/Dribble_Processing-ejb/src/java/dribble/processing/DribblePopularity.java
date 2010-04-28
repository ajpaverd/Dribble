package dribble.processing;

import dribble.common.Drib;
import dribble.common.DribSubject;

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

    public static ArrayList<DribSubject> rankSubjects(ArrayList<DribSubject> input, int latitude, int longitude) {

        ListIterator<DribSubject> iterator = input.listIterator();
        DribSubject current;

        int posts;
        int views;
        int deltaLatitude;
        int deltaLongitude;
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

            //Location difference in kilometers (assuming 8000 microDegrees = 1 km)
            deltaLocation = Math.sqrt(Math.pow(deltaLatitude, 2) + Math.pow(deltaLongitude, 2))/8000.0;

            //Time difference in 5 minute multiples
            deltaTime = ((System.currentTimeMillis() - current.getTime()) / (300000.0));

            popularityDouble = (100 * posts + 100 * views) * Math.pow(0.5, deltaLocation + deltaTime);
            popularity = (int) Math.ceil(popularityDouble);

            current.setPopularity(popularity);

        }

        //Use a built-in modified version of merge-sort to sort by popularity
        Collections.sort(input, dribSubjectComparator);

        return input;

    }

    public static ArrayList<Drib> rankDribs(ArrayList<Drib> input, int latitude, int longitude) {

        ListIterator<Drib> iterator = input.listIterator();
        Drib current;

        int likeCount;
        int deltaLatitude;
        int deltaLongitude;
        double deltaLocation;
        double deltaTime;
        double popularityDouble;
        int popularity;

        while (iterator.hasNext() == true) {

            current = iterator.next();

            likeCount = current.getLikeCount();
            deltaLatitude = latitude - current.getLatitude();
            deltaLongitude = longitude - current.getLongitude();

            //Location difference in kilometers (assuming 8000 microDegrees = 1 km)
            deltaLocation = Math.sqrt(Math.pow(deltaLatitude, 2) + Math.pow(deltaLongitude, 2))/8000.0;

            //Time difference in 5 minute multiples
            deltaTime = ((System.currentTimeMillis() - current.getTime()) / (300000.0));

            popularityDouble = (1000 * likeCount) * Math.pow(0.5, deltaLocation + deltaTime);
            popularity = (int) Math.ceil(popularityDouble);

            current.setPopularity(popularity);

        }

        //Use a built-in modified version of merge-sort to sort by popularity
        Collections.sort(input, dribComparator);

        return input;

    }
}
