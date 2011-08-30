// Authors: Dribble
// Date: 24 April 2010
// Updated 01/07/2011
// Class: DribCom

package com.dribble.dribbleapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.dribble.common.Drib;
import com.dribble.common.DribList;
import com.dribble.common.DribSubject;
import com.dribble.common.DribSubjectList;
import com.dribble.dribbleapp.utilities.HttpUtils;

// Communications class
public class DribCom{

	private static String urlToSendRequest;
	// 10.0.2.2 resolves to localhost in emulator
	// 50.18.104.62 for EC2 server
	private static final String targetDomain = "wheres.dyndns.org:8080";
	private static final String TAG = "DribCom";
	private static final Serializer serializer = new Persister();
	
		private Context context;
	
	public DribCom(Context context){
		this.context = context;
	}
	
	// Converts/casts XML streams to defined classes
	private static Object XMLStreamToClass (HttpGet httpGet, Class<?> clss)
	{
		try 
		{
			Log.i(TAG, "Execute HTTP Request");
			HttpClient httpClient = HttpUtils.getThreadSafeClient();
			// Get http response
			HttpResponse response = httpClient.execute(httpGet);	
			
			// initialise object
			Object obj = null;
			try 
			{
				InputStream res = response.getEntity().getContent();
				
//				// Debug - print result to screen
//				// Comment out otherwise
////				//--------------
//				BufferedReader r = new BufferedReader(new InputStreamReader(res));
//				StringBuilder total = new StringBuilder();
//				String line;
//				while ((line = r.readLine()) != null) {
//				    total.append(line);
//				}
//				Log.d(clss.getSimpleName(), total.toString());
//				// ------------------
				
				obj = serializer.read(clss, res);
				Log.d(clss.getSimpleName(), clss.toString());
				return obj;
			} 
			catch (Exception e)
			{
				Log.e(TAG, "Exception: " + e.getMessage());
				return null;
			}			
		} 
		catch (ClientProtocolException e1) 
		{  
			Log.e(TAG, "Client Protocol Exception: " + e1.getMessage());
			return null;
		} 
		catch (IOException e2) 
		{  
			Log.e(TAG, "IO Exception: " + e2.getMessage());			
			return null;
		} 
	}
	
	//GET - list of topics
	public ArrayList<DribSubject> getTopics(Location location) 
	{   
		
		Log.i(TAG, "Attempt: Retrieve List of Topics");
		
		// request url
		urlToSendRequest =  "http://"+targetDomain+"/Dribble_Communications-war/resources/GetDribSubjects";
		
		try{
		HttpGet httpGet = new HttpGet(urlToSendRequest + "?latitude=" + (int)(location.getLatitude()*1E6) + "&longitude=" +
				(int)(location.getLongitude()*1E6) + "&results=" + DribbleSharedPrefs.getNumDribTopics(context));
		DribSubjectList subjectList =  (DribSubjectList)XMLStreamToClass(httpGet, DribSubjectList.class);
		
		
		if (subjectList != null)
			return subjectList.list;
		else
			return null;
		}catch(NullPointerException npe){
			Log.e(TAG,"Error: "+npe.getMessage());
			return null;
		}
	}

	//GET - messages for a topic 
	public ArrayList<Drib> getMessages(int SubjectID, Location location) 
	{
		Log.i(TAG, "Application Server Communication");
		Log.i(TAG, "Attempt: Retrieve all messages for selected topic");
       
		urlToSendRequest = "http://"+targetDomain+"/Dribble_Communications-war/resources/GetDribs";
			
		HttpGet httpGet = new HttpGet(urlToSendRequest+ "?latitude=" + (int)(location.getLatitude()*1E6) 
				+ "&longitude=" + (int)(location.getLongitude()*1E6) +
				"&results=" + DribbleSharedPrefs.getNumDribTopics(context) +"&subjectID=" + SubjectID);
		DribList dribList =  (DribList)XMLStreamToClass(httpGet, DribList.class);
		return dribList.list;
	} 
	
	//POST - send message
	public static void sendDrib(Object objectToSend) 
	{
		//Serialise the object
		StringWriter sw = new StringWriter();
		Serializer serializer = new Persister();
		try 
		{				
			serializer.write(objectToSend, sw);
			Log.d(TAG, "Sent: " + sw);
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "Client Protocol Exception: " + e.getMessage());
		}
		String xmlToSend = sw.toString();
		HttpClient httpClient = HttpUtils.getThreadSafeClient();
				
		urlToSendRequest =  "http://"+targetDomain+"/Dribble_Communications-war/resources/PutDrib";

		HttpPut httpPut = new HttpPut(urlToSendRequest);
		try 
		{  
			StringEntity entity = new StringEntity(xmlToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPut.setEntity(entity);
			Log.i(TAG, "Execute HTTP Request");
			// Execute HTTP Post Request  
			httpClient.execute(httpPut);
		}
		catch (ClientProtocolException e1) 
		{  
			Log.e(TAG, "Client Protocol Exception: " + e1.getMessage());
		} 
		catch (IOException e2) 
		{  
			Log.e(TAG, "IO Exception: " + e2.getMessage()); 
		}
	}
	
}
	
