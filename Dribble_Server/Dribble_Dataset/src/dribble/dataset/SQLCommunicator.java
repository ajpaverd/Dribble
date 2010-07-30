package dribble.dataset;

import dribble.common.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 *
 * @author Dr ibble
 */
public class SQLCommunicator implements Dataset {

    static final Logger logger = Logger.getLogger("SQLCommunicator");
    private String connectionUrl;
    private Connection con;

    //Initialise the database connection in constructor
    public SQLCommunicator() {
        logger.info("Inside SQLConstructor");
        try {
            //Connect to the database
            connectionUrl = "jdbc:derby://localhost:1527/DribbleDerbyDB";

            con = DriverManager.getConnection(connectionUrl, "APP", "dribble");
            logger.info("Connected to Glassfish database");
            DatabaseMetaData md = con.getMetaData();
            //Check if the table exists in the database
            ResultSet rs = md.getTables(null, "APP", "%", null);
            //Add a table if the table does not exist
            if (rs.next() == false) {
                Statement stmt = con.createStatement();
                logger.info("No table in database. Creating main table...");
                stmt.execute("CREATE TABLE DRIBBLE_SYSTEM_SUBJECTS"
                        + "(NAME VARCHAR(20),"
                        + "ID INTEGER NOT NULL,"
                        + "LATITUDE BIGINT,"
                        + "LONGITUDE BIGINT,"
                        + "VIEWS INTEGER default 0,"
                        + "POSTS INTEGER default 0,"
                        + "POPULARITY BIGINT default 0,"
                        + "CURRENTIME BIGINT"
                        + ")");
            } else {
                logger.info("Table exists in database");
            }


        } catch (SQLException e) {
            logger.severe("SQLexception: " + e.toString());
        }

        logger.info("Constructed SQLCommunicator");

    }
    //Adds a drib to the database

    public boolean addDrib(Drib m) {

        logger.info("Adding a Drib to the database...");

        try {

            Statement stmt = con.createStatement();
            logger.info("Add Drib connection established");
            ResultSet rs = stmt.executeQuery("SELECT ID "
                    + "FROM DRIBBLE_SYSTEM_SUBJECTS "
                    + "WHERE ID=" + m.getSubject().getSubjectID());

            if (rs.next() == false) {
                logger.info("Creating new DribSubject table");
                stmt.execute("CREATE TABLE \"" + m.getSubject().getSubjectID() + "\""
                        + "(DRIB VARCHAR(144),"
                        + "LAT BIGINT,"
                        + "LONG BIGINT,"
                        + "CURRENTIME BIGINT,"
                        + "DRIBID BIGINT,"
                        + "DRIBLIKE BIGINT default 0,"
                        + "DRIBPOPULARITY BIGINT default 0"
                        + ")");

                logger.info("Subject table created");

                stmt.execute("INSERT INTO DRIBBLE_SYSTEM_SUBJECTS" + " (NAME,ID,LATITUDE,LONGITUDE,VIEWS,POSTS,POPULARITY,CURRENTIME)"
                        + "VALUES (" + "\'" + m.getSubject().getName() + "\'," + m.getSubject().getSubjectID()
                        + "," + m.getSubject().getLatitude() + "," + m.getSubject().getLongitude()
                        + "," + 0 + "," + 0
                        + "," + 0 + "," + System.currentTimeMillis() + ")");
            }

            logger.info("Adding Drib to subject table");
            stmt.execute("INSERT INTO \"" + m.getSubject().getSubjectID() + "\" (DRIB,LAT,LONG,CURRENTIME,DRIBID,DRIBLIKE, DRIBPOPULARITY)"
                    + "VALUES (" + "\'" + m.getText() + "\'" + "," + m.getLatitude() + "," + m.getLongitude() + "," + System.currentTimeMillis() + "," + m.getMessageID() + "," + m.getLikeCount() + "," + m.getPopularity() + ")");

            stmt.execute("UPDATE DRIBBLE_SYSTEM_SUBJECTS SET CURRENTIME = " + System.currentTimeMillis() + " WHERE ID = " + m.getSubject().getSubjectID());
            logger.info("Drib successfully added to table");

            //Incrementing the number of posts of a subject
            ResultSet updatepost = stmt.executeQuery("SELECT POSTS "
                    + "FROM DRIBBLE_SYSTEM_SUBJECTS "
                    + "WHERE ID=" + m.getSubject().getSubjectID());
            updatepost.next();
            int num_posts = updatepost.getInt("POSTS");

            stmt.execute("UPDATE DRIBBLE_SYSTEM_SUBJECTS SET POSTS = " + (num_posts + 1) + "WHERE ID = " + m.getSubject().getSubjectID());

            return true;

        } catch (SQLException e) {
            logger.severe("Error adding Drib: " + e.getMessage());
            return false;
        }


    }
    //Get a list of Drib messages for a particular subject

