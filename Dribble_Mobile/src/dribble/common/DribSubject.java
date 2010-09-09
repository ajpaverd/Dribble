// Authors: Dribble
// Date: 24 April 2010
// Class: Drib

package dribble.common;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.util.Log;

@Root
public class DribSubject {
	@Element
    private String name;
	@Element
    private int subjectID;
	@Element
	private int latitude;
	@Element
    private int longitude;
	@Element
    private int numViews;
	@Element
    private int numPosts;
	@Element
    private long time;
	@Element
    private int popularity;
	
    public final static int maxLength = 20;
    private static final String TAG = "DribTopic";

    public DribSubject ()
    {
    }
    
    public DribSubject (String name, int subjectID, int latitude, int longitude, int numViews, int numPosts, long time, int popularity)
    {
    	Log.i(TAG, "New DribTopic Created");
        setName(name);
        setSubjectID (subjectID);
        setLatitude(latitude);
        setLongitude(longitude);
        setNumViews(numViews);
        setNumPosts(numPosts);
        setPopularity(popularity);
    }

    public void setName(String name)
    {
        //Make sure the string length is not larger than 144 characters
        if(name.length()>=maxLength)
           name = name.substring(0,maxLength);
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    //get and set the id for a specific topic
    public void setSubjectID(int id)
    {
        this.subjectID = id;
    }

    public int getSubjectID()
    {
        return this.subjectID;
    }    

    public void setLatitude(int latitude)
    {
        this.latitude = latitude;
    }

    public int getLatitude()
    {
        return latitude;
    }

    public void setLongitude(int longitude)
    {
        this.longitude = longitude;
    }

    public int getLongitude()
    {
        return this.longitude;
    }

//get and set the id for a specific topic
    public void setNumViews(int views)
    {
        this.numViews = views;
    }

    public int getNumViews()
    {
        return this.numViews;
    }

  //get and set the id for a specific topic
    public void setNumPosts(int posts)
    {
        this.numPosts = posts;
    }

    public int getNumPosts()
    {
        return this.numPosts;
    }

     //Set a Time object with current date and time
    public void setTime(long time)
    {
        this.time = time;
    }
    public long getTime()
    {
        return this.time;
    }
 
   public void setPopularity(int popularity)
    {
        this.popularity = popularity;
    }

    public int getPopularity()
    {
        return popularity;
    }
}
