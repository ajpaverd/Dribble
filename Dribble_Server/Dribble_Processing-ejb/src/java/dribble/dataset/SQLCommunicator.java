package dribble.dataset;

import dribble.common.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;

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
/*
            Statement stmt = con.createStatement();
            logger.info("Connection Started");
            ResultSet rs = stmt.executeQuery("SELECT ID "
                    + "FROM DRIBBLE_SYSTEM_SUBJECTS ");

            if (rs.next() == false) {
                logger.info("Creating new subject table");
                stmt.execute("CREATE TABLE \"DRIBBLE_SYSTEM_SUBJECTS\""
                        + "(DRIB VARCHAR(144),"
                        + "LAT BIGINT,"
                        + "LONG BIGINT,"
                        + "CURRENTIME BIGINT,"
                        + "DRIBID BIGINT,"
                        + "DRIBPOPULARITY BIGINT"
                        + ")");

                logger.info("Subject table created");
            }
*/

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

            if (rs.next() == false) {
                logger.info("Creating new subject table");
                stmt.execute("CREATE TABLE \"" + m.getSubject().getSubjectID() + "\""
                        + "(DRIB VARCHAR(144),"
                        + "LAT BIGINT,"
                        + "LONG BIGINT,"
                        + "CURRENTIME BIGINT,"
                        + "DRIBID BIGINT,"
                        + "DRIBPOPULARITY BIGINT"
                        + ")");

                logger.info("Subject table created");

                stmt.execute("INSERT INTO DRIBBLE_SYSTEM_SUBJECTS" + " (NAME,ID,LATITUDE,LONGITUDE,VIEWS,POSTS,POPULARITY,CURRENTIME)"
                        + "VALUES (" + "\'" + m.getSubject().getName() + "\'," + m.getSubject().getSubjectID()
                        + "," + m.getSubject().getLatitude() + "," + m.getSubject().getLongitude()
                        + "," + m.getSubject().getNumViews() + "," + m.getSubject().getNumPosts()
                        + "," + m.getSubject().getPopularity() + "," + m.getSubject().getTime() + ")");
            }

            logger.info("Adding Drib to subject table");
            stmt.execute("INSERT INTO \"" + m.getSubject().getSubjectID() + "\" (DRIB,LAT,LONG,CURRENTIME,DRIBID,DRIBPOPULARITY)"
                    + "VALUES (" + "\'" + m.getText() + "\'" + "," + m.getLatitude() + "," + m.getLongitude() + "," + m.getTime() + "," + m.getMessageID() + "," + m.getPopularity() + ")");

            logger.info("Drib successfully added to table");
            
            return true;

        } catch (SQLException e) {
            logger.severe("Error adding Drib: " + e.getMessage());
            return false;
        }


    }

    public ArrayList<Drib> getDribs(DribSubject s, int lat, int longitude, int radius) {

        logger.info("Get Dribs");

        ArrayList<Drib> DribList = new ArrayList<Drib>();
        int latlimit1 = lat + radius;
        int latlimit2 = lat - radius;
        int longlimit1 = longitude + radius;
        int longlimit2 = longitude - radius;
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

    public ArrayList<DribSubject> getDribSubjects(int lat, int longitude, int radius) {

        logger.info("Get Drib Subjects");

        ArrayList<DribSubject> DribList = new ArrayList<DribSubject>();
        try {

            Statement stmt = con.createStatement();
            ResultSet topics = stmt.executeQuery("SELECT ID FROM DRIBBLE_SYSTEM_DRIBSUBJECTS");
            while (topics.next()) {
                if (!(getDribs(getDribSubject(topics.getInt("ID")), lat, longitude, radius).isEmpty())) {
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

            if (m.getSubject().getNumPosts() == 1) {
                stmt.execute("DELETE FROM  DRIBBLE_SYSTEM_DRIBSUBJECTS"
                        + "WHERE ID=" + m.getSubject().getSubjectID() + ")");
                stmt.execute("(DROP TABLE" + m.getSubject().getSubjectID() + ")");
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
            int lat = rs.getInt("LAT");
            m.setLatitude(lat);
            int longitude = rs.getInt("LONG");
            m.setLongitude(longitude);
            Date time = rs.getDate("TIME");
            m.setTime(time.getTime());
            int popularity = rs.getInt("DRIBPOPULARITY");
            m.setPopularity(popularity);

        } catch (SQLException e) {

            logger.severe("Error getting Drib: " + e.getMessage());

            return null;
        }

        logger.info("Got Drib");

        return m;
    }

    public DribSubject getDribSubject(int SubjectID) {
        logger.info("Getting Subject");

        DribSubject s = new DribSubject();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM DRIBBLE_SYSTEM_DRIBSUBJECTS"
                    + "WHERE ID=" + SubjectID);
            String name = rs.getString("NAME");
            s.setName(name);
            int lat = rs.getInt("LAT");
            s.setLatitude(lat);
            int longitude = rs.getInt("LONG");
            s.setLongitude(longitude);
            int views = rs.getInt("VIEWS");
            s.setNumViews(views);
            int posts = rs.getInt("POSTS");
            s.setNumViews(posts);
            Date time = rs.getDate("TIME");
            s.setTime(time.getTime());
            int popularity = rs.getInt("POPULARITY");
            s.setPopularity(popularity);

        } catch (SQLException e) {

            logger.severe("Error getting subject: " + e.getMessage());

            return null;
        }

        logger.info("Got subject");

        return s;
    }

    public DribSubject getDribSubject(String SubjectName) {
        logger.info("Getting Subject");

        DribSubject s = new DribSubject();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID FROM DRIBBLE_SYSTEM_DRIBSUBJECTS"
                    + "WHERE NAME=" + SubjectName);
            s = getDribSubject(rs.getInt("ID"));

        } catch (SQLException e) {

            logger.severe("Error getting subject: " + e.getMessage());

            return null;
        }

        logger.info("Got subject");

        return s;
    }
}
