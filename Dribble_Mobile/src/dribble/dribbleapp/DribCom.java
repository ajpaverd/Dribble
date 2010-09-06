// Authors: Dribble
// Date: 24 April 2010
// Class: DribCom

package dribble.dribbleapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.thoughtworks.xstream.XStream;

import dribble.common.Drib;
import dribble.common.DribList;
import dribble.common.DribSubject;
import dribble.common.DribSubjectList;

public class DribCom {

	String urlToSendRequest;
	String targetDomain = "lab1.cetas.ac.za:8080";
	int results = 5;
	
	private static final String TAG = "DribCom";
	
	public ArrayList<DribSubject> getTopics() //GET - list of topics
	{
		Log.i(TAG, "Application Server Communication");
		Log.i(TAG, "Attempt: Retrieve List of Topics");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		//HttpHost targetHost = new HttpHost(targetDomain, 80, "http");
		urlToSendRequest =  "http://"+targetDomain+"/Dribble_Communications-war/resources/GetDribSubjects";
		XStream xs = new XStream();
		HttpGet httpGet = new HttpGet(urlToSendRequest + "?latitude=" + MapsThread.LATITUDE + "&longitude=" + MapsThread.LONGITUDE + "&results=" + results);
		try 
		{
			Log.i(TAG, "Received List of Topics");

			Log.i(TAG, "Execute HTTP Request");
			HttpResponse response = httpClient.execute(httpGet);
			InputStream res = response.getEntity().getContent();
			//	String resData = generateString(res);
			String xml = convertStreamToString(res);
			Log.i(TAG, "Conversion Stream to String Successful");
		//	String xmlTest = "<dribble.common.DribTopic><name>Fire</name><longitude>2.400002E7</longitude><latitude>2.400002E7</latitude><numViews>10</numViews><popularity>6</popularity><numPosts>30</numPosts><topicID>1</topicID></dribble.common.DribTopic>";
			DribSubjectList topicList  = (DribSubjectList)xs.fromXML(xml);
			Log.i(TAG, "Conversion String to XML to DribSubjectList Wrapper Class Successful");
		//	DribTopic drib = (DribTopic)xs.fromXML(xmlTest);
			return topicList.list;
		} 
		catch (ClientProtocolException e1) 
		{  
			Log.e(TAG, "Client Protocol Exception: " + e1);// TODO Auto-generated catch block  
			return null;
		} catch (IOException e2) {  
			Log.e(TAG, "IO Exception: " + e2);
			// TODO Auto-generated catch block  
			return null;
		}
	}

	public ArrayList<Drib> getMessages(int SubjectID) //GET - messages for a topic 
	{
		Log.i(TAG, "Application Server Communication");
		Log.i(TAG, "Attempt: Retrieve all messages for selected topic");

		DefaultHttpClient httpClient = new DefaultHttpClient();
		XStream xs = new XStream();
		urlToSendRequest = "http://"+targetDomain+"/Dribble_Communications-war/resources/GetDribs";
			
		HttpGet httpGet = new HttpGet(urlToSendRequest+ "?latitude=" + MapsThread.LATITUDE + "&longitude=" + MapsThread.LONGITUDE + "&results=" + results +"&subjectID=" + SubjectID);
		try 
		{  
			Log.i(TAG, "Execute HTTP Request");
			// Execute HTTP Get Request  
			HttpResponse response = httpClient.execute(httpGet);
			// Read response using InputStream
			InputStream res = response.getEntity().getContent();
			//Convert Stream to String, Cast to ArrayList of Dribs (messages)
			String xml = convertStreamToString(res);
			Log.i(TAG, "Conversion Stream to String Successful");
			DribList messageList  = (DribList)xs.fromXML(xml);
			Log.i(TAG, "Conversion String to XML to ArrayList Successful");
			ArrayList<Drib> ar = messageList.list;
			
			return ar;
		}
		catch (ClientProtocolException e1) 
		{  
			Log.e(TAG, "Client Protocol Exception: " + e1);// TODO Auto-generated catch block
			return null;
		} 
		catch (IOException e2) 
		{  
			Log.e(TAG, "IO Exception: " + e2.getMessage());
			Log.i(TAG, "Conversion String to XML to ArrayList Successful");
			return null;
		}
	} 
	
	public void sendDrib(Object objectToSend) //POST - send message
	{
		Log.i(TAG, "Application Server Communication");
		//Serialise the object
		XStream xs = new XStream();
		String xmlToSend = xs.toXML(objectToSend);
		DefaultHttpClient httpClient = new DefaultHttpClient();
				
		urlToSendRequest =  "http://"+targetDomain+"/Dribble_Communications-war/resources/PutDrib";

		HttpPut httpPut = new HttpPut(urlToSendRequest);
		try 
		{  
			StringEntity entity = new StringEntity(xmlToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPut.setEntity(entity);
			Log.i(TAG, "Execute HTTP Request");
			// Execute HTTP Post Request  
			HttpResponse response = httpClient.execute(httpPut);
		}
		catch (ClientProtocolException e1) 
		{  
			Log.e(TAG, "Client Protocol Exception: " + e1);// TODO Auto-generated catch block  
		} catch (IOException e2) 
		{  
			Log.e(TAG, "IO Exception: " + e2);// TODO Auto-generated catch block  
		}
	}

	
	// Method to convert Input Stream to String
	public String convertStreamToString(InputStream is) throws IOException 
	{
		Log.i(TAG, "Converting Stream to String");
		
        if (is != null) 
        {
            StringBuilder sb = new StringBuilder();
            String line;

            try 
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) 
                {
                    sb.append(line).append("\n");
                }
            } 
            finally 
            {
                is.close();
            }
            return sb.toString();
        } 
        else 
        {        
            return "";
        }
	}
}
