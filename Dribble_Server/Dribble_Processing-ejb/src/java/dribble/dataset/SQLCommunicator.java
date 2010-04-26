/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dribble.dataset;

import dribble.common.*;

import java.sql.*;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 *
 * @author Dribble
 */
public class SQLCommunicator implements Dataset {

    static final Logger logger = Logger.getLogger("SQLCommunicator");

    private String connectionUrl;
    private Connection con;

    public SQLCommunicator() {
logger.info("Inside SQLConstructor");
        try {
            
            connectionUrl = "jdbc:derby://localhost:1527/DribbleDerbyDB";
            con = DriverManager.getConnection(connectionUrl, "APP", "dribble");
        } catch (SQLException e) {
            logger.severe("SQLexception: " + e.toString());
        }

        logger.info("Constructed SQLCommunicator");

    }

    public boolean addDrib(Drib m) {

        logger.info("Add Drib");

        try {

            Statement stmt = con.createStatement();
            logger.info("Connection Started");
            ResultSet rs = stmt.executeQuery("SELECT ID "
                    + "FROM DRIBBLE_SYSTEM_SUBJECTS "
                    + "WHERE ID=" + m.getSubject().getSubjectID());
            logger.info("If not in queue execute was null");
            if (rs == null) {
                logger.info("wasnull executing");
                stmt.execute("CREATE TABLE " + m.getSubject().getName()
                        + "(DRIB VARCHAR(144),"
                        + "LAT double,"
                        + "LONG double,"
                        + "CURRENTIME time,"
                        + "DRIBID Integer,"
                        + "DRIBPOPULARITY integer"
                        + ")");

                logger.info("Message input into database");
                
                stmt.execute("INSERT INTO DRIBBLE_SYSTEM_SUBJECTS"  + " (NAME,ID,LATITUDE,LONGITUDE,VIEWS,POSTS,POPULARITY,CURRENTIME)"
                    + "VALUES (" + "\'"+m.getSubject().getName()+"\'" + "," + m.getSubject().getSubjectID()
                    + "," + m.getSubject().getLatitude() + "," + m.getSubject().getLongitude()
                    +"," +m.getSubject().getNumViews()+","+m.getSubject().getNumPosts()
                    +","+m.getSubject().getPopularity()+","+m.getSubject().getTime()+")");
            }
            logger.info("Skipped table creation");
            
            
           stmt.execute("INSERT INTO " + m.getSubject().getName() + " (DRIB,LAT,LONG,CURRENTIME,DRIBID,DRIBPOPULARITY)"
                    + "VALUES (" + "\'"+m.getText()+"\'" + "," + m.getLatitude() + "," + m.getTime() + "," + m.getTime() + "," + m.getMessageID() + "," + m.getPopularity() + ")");
            return true;
        } catch (SQLException e) {
            logger.severe("Error adding Drib: " + e.getMessage());
            return false;
        }


    }

