/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.common;


import java.io.Serializable;
import java.util.Calendar;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Daniel
 */
@XmlRootElement
/**
 *
 * @author Daniel
 */
public class Drib implements Serializable{
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

    private String text;
    private double latitude;
    private double longitude;
    private int messageID;
    private Calendar time = Calendar.getInstance();
    private DribTopic topic;
    private int likeCount;
    private int popularity;
    public static final int maxLength = 144;

    public Drib() {
        
    }

    //Set the message constructor
    public Drib(DribTopic Message_topic, String text, double latitude, double longitude)
    {
        //Set the message topic
        setTopic(Message_topic);
        setText(text);
        setLatitude(latitude);
        setLongitude(longitude);

    }
    //Set and get the message text
    public void setText(String text)
    {
        //Make sure the string length is not larger than 144 characters
        if(text.length()>=maxLength)
           text = text.substring(0,maxLength);
        this.text = text;
    }

    public String getText()
    {
        return text;
    }
    //Set and get the latitude coordinates of the message
    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLatitude()
    {
        return latitude;
    }
    //Set and get the longitude coordinates of the message
    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLongitude()
    {
        return this.longitude;
    }
    //Set a Time object with current date and time
    public void setTime(Calendar time)
    {
        this.time = time;
    }
    public Calendar getTime()
    {
        return this.time;
    }
    //Set and get the topic for the message
    public void setTopic(DribTopic topic)
    {
        this.topic = topic;
    }
    public DribTopic getTopic()
    {
        return this.topic;
    }


    //Set and get the message id
    public void setMessageID(int message_id)
    {
        this.messageID = message_id;

    }
    public int getMessageID()
    {
        return this.messageID;

    }
     //Set and get the amount of users that like the message
    public void setLikeCount(int like)
    {
        this.likeCount = like;
    }

     public int getLikeCount()
    {
        return likeCount;
    }

    //Set and get the popularity of the message
    public void setPopularity(int popularity)
    {
        this.popularity = popularity;
    }

     public int getPopularity()
    {
        return popularity;
    }



}
