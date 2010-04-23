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

        try {
            connectionUrl = "jdbc:derby://localhost:1527/Dribble;";
            con = DriverManager.getConnection(connectionUrl, "", "");
        } catch (SQLException e) {
            logger.severe("SQLexception: " + e.toString());
        }

        logger.info("Constructed SQLCommunicator");

    }

    public boolean addDrib(Drib m) {

        logger.info("Add Drib");

        try {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DRIBSUBJECT_ID "
                    + "FROM DRIBBLE_SYSTEM_DRIBSUBJECTS "
                    + "WHERE DRIBSUBJECT_ID=" + m.getSubject().getSubjectID());
            if (rs.wasNull()) {
                stmt.execute("CREATE TABLE " + m.getSubject().getSubjectID()
                        + "(DRIB varchar(144),"
                        + "LAT double,"
                        + "LONG double,"
                        + "TIME date,"
                        + "DRIBID varchar(50),"
                        + "DRIBPOPULARITY integer,"
                        + "PRIMARY KEY (DribID)");
//TODO Add subject ID to table of subjects id table
                stmt.execute("");
            }
            stmt.execute("INSERT INTO " + m.getSubject().getSubjectID() + " (DRIB,LAT,LONG,TIME,DRIBID,DRIBPOPULARITY)"
                    + "VALUES (" + m.getText() + "," + m.getLatitude() + "," + m.getTime() + "," + m.getMessageID() + "," + m.getPopularity() + ")");
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
/*
    public ArrayList<DribSubject> getDribSubjects(double lat, double longitude, double radius) {

        logger.info("Get Drib Subjects");

        ArrayList<Drib> DribList = new ArrayList<Drib>();
        double latlimit1 = lat + radius;
        double latlimit2 = lat - radius;
        double longlimit1 = longitude + radius;
        double longlimit2 = longitude - radius;
        try {
            Statement stmt = con.createStatement();

            //TODO Read all subject ids from subjects table

            //TODO Search each table in the above list for any Dribs which fall within
            //lat and long and get their subject and if there is a result,
            //create a new DribSubject from the Subject ID and add to list
            //return list

            ResultSet rs = stmt.executeQuery("SELECT FROM " + s.getSubjectID()
                    + "WHERE LAT <" + latlimit1
                    + "AND LAT >" + latlimit2
                    + "AND LONG <" + longlimit1
                    + "AND LONG >" + longlimit2);
            while (rs.next()) {
                Drib m = getDrib(s, rs.getInt("Drib_ID"));
                DribList.add(m);
            }
        } catch (SQLException e) {
            logger.severe("Error getting Dribs: " + e.getMessage());
        }
        return DribList;
    }
*/
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
            int popularity = rs.getInt("DRIBPUPULARITY");
            m.setPopularity(popularity);

        } catch (SQLException e) {

            logger.severe("Error getting Drib: " + e.getMessage());

            return null;
        }

        logger.info("Got Drib");

        return m;
    }
}
