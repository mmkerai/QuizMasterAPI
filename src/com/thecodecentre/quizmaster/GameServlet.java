package com.thecodecentre.quizmaster;

/*
 * File to process all API calls to /qmgames
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mortbay.log.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("serial")
public class GameServlet extends HttpServlet 
{
  	private static final Logger Log = Logger.getLogger(GameServlet.class.getName());
    public Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	public ErrorJsonMsg errors = new ErrorJsonMsg();

	// new game
	public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{	
        String jsonrsp = null;
		QMaster qm;

		try
		{
//			qma = MPGMethods.checkValidAppId(appId);			
			qm = checkQMAuthToken(req);
			jsonrsp = GameRestPosts.RestPostsFromPath(qm, req);
		}
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QM Game", te.getMessage());
			jsonrsp = gson.toJson(error);
		}

        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
    }
	
	// get games for this host
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
        String jsonrsp = null;
		QMaster qm;
		
		try
		{
			qm = checkQMAuthToken(req);
			jsonrsp = GameRestGets.RestGetsFromPath(qm, req);
		}
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QM Games", te.getMessage());
			jsonrsp = gson.toJson(error);
		}

        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
    }

	// update and actions 
	public void doPut(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
	    String jsonrsp = null;
		QMaster qm;
		
		try
		{
			qm = checkQMAuthToken(req);
			jsonrsp = GameRestPutandDel.RestPutsFromPath(qm, req);
		}
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QM Games", te.getMessage());
			jsonrsp = gson.toJson(error);
		}
	
        MPGMethods.addResponseHeaders(resp);
		resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
	
	}
	
	// delete a game and its contestants
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
	    String jsonrsp = null;
        String appId = req.getParameter("app_id");
		QMApp qma;
		QMaster qm;
		
		try
		{
//			qma = MPGMethods.checkValidAppId(appId);			
			qm = checkQMAuthToken(req);
			jsonrsp = GameRestPutandDel.RestDelFromPath(qm, req);
		}
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QM Games", te.getMessage());
			jsonrsp = gson.toJson(error);
		}
	
        MPGMethods.addResponseHeaders(resp);
		resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
	
	}
	
	// Options for CORS 
	public void doOptions(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{				
        MPGMethods.addResponseHeaders(resp);
		resp.setStatus(HttpServletResponse.SC_OK);		// request ok
	}

	/*
	 * This function checks the access token and returns a QMaster id
	 */
		public static QMaster checkQMAuthToken(HttpServletRequest req) throws TCCException
		{
	    	long user = MPGMethods.checkAuthToken(req);
	    	return(MPGMethods.GetQMasterFromId(user));
		}
		
}