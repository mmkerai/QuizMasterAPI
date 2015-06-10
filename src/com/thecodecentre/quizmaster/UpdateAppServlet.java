package com.thecodecentre.quizmaster;
/*
 * Register new user
 * Could either be patient (HMUser) or professional (HMProUser)
 * 
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class UpdateAppServlet extends HttpServlet 
{
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		String query = req.getQueryString();
		HttpSession session = req.getSession();
		String appname = req.getParameter("appname");
		String appurl = req.getParameter("appurl");
		QMApp app = (QMApp) session.getAttribute("APP");
		
		String emsg = "";
		
		if(!isValidAppName(appname))
		{
			emsg = "App name should be between 10 and 24 chars long and contain no spaces";
		}
		else
		{
			app.setAppName(appname);
			app.setAppUrl(appurl);
			MPGMethods.Persist(app);
			emsg = "Details updated. Please make a note of your app details below";			
		}

		session.setAttribute("ERROR", emsg);			
		session.setAttribute("APP", app);
		resp.sendRedirect(TccOpenIDServlet.REDIRECTURL);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		HttpSession session = req.getSession();
		
		resp.setContentType("text/html");
		resp.sendRedirect("/registerapp.jsp");

	}
	
    public Boolean isValidEmail(String email)
    {
    	return email.matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
    }

    public Boolean isValidAppName(String name)
    {
    	if(name.length() < 10 || name.length() > 24) 
    		return false;
    	if(name.indexOf(" ") == -1)	// contain no spaces
    		return true;
    	
    	return false;
    }

}
