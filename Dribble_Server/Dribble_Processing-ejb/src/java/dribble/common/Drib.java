package dribble.common;


import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Dribble
 */

@XmlRootElement
public class Drib implements Serializable{


    private String text;
    private double latitude;
    private double longitude;
    private int messageID;
    private Date time;
    private DribSubject subject;
    private int likeCount;
    private int popularity;
    public static final int maxLength = 144;

    public Drib() {
        
    }

    //Set the message constructor
    public Drib(DribSubject Message_subject, String text, double latitude, double longitude)
    {
        //Set the message subject
        setSubject(Message_subject);
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
    public void setTime(Date time)
    {
        this.time = time;
    }

    public Date getTime()
    {
        return this.time;
    }

    //Set and get the subject for the message
    public void setSubject(DribSubject subject)
    {
        this.subject = subject;
    }

    public DribSubject getSubject()
    {
        return this.subject;
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
