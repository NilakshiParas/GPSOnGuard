package com.gpsonguard.http;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

//import javax.microedition.io.Connector;
//import javax.microedition.io.HttpConnection;

//import com.gpsonguard.util.Utilities;

public class HttpPost {

	private HttpsURLConnection httpConn;
	private RestApiListener listener;
	private JSONObject jsonObject;
	private int methodType;

	public JSONObject HttpPost(String url, JSONObject jsonObject, int methodType) {

		JSONObject json = null;
		
		try {
			this.jsonObject = jsonObject;
			this.methodType = methodType;

			System.setProperty("http.keepAlive", "false");

//			// Load CAs from an InputStream
//// (could be from a resource or ByteArrayInputStream or ...)
//			CertificateFactory cf = CertificateFactory.getInstance("X.509");
//// From https://www.washington.edu/itconnect/security/ca/load-der.crt
//			InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
//			Certificate ca;
//			try {
//				ca = cf.generateCertificate(caInput);
//				System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
//			} finally {
//				caInput.close();
//			}
//
//// Create a KeyStore containing our trusted CAs
//			String keyStoreType = KeyStore.getDefaultType();
//			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//			keyStore.load(null, null);
//			keyStore.setCertificateEntry("ca", ca);
//
//// Create a TrustManager that trusts the CAs in our KeyStore
//			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//			tmf.init(keyStore);
//
//// Create an SSLContext that uses our TrustManager
//			SSLContext context = SSLContext.getInstance("TLS");
//			context.init(null, tmf.getTrustManagers(), null);


			System.setProperty("http.keepAlive", "false");
			URL httpurl = new URL(url);
			httpConn = (HttpsURLConnection) httpurl.openConnection();
			//this.listener = listener;


			CertificadoAceptar ca = new CertificadoAceptar();
			ca.allowAllSSL();


			if (methodType == 1) {
				httpConn.setRequestMethod("POST");
				// Utilities.showMessage(" Post Method");
			} else {
				httpConn.setRequestMethod("GET");
			}
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.getHostnameVerifier();
			httpConn.getSSLSocketFactory();
			//httpConn.setConnectTimeout(30000);
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			
			//System.out.println("Connecting:" + url);
			//open
			httpConn.connect();
			  
			//setup send
			OutputStream os = new BufferedOutputStream(httpConn.getOutputStream());
			os.write(jsonObject.toString().getBytes());
			//clean up
			os.flush();
			
			int responseCode = httpConn.getResponseCode();
			InputStream is;

			//System.out.println("====Response Code from Server" + responseCode);
			
			int statusCode = httpConn.getResponseCode();
	        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
	            // handle unauthorized (if service requires user login)
	        } else if (statusCode != HttpURLConnection.HTTP_OK) {
	            // handle any other errors, like 404, 500,..
	        }
	        
	        if (statusCode == HttpURLConnection.HTTP_OK) {
	        	// create JSON object from content
	        	InputStream in = new BufferedInputStream(httpConn.getInputStream());
	        	json = new JSONObject(getResponseText(in));
	        	//System.out.print("Response:" + json.toString());
	        }
	       
	        //String guardStatus = json.getString("guard");
	        //JSONObject guardObj = json.getJSONObject("guard");
			//JSONObject userObject = guardObj.getJSONObject("User");

			//String guardName = userObject.getString("name");
	        //System.out.print("Guard Name:" +  guardName);
	        

		}		catch (Exception e) {
			Log.e("Connection Error","Connection Error+++++++"+e);
			// Utilities.showMessage("== Cause " + e.getMessage());
			//listener.noIternetConnectivity();
			//System.out.print("Error from server");
			//e.printStackTrace();
		} finally {
	        if (httpConn != null) {
	            httpConn.disconnect();
	        }
	    }
	    
		return json;
	}
	
	private static String getResponseText(InputStream inStream) {
	    // very nice trick from 
	    // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
	    return new Scanner(inStream).useDelimiter("\\A").next();
	}
	
}
