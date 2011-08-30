package com.dribble.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DribbleData {

	public static final String DB_NAME = "Dribble";
	public static final String DB_TABLE_VOTEDDRIBID = "votedDribIdTable";
	public static final int DB_VERSION = 3;

	private static final String CLASSNAME = DribbleData.class.getSimpleName();
	private static final String[] COLS = new String[]{"_dribid", "_timeCreated"};

	public static final String TAG = "DribbleData";


	private SQLiteDatabase db;
	private final DBOpenHelper dbOpenHelper;

	//Represent the information to be placed into the database
	public static class VotedUserData{
		public String dribId;
		public long timeCreated;

		public VotedUserData(){

		}

		public VotedUserData(final String messageId, final long timeCreated){
			this.dribId = messageId;
			this.timeCreated = timeCreated;
		}

		public void SetVotedUserData(String _messageId, long _time){
			this.dribId = _messageId;
			this.timeCreated = _time;
		}

		@Override
		public String toString(){
			return this.dribId +", "+this.timeCreated;
		}

	}

	//Used to create, update and open the database
	private static class DBOpenHelper extends SQLiteOpenHelper{
		private static final String DB_CREATE = "CREATE TABLE "
				+DribbleData.DB_TABLE_VOTEDDRIBID
				+ " (_dribId TEXT, _timeCreated);";
		private SQLiteDatabase db;

		public DBOpenHelper (final Context context, final String dbName, 
				final int version){
			super(context, DribbleData.DB_NAME, null, DribbleData.DB_VERSION);
			Log.i(TAG,"Constructor called");
		}

		@Override
		public void onCreate(final SQLiteDatabase db){
			
			try{
				Log.i(TAG,"Table created: "+DBOpenHelper.DB_CREATE);
				db.execSQL(DBOpenHelper.DB_CREATE);
			}catch (SQLException e){

			}
		}
		
		@Override
		public void onOpen(final SQLiteDatabase db) {
			super.onOpen(db);
		}
		
		@Override
		public void onUpgrade (final SQLiteDatabase db, final int oldVersion,
				final int newVersion){
			db.execSQL("DROP TABLE IF EXISTS"
					+ DribbleData.DB_TABLE_VOTEDDRIBID);
			this.onCreate(db);
		}

	}//End of DBOpenHelper Class
	
	public DribbleData(final Context context){
		this.dbOpenHelper = new DBOpenHelper (context, DB_NAME,1);
		this.establishDB();
	}
	
	//Creates a database if needed
	private void establishDB(){
		if(this.db==null){
			this.db = this.dbOpenHelper.getWritableDatabase();
			Log.i(TAG,"New Database established..."+ db.toString());
			
		}
		
	}
	
	// Use when pausing to close connections and free up resources
		public void cleanup() {
			if (this.db != null) {
				this.db.close();
				this.db = null;
			}
		}

		public void insert(final VotedUserData votedUserData) {
			
			try{
			
			Log.i(TAG, "The dribid value is: "
					+ votedUserData.dribId);
			Log.i(TAG, "The time created is: " + votedUserData.timeCreated);

			ContentValues values = new ContentValues();
			values.put("_dribId", votedUserData.dribId);
			values.put("_timeCreated", votedUserData.timeCreated);
			this.db.insert(DribbleData.DB_TABLE_VOTEDDRIBID, null, values);
			}
			catch(SQLException e){
				Log.e(TAG,"Caught SQLException in insert function: "+e.getMessage());
			}
		}

		public Cursor getDribId(String dribId)
				throws SQLException {

			Cursor mCursor = this.db.query(true,
					DribbleData.DB_TABLE_VOTEDDRIBID,
					new String[] { "_dribId","_timeCreated" }, "_dribId" + "=" + "'"
							+ dribId + "'", null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}
		
		//Update a measurement value 
		public void updatedrib(String dribId) {
			try{
			
			ContentValues uniqueDribId = new ContentValues();
			
			uniqueDribId.put("_dribId", dribId);
			
			//Log.i(TAG,"The new measurement value is: "+newMeasurementValue);
			
			db.update(DribbleData.DB_TABLE_VOTEDDRIBID, uniqueDribId, 
														"_dribId" + "=" + "'"+dribId+"'", null);
			}
			catch(SQLException e){
				Log.e(TAG,"Caught the SQLException in update function: "+e.getMessage());
			}
			
		}

		public ArrayList<VotedUserData> getAll() {
			ArrayList<VotedUserData> ret = new ArrayList<VotedUserData>();
			Cursor c = null;
			try {
				c = this.db.query(DribbleData.DB_TABLE_VOTEDDRIBID,
						DribbleData.COLS, null, null, null, null,
						null);
				int numRows = c.getCount();
				c.moveToFirst();
				for (int i = 0; i < numRows; i++) {
					VotedUserData votedUserData = new VotedUserData();
					votedUserData.dribId = c.getString(0);
					votedUserData.timeCreated = c.getLong(2);
					ret.add(votedUserData);
				}
				c.moveToNext();

			} catch (SQLException e) {

			} finally {

				if (c != null && !c.isClosed()) {
					c.close();
				}
			}
			return ret;

		}

}