    public ArrayList<Drib> getDribs(DribSubject s, double lat, double longitude, double radius) {

        logger.info("Get Dribs");

        ArrayList<Drib> DribList = new ArrayList<Drib>();
        double latlimit1 = lat + radius;
        double latlimit2 = lat - radius;
        double longlimit1 = longitude + radius;
        double longlimit2 = longitude - radius;
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT FROM " + s.getSubjectID()
                    + "WHERE LAT <" + latlimit1
                    + "AND LAT >" + latlimit2
                    + "AND LONG <" + longlimit1
                    + "AND LONG >" + longlimit2);
            while (rs.next()) {
                Drib m = getDrib(s, rs.getInt("DRIBID"));
                DribList.add(m);
            }
        } catch (SQLException e) {
            logger.severe("Error getting Dribs: " + e.getMessage());
        }
        return DribList;
    }

    public ArrayList<DribSubject> getDribSubjects(double lat, double longitude, double radius) {

        logger.info("Get Drib Subjects");

        ArrayList<DribSubject> DribList = new ArrayList<DribSubject>();
        try {

            Statement stmt = con.createStatement();
            ResultSet topics=  stmt.executeQuery("SELECT ID FROM DRIBBLE_SYSTEM_DRIBSUBJECTS");
            while(topics.next())
            {
              if (!(getDribs(getDribSubject(topics.getInt("ID")),lat,longitude,radius).isEmpty()))
              {
                  DribList.add(getDribSubject(topics.getInt("ID")));
              }
            }

        } catch (SQLException e) {
            logger.severe("Error getting Topics: " + e.getMessage());
        }
        return DribList;


    }

    public boolean updateDrib(Drib m) {
        //Deletes Original Drib and updates with the new one
        //This is not the most efficient way but is very simple and elegent
        deleteDrib(m);
        addDrib(m);

        logger.info("Updated Drib");

        return true;
    }

    public boolean deleteDrib(Drib m) {

        logger.info("Deleting Drib");

        try {
            Statement stmt = con.createStatement();
            stmt.execute("DELETE FROM " + m.getSubject().getSubjectID()
                    + "WHERE DRIBID=" + m.getMessageID());

            if(m.getSubject().getNumPosts()==1)
            {
                stmt.execute("DELETE FROM  DRIBBLE_SYSTEM_DRIBSUBJECTS"
                    + "WHERE ID=" + m.getSubject().getSubjectID()+")");
                stmt.execute("(DROP TABLE"+ m.getSubject().getSubjectID()+")");
            }

            logger.info("Drib deleted");

            return true;

        } catch (SQLException e) {

            logger.severe("Error deleting Drib: " + e.getMessage());

            return false;
        }
    }

    public Drib getDrib(DribSubject s, int DribID) {

        logger.info("Getting Drib");

        Drib m = new Drib(s, "default", 0, 0);

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT FROM" + s.getSubjectID()
                    + "WHERE DRIBID=" + DribID);
            String DribText = rs.getString("DRIB");
            m.setText(DribText);
            double lat = rs.getDouble("LAT");
            m.setLatitude(lat);
            double longitude = rs.getDouble("LONG");
            m.setLongitude(longitude);
            Date time = rs.getDate("TIME");
            m.setTime(time);
            int popularity = rs.getInt("DRIBPOPULARITY");
            m.setPopularity(popularity);

        } catch (SQLException e) {

            logger.severe("Error getting Drib: " + e.getMessage());

            return null;
        }

        logger.info("Got Drib");

        return m;
    }

    public DribSubject getDribSubject(int SubjectID){
        logger.info("Getting Subject");

        DribSubject s = new DribSubject();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM DRIBBLE_SYSTEM_DRIBSUBJECTS"
                    + "WHERE ID=" + SubjectID);
            String name = rs.getString("NAME");
            s.setName(name);
            double lat = rs.getDouble("LAT");
            s.setLatitude(lat);
            double longitude = rs.getDouble("LONG");
            s.setLongitude(longitude);
            int views=rs.getInt("VIEWS");
            s.setNumViews(views);
            int posts=rs.getInt("POSTS");
            s.setNumViews(posts);
            Date time = rs.getDate("TIME");
            s.setTime(time);
            int popularity = rs.getInt("POPULARITY");
            s.setPopularity(popularity);

        } catch (SQLException e) {

            logger.severe("Error getting subject: " + e.getMessage());

            return null;
        }

        logger.info("Got subject");

        return s;
    }

    public DribSubject getDribSubject(String SubjectName){
        logger.info("Getting Subject");

        DribSubject s = new DribSubject();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID FROM DRIBBLE_SYSTEM_DRIBSUBJECTS"
                    + "WHERE NAME=" + SubjectName);
           s=getDribSubject(rs.getInt("ID"));

        } catch (SQLException e) {

            logger.severe("Error getting subject: " + e.getMessage());

            return null;
        }

        logger.info("Got subject");

        return s;
    }

}
