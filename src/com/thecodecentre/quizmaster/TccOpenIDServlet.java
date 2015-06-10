package com.thecodecentre.quizmaster;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class TccOpenIDServlet extends HttpServlet 
{
  	private static final Logger Log = Logger.getLogger(TccOpenIDServlet.class.getName());
	public static final String OAUTHCALLBACKURI = "https://tcc-quizmaster.appspot.com/oauth2callback";
	public static final String REDIRECTURL = "https://tcc-quizmaster.appspot.com/quizmaster.jsp";
//	public static final String OAUTHCALLBACKURI = "http://localhost:8888/oauth2callback";
//	public static final String REDIRECTURL = "http://localhost:8888/quizmaster.jsp";

	public static final String OAUTH2_ENDPOINT_GO = "https://accounts.google.com/o/oauth2/auth";
	public static final String TOKEN_ENDPOINT_GO = "https://accounts.google.com/o/oauth2/token";
	public static final String USER_ENDPOINT_GO = "https://www.googleapis.com/plus/v1/people/me/openIdConnect?access_token=";
	public static final String CLIENT_ID_GO = "230671905925-if9uj89510obrrqulksbjno54e8jueab.apps.googleusercontent.com";
	public static final String CLIENT_SECRET_GO = "yQ-ySXWHFzY9t5vjThgZdAc1";	//google

	public static final String OAUTH2_ENDPOINT_FB = "https://www.facebook.com/dialog/oauth";
	public static final String TOKEN_ENDPOINT_FB = "https://graph.facebook.com/oauth/access_token";
	public static final String USER_ENDPOINT_FB = "https://graph.facebook.com/me?access_token=";
	public static final String CLIENT_ID_FB = "1602725453274999";		//facebook
	public static final String CLIENT_SECRET_FB = "2d2344a433cae8377a94527a819c3cad";	//facebook

//	public static final String Client_Id = "dj0yJmk9Um5hN2VRNFdENmhuJmQ9WVdrOVVqbENUbU5xTTJVbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD0xNg--";	//yahoo
//	public static final String Client_Secret = "31a31f9f1f1d97049edb490944b13ae3e049ecb8";	// yahoo
	public static final String scopes = "email";
	public static final String FACEBOOK = "FACEBOOK";
	public static final String GOOGLE = "GOOGLE";
	
	public static String oauth2Endpoint;
	public static String tokenEndpoint;
	public static String userEndpoint;
	public static String clientId;
	public static String clientSecret;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
		HttpSession session = req.getSession();
		String user = (String) session.getAttribute("TCC_QMASTER_EMAIL");
		
	    if(user == null)	// not logged in
	    {
	    	String provider = req.getParameter("provider");	// get name of open id provider to use
	    	if(provider == null)	// invalid provider
	    	{
		    	resp.getWriter().println("Provider missing");
	    		return;
	    	}
	    	
		    session.setAttribute("PROVIDER", provider);
	    	if(provider.equalsIgnoreCase(FACEBOOK))
	    	{
		    	oauth2Endpoint = OAUTH2_ENDPOINT_FB;
		    	tokenEndpoint = TOKEN_ENDPOINT_FB;
		    	userEndpoint = USER_ENDPOINT_FB;
		    	clientId = CLIENT_ID_FB;
		    	clientSecret = CLIENT_SECRET_FB;
	    	}
	    	else if(provider.equalsIgnoreCase(GOOGLE))
	    	{
		    	oauth2Endpoint = OAUTH2_ENDPOINT_GO;
		    	tokenEndpoint = TOKEN_ENDPOINT_GO;
		    	userEndpoint = USER_ENDPOINT_GO;
		    	clientId = CLIENT_ID_GO;
		    	clientSecret = CLIENT_SECRET_GO;
	    	}
	    	else	// invalid provider
	    	{
		    	resp.getWriter().println("Provider invalid: "+provider);
	    		return;
	    	}
	    	// Start Oauth2 process and first getting an auth code from the Openid provider
	    	String requestUrl = oauth2Endpoint + "?client_id=" + clientId;
	    	requestUrl = requestUrl + "&redirect_uri=" + URLEncoder.encode(OAUTHCALLBACKURI, "utf-8") +
	    			"&scope="+scopes+"&response_type=code&approval_prompt=force";
	    	Log.info("Sending oauth2 request to "+requestUrl);
	    	resp.sendRedirect(requestUrl);
	    }
	    else
	    	resp.getWriter().println("User logged in: "+user);
	}
}