    public ArrayList<Drib> getDribs(int subjectID, long lat, long longitude, long radius) {

        logger.info("Getting Drib messages for a particular subject...");
        try {
            Statement stmt = con.createStatement();
            //incrementing number of views for a subject
            ResultSet updatepost = stmt.executeQuery("SELECT VIEWS "
                    + "FROM DRIBBLE_SYSTEM_SUBJECTS "
                    + "WHERE ID=" + subjectID);
            updatepost.next();
            int num_views = updatepost.getInt("VIEWS");

            stmt.execute("UPDATE DRIBBLE_SYSTEM_SUBJECTS SET VIEWS = " + (num_views + 1) + "WHERE ID = " + subjectID);
            stmt.execute("UPDATE DRIBBLE_SYSTEM_SUBJECTS SET CURRENTIME = " + (System.currentTimeMillis()) + "WHERE ID = " + subjectID);
        } catch (SQLException e) {
            logger.severe("Error updtaing views");
        }
        return getDribsInternal(subjectID, lat, longitude, radius);
    }

    //Get a list of Drib messages for a particular subject
    public ArrayList<Drib> getDribsInternal(int subjectID, long lat, long longitude, long radius) {

        logger.info("Getting Drib messages for a particular subject...");

        ArrayList<Drib> DribList = new ArrayList<Drib>();
        long latlimit1 = lat + radius;
        long latlimit2 = lat - radius;
        long longlimit1 = longitude + radius;
        long longlimit2 = longitude - radius;
        try {
            Statement stmt = con.createStatement();
            logger.info("Get Dribs connection established");
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"" + subjectID
                    + "\" WHERE LAT <" + latlimit1
                    + "AND LAT >" + latlimit2
                    + "AND LONG <" + longlimit1
                    + "AND LONG >" + longlimit2);
            //Add Dribs found in table to the dribs list to be sent to the user
            while (rs.next()) {
                Drib m = getDrib(subjectID, rs.getInt("DRIBID"));
                DribList.add(m);
            }


        } catch (SQLException e) {
            logger.severe("Error getting Dribs: " + e.getMessage());
        }
        return DribList;
    }

    //Get all the DribSubjects available in a specific region
    public ArrayList<DribSubject> getDribSubjects(long lat, long longitude, long radius) {

        logger.info("Getting all the DribSubjects...");

        ArrayList<DribSubject> DribList = new ArrayList<DribSubject>();
        try {

            Statement stmt = con.createStatement();
            logger.info("Connection Established");
            ResultSet rs = stmt.executeQuery("SELECT * FROM DRIBBLE_SYSTEM_SUBJECTS");

            //Add the drib subjects to a drib subject list
            while (rs.next()) {
                if (!(getDribsInternal(rs.getInt("ID"), lat, longitude, radius).isEmpty())) {
                    logger.info("Subject Found");
                    DribList.add(getDribSubject(rs.getInt("ID")));
                }
            }

        } catch (SQLException e) {
            logger.severe("Error getting Topics: " + e.getMessage());
        }
        return DribList;


    }

    //Update the Drib
    public boolean updateDrib(Drib m) {

        deleteDrib(m);
        addDrib(m);

        logger.info("Updated Drib");

        return true;
    }

    //Remove a Drib
    public boolean deleteDrib(Drib m) {

        logger.info("Deleting Drib...");

        try {
            Statement stmt = con.createStatement();
            stmt.execute("DELETE FROM \"" + m.getSubject().getSubjectID()
                    + "\" WHERE DRIBID=" + m.getMessageID());
            logger.info("Deleted drib from subject table");

            //Subtract the number of posts from a subject
            ResultSet updatepost = stmt.executeQuery("SELECT POSTS "
                    + "FROM DRIBBLE_SYSTEM_SUBJECTS "
                    + "WHERE ID=" + m.getSubject().getSubjectID());
            updatepost.next();
            int num_posts = updatepost.getInt("POSTS");

            stmt.execute("UPDATE DRIBBLE_SYSTEM_SUBJECTS SET POSTS = " + (num_posts - 1) + "WHERE ID = " + m.getSubject().getSubjectID());
            logger.info("Number of posts decremented by 1");

            if (m.getSubject().getNumPosts() == 1) {
                stmt.execute("DROP TABLE \"" + m.getSubject().getSubjectID() + "\"");
                logger.info("Dropped table");
                stmt.execute("DELETE FROM DRIBBLE_SYSTEM_SUBJECTS "
                        + "WHERE ID =" + m.getSubject().getSubjectID());


            }

            logger.info("Drib deleted");

            return true;

        } catch (SQLException e) {

            logger.severe("Error deleting Drib: " + e.getMessage());

            return false;
        }
    }
    //Return a specific Drib

    public Drib getDrib(int subjectID, int DribID) {

        logger.info("Getting Drib");

        //Create a new Drib object to hold the information of the requested Drib
        Drib message = new Drib();
        message.setSubject(getDribSubject(subjectID));

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"" + subjectID
                    + "\" WHERE DRIBID=" + DribID);
            rs.next();

            logger.info("Drib selected from database and updating...");

            //Populating a drib with information
            String DribText = rs.getString("DRIB");
            logger.info("The string is " + DribText);
            message.setText(DribText);
            int lat = rs.getInt("LAT");
            message.setLatitude(lat);
            int longitude = rs.getInt("LONG");
            message.setLongitude(longitude);
            long time = rs.getLong("CURRENTIME");
            message.setTime(time);
            int dribid = rs.getInt("DRIBID");
            message.setMessageID(dribid);
            int driblike = rs.getInt("DRIBLIKE");
            message.setLikeCount(driblike);
            int popularity = rs.getInt("DRIBPOPULARITY");
            message.setPopularity(popularity);
            logger.info("Drib populated... Returning Drib");

        } catch (SQLException e) {

            logger.severe("Error getting Drib: " + e.getMessage());

            return null;
        }

        return message;
    }

    //Return a requested DribSubject
    public DribSubject getDribSubject(int SubjectID) {
        logger.info("Getting the requested DribSubject");

        DribSubject subject = new DribSubject();

        try {
            Statement stmt = con.createStatement();
            //Find the requested subject in the database
            ResultSet rs = stmt.executeQuery("SELECT * FROM DRIBBLE_SYSTEM_SUBJECTS "
                    + "WHERE ID = " + SubjectID);
            logger.info("Added name field to subject object");
            rs.next();
            String name = rs.getString("NAME");
            subject.setName(name);

            //Populate the DribSubject Object
            subject.setSubjectID(SubjectID);
            int lat = rs.getInt("LATITUDE");
            subject.setLatitude(lat);
            int longitude = rs.getInt("LONGITUDE");
            subject.setLongitude(longitude);
            int views = rs.getInt("VIEWS");
            subject.setNumViews(views);
            int posts = rs.getInt("POSTS");
            subject.setNumPosts(posts);
            int popularity = rs.getInt("POPULARITY");
            subject.setPopularity(popularity);
            long time = rs.getLong("CURRENTIME");
            subject.setTime(time);
            logger.info("DribSubject populated... Returning DribSubject");
        } catch (SQLException e) {

            logger.severe("Error getting subject: " + e.getMessage());

            return null;
        }

        return subject;
    }

    public boolean deleteOldDribSubjects(long qualifyingTime) {

        logger.info("Deleting DribSubject...");

        try {
            Statement stmt = con.createStatement();

             ResultSet rs = stmt.executeQuery("SELECT ID, CURRENTIME FROM DRIBBLE_SYSTEM_SUBJECTS");

            //Add the drib subjects to a drib subject list
              int id;
              long time;
            while (rs.next()) {
             id = rs.getInt("ID");
             time = rs.getLong("CURRENTIME");

             if(time<qualifyingTime)
             {
             stmt.execute("DROP TABLE \""+id+"\"");
            stmt.execute("DELETE FROM DRIBBLE_SYSTEM_SUBJECTS WHERE ID = " + id);
            logger.info("Deleted dribSubjects from subjects table "+id);
                }

            }

            
            return true;

        } catch (SQLException e) {

            logger.severe("Error deleting DribSubject: " + e.getMessage());

            return false;
        }
    }
    
    public boolean deleteOldDribs(long qualifyingTime) {

        logger.info("Deleting old Dribs...");

        try {
            Statement stmt = con.createStatement();

              ResultSet rs = stmt.executeQuery("SELECT ID FROM DRIBBLE_SYSTEM_SUBJECTS");

            //Add the drib subjects to a drib subject list
              int id;
            while (rs.next()) {
                id = rs.getInt("ID");

                stmt.execute("DELETE FROM \""+id+"\" WHERE CURRENTIME < " + qualifyingTime);
                logger.info("Drib Deleted..."+id);
            }
            
            logger.info("Deleted old dribs");
            return true;

        } catch (SQLException e) {

            logger.severe("Error deleting old Dribs: " + e.getMessage());

            return false;
        }
    }



}
