package com.thecodecentre.quizmaster;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.datanucleus.util.Base64;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

public class OauthCallbackServlet extends HttpServlet 
{
  	private static final Logger Log = Logger.getLogger(OauthCallbackServlet.class.getName());

  	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
//	  Log.info ("doGet for /oauth2callback service");
	  HttpSession session = req.getSession();

      String user_email = null;
      String accessToken = null;
      String refreshToken = null;
      String idToken = null;
      int expiresIn = 0;
      
      // Read the auth code from URL.
      String auth_code = req.getParameter("code");
//      Log.info("Code is "+auth_code);
      String tokenResponse = GetTokenFromCode(auth_code);
	  Log.info("Response is: "+tokenResponse);
      try 
      {
    	  JSONObject obj = new JSONObject(tokenResponse);
    	  if(obj.has("error"))			// user auth failed or not authorised
    	  {
        	  String error = obj.getString("error");
        	  String desc = obj.getString("error_description");
    	      session.setAttribute("ERROR", error+" - "+ desc);
    	  }
    	  else	// no error so assume a valid access token
    	  {
	    	  accessToken = obj.getString("access_token");
	    	  expiresIn = obj.getInt("expires_in");
	    	  if(obj.has("refresh_token"))
	    		  refreshToken = obj.getString("refresh_token");
	    	  if(obj.has("id_token"))
	    	  {
	    		idToken = obj.getString("id_token");
	    		user_email = getEmailfromJWT(idToken);
	    	  }
    	  }
	      
	  } 
      catch (JSONException | NullPointerException e) // not a json object so assume name value pairs
	  {
		  String[] nvpairs = tokenResponse.split("\\&");	//split into name value pairs
		  for(int i=0; i < nvpairs.length; i++)
		  {
			  String[] fields = nvpairs[i].split("=");
			  if(fields[0].contains("access_token"))		// contains access token param
				  accessToken = fields[1];	//save it
			  else if(fields[0].contains("refresh_token"))	
				  refreshToken = fields[1];	//save it
			  else if(fields[0].contains("expires"))			
				  expiresIn = Integer.parseInt(fields[1]);	//save it
			  else if(fields[0].contains("id_token"))			
				  idToken = fields[1];	//save it
		  }
		  
	  }
      
      if(idToken == null)		// no JWT
    	  user_email = getUserEmail(accessToken);	// get user details normal way
      
//      MPGMethods.saveHostDetails(session, user_email, req.getRemoteAddr());
      QMApp app = MPGMethods.getAppDetails(session, user_email, req.getRemoteAddr());
      session.setAttribute("APP", app);
	  session.setAttribute("TCC_QMASTERAPP_EMAIL", user_email);
      resp.sendRedirect(TccOpenIDServlet.REDIRECTURL);
	}
  	
  	private String getEmailfromJWT(String jwtToken)
  	{
		String[] base64EncodedSegments = jwtToken.split("\\.",3);	// JWT should have 3 segments
//		String base64EncodedHeader = base64EncodedSegments[0];	// not checking
		String base64EncodedClaims = base64EncodedSegments[1];
//		String signature = base64EncodedSegments[2];	// not checking
		Log.info("Encoded claims: "+base64EncodedClaims);		
		byte[] decodedByteArray = Base64.decodeBase64(base64EncodedClaims);
		String claimsJson = new String(decodedByteArray);
		Log.info("JWT claims: "+claimsJson);
		
		try {
			JSONObject obj = new JSONObject(claimsJson);
			if(obj.has("email"))
			{
				String email = obj.getString("email");
				return email;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private String getUserEmail(String token) 
  	{
		try 
		{
			StringBuilder sb = new StringBuilder();			 
			String line;
			String openidendpoint = TccOpenIDServlet.userEndpoint + token;
			Log.info("Getting user info from Openid provider");
			URL url = new URL(openidendpoint);
			HttpURLConnection cnx = (HttpURLConnection) url.openConnection();		
			cnx.setRequestMethod("GET");
			int responseCode = cnx.getResponseCode();
			Log.info("HTTP response code is "+responseCode);
			BufferedReader br = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
			while ((line = br.readLine()) != null) 
			{
				sb.append(line);
			}
			br.close();
			Log.info("Userinfo: "+ sb.toString());
			
			//extract the useful info
			JSONObject json = new JSONObject(sb.toString());

			if (json.has("error"))		// if an error response
			{
				Log.info("OpenID Error: "+sb.toString());
			}
			else
			{
				String email = json.getString("email");
				String userName = json.getString("name");
				return(email);
			}
		}
  		catch (IOException e) 
  		{
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}		
		return ("noname");
  	}

	private String GetTokenFromCode(String code)
  	{
        String clientId = TccOpenIDServlet.clientId;
        String clientSecret = TccOpenIDServlet.clientSecret;
			String input;
			String uri;
			URL url;
			try {
				uri = TccOpenIDServlet.tokenEndpoint;
				String postdata = "code="+ code +
						"&grant_type=authorization_code"+
						"&client_id="+ clientId+
						"&client_secret="+ clientSecret+
						"&redirect_uri="+ URLEncoder.encode(TccOpenIDServlet.OAUTHCALLBACKURI,"UTF-8");
				Log.info("Send authcode request to: "+uri);
				url = new URL(uri);
				HttpURLConnection cnx = (HttpURLConnection) url.openConnection();		
				cnx.setRequestMethod("POST");
				cnx.setRequestProperty("User-Agent", "Mozilla-X");
				cnx.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				// send the post
				cnx.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(cnx.getOutputStream());
				wr.writeBytes(postdata);
				wr.flush();
				wr.close();

				int responseCode = cnx.getResponseCode();
//				Log.info("HTTP response code is "+responseCode);
				BufferedReader br = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
				StringBuffer tokenResponse = new StringBuffer();
		 
				while ((input = br.readLine()) != null)
				{
					tokenResponse.append(input);
				}
				br.close();
		  		return(tokenResponse.toString());	 
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

  		return null;
  	}
}
