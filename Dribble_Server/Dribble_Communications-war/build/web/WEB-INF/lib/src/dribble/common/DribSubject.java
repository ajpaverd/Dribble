
package dribble.common;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author Dribble
 */

@XmlRootElement(name="dribble.common.DribSubject")
public class DribSubject implements Serializable{

    private String name;
    private int subjectID;
    private int latitude;
    private int longitude;
    private int numViews;
    private int numPosts;
    private long time;
    private int popularity;
    public final static int maxLength = 20;


    public DribSubject ()
    {
    }

    public void setName(String name)
    {
        //Make sure the string length is not larger than 20 characters
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

    public void setNumViews(int views)
    {
        this.numViews = views;
    }

    public int getNumViews()
    {
        return this.numViews;
    }

    public void setNumPosts(int posts)
    {
        this.numPosts = posts;
    }

    public int getNumPosts()
    {
        return this.numPosts;
    }

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