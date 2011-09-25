// Authors: Dribble
// Date: 24 April 2010
// Class: Drib

package com.dribble.common;

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
	private double latitude;
	@Element
    private double longitude;
	@Element
    private int numViews;
	@Element
    private int numPosts;
	@Element
    private long time;
	@Element
    private int popularity;
	
    public static final int maxLength = 20;
    private static final String TAG = "DribTopic";

    public DribSubject ()
    {
    }
    
    public DribSubject (String name, double latitude, double longitude)
    {
    	Log.i(TAG, "New DribTopic Created");

    	numViews = 0;
    	numPosts = 0;
    	subjectID = 0;
    	popularity = 0;
        // Set fields directly for faster performance
    	this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLongitude()
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
