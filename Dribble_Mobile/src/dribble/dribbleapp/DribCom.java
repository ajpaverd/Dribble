// Authors: Dribble
// Date: 24 April 2010
// Class: DribCom

package dribble.dribbleapp;

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

import android.util.Log;
import dribble.common.Drib;
import dribble.common.DribList;
import dribble.common.DribSubject;
import dribble.common.DribSubjectList;

public class DribCom {

	private static String urlToSendRequest;
	private static final String targetDomain = "lab1.cetas.ac.za:8080";
	private static final int results = 5;	
	private static final String TAG = "DribCom";
	
	@SuppressWarnings("unchecked")
	private static Object XMLStreamToClass (HttpGet httpGet, Class clss)
	{
		try 
		{
			Log.i(TAG, "Execute HTTP Request");
			HttpClient httpClient = HttpUtils.getThreadSafeClient();
			HttpResponse response = httpClient.execute(httpGet);		
			Serializer serializer = new Persister();
			Object obj = null;
			try 
			{
				InputStream res = response.getEntity().getContent();
				obj = serializer.read(clss, res);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return obj;
		} 
		catch (ClientProtocolException e1) 
		{  
			Log.e(TAG, "Client Protocol Exception: " + e1);
			return null;
		} 
		catch (IOException e2) 
		{  
			Log.e(TAG, "IO Exception: " + e2);			
			return null;
		}
	}
	
	public static ArrayList<DribSubject> getTopics() //GET - list of topics
	{   
		Log.i(TAG, "Application Server Communication");
		Log.i(TAG, "Attempt: Retrieve List of Topics");
		
		urlToSendRequest =  "http://"+targetDomain+"/Dribble_Communications-war/resources/GetDribSubjects";
		
		HttpGet httpGet = new HttpGet(urlToSendRequest + "?latitude=" + MapsThread.LATITUDE + "&longitude=" +
				MapsThread.LONGITUDE + "&results=" + results);
		DribSubjectList subjectList =  (DribSubjectList)XMLStreamToClass(httpGet, DribSubjectList.class);
		return subjectList.list;
	}

	public static ArrayList<Drib> getMessages(int SubjectID) //GET - messages for a topic 
	{
		Log.i(TAG, "Application Server Communication");
		Log.i(TAG, "Attempt: Retrieve all messages for selected topic");
       
		urlToSendRequest = "http://"+targetDomain+"/Dribble_Communications-war/resources/GetDribs";
			
		HttpGet httpGet = new HttpGet(urlToSendRequest+ "?latitude=" + MapsThread.LATITUDE + "&longitude=" + MapsThread.LONGITUDE +
				"&results=" + results +"&subjectID=" + SubjectID);
		DribList dribList =  (DribList)XMLStreamToClass(httpGet, DribList.class);
		return dribList.list;
	} 
	
	public static void sendDrib(Object objectToSend) //POST - send message
	{
		Log.i(TAG, "Application Server Communication");
		
		//Serialise the object
		StringWriter sw = new StringWriter();
		Serializer serializer = new Persister();
		try {				
			serializer.write(objectToSend, sw);
		} catch (Exception e) {
			e.printStackTrace();
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
			Log.e(TAG, "Client Protocol Exception: " + e1);
		} catch (IOException e2) 
		{  
			Log.e(TAG, "IO Exception: " + e2); 
		}
	}
}
	
