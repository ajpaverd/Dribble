/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.common;


import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author Daniel
 */
@XmlRootElement(name="dribble.common.DribTopic")
public class DribTopic implements Serializable{

    private String name;
    private int topicID;
    private double latitude;
    private double longitude;
    private int numViews;
    private int numPosts;
    private Date time;
    //private Calendar time = Calendar.getInstance();
    private int popularity;
    public final static int maxLength = 20;


    public DribTopic ()
    {
        //setName(name);
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
    public void setTopicID(int id)
    {
        this.topicID = id;
    }

    public int getTopicID()
    {
        return this.topicID;
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
//    public void setTime(Calendar time)
//    {
//        this.time = time;
//    }
//    public Calendar getTime()
//    {
//        return this.time;
//    }

    public void setTime(Date time)
    {
        this.time = time;
    }
    public Date getTime()
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