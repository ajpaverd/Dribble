package com.dribble.dribbleapp.utilities;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

// HTTP utilities class
public class HttpUtils
{
	// Gets thread safe http client
	public static DefaultHttpClient getThreadSafeClient() 
	{
		HttpParams param = new BasicHttpParams();
		// Force HTTP 1.1 protocol (faster)
		param.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	    DefaultHttpClient client = new DefaultHttpClient(param);
	    ClientConnectionManager mgr = client.getConnectionManager();
	    HttpParams params = client.getParams();

	    client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

	    return client;
	}


}
