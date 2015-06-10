package com.thecodecentre.quizmaster;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("serial")
public class QMServlet extends HttpServlet 
{
  	private static final Logger Log = Logger.getLogger(QMServlet.class.getName());
    public Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	public ErrorJsonMsg errors = new ErrorJsonMsg();

	// new quizmaster
	public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{	
        String jsonrsp = null;
		QMApp qma;
        String appId = req.getParameter("app_id");
		
		try
		{
			qma = MPGMethods.checkValidAppId(appId);			
			jsonrsp = RegisterNewQM(qma, req);
		}
		catch (TCCException | NumberFormatException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QuizMaster", te.getMessage());
			jsonrsp = gson.toJson(error);
		}
       
        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
    }
	
	// authenticate this quizmaster user
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
        String jsonrsp = null;
		QMApp qma;
		String path = req.getPathInfo();
        String appId = req.getParameter("app_id");
		
		if(path != null && path.length() > 1)	// nothing or just a /
		{
			String[] p = path.split("/");	// first field will always be blank after the split

			try
			{
				qma = MPGMethods.checkValidAppId(appId);			
				if(p[1].compareTo("authenticate") == 0)
				{
					QMaster qm = checkBasicAuthentication(qma, req);
					TCCToken at = new TCCToken(qma.getAppId(), qm.getQMId(), qma.getAppSecret());
					AuthTokenJsonMsg jmsg = new AuthTokenJsonMsg(at);
					jsonrsp = gson.toJson(jmsg);
				}
			}
			catch (TCCException te)
			{
				ErrorJsonMsg jmsg = new ErrorJsonMsg("QuizMaster", te.getMessage());
				jsonrsp = gson.toJson(jmsg);
			}
		}
		else
		{
			ErrorJsonMsg jmsg = new ErrorJsonMsg("QuizMaster", "Invalid API call");
			jsonrsp = gson.toJson(jmsg);
		}

        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);

    }

	// update quiz master
	public void doPut(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
	    String jsonrsp = null;
        String appId = req.getParameter("app_id");
		QMApp qma;
		
		try
		{
			qma = MPGMethods.checkValidAppId(appId);			
//			jsonrsp = GameRestPutandDel.RestPutsFromPath(path, req);
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
	
	// delete a quiz master
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
	    String jsonrsp = null;
        String appId = req.getParameter("app_id");
		QMApp qma = null;
		
		try
		{
			qma = MPGMethods.checkValidAppId(appId);			
//			jsonrsp = GameRestPutandDel.RestDelFromPath(path, req);
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
		resp.getWriter().println("TCC");
	}

	private String RegisterNewQM(QMApp qmapp, HttpServletRequest req) throws TCCException 
	{
		String QMName, QMEmail,QMPassword;
		String ipaddr = req.getRemoteAddr();
		if((QMName = req.getParameter("qmname")) == null) QMName = "";
		if((QMEmail = req.getParameter("qmemail")) == null) QMEmail = "";
		if((QMPassword = req.getParameter("qmpassword")) == null) QMPassword = "";
		
		checkValidName(QMName);
		checkValidEmail(QMEmail);
		checkValidPassword(QMPassword);
		
		MPGMethods.checkUniqueName(QMName);
		
		QMaster qm = new QMaster(QMName, QMEmail, qmapp.getAppId(), ipaddr);
		qm.setPassword(QMPassword);
		MPGMethods.Persist(qm);
		qmapp.incRegUsers();
		Log.info("New QM reg for app "+qmapp.getAppName());
		QMasterJsonMsg jmsg = new QMasterJsonMsg(qm.getQMId(), qm.getAppId(), QMName, QMEmail);
		return(gson.toJson(jmsg));
	}

	private void checkValidName(String name) throws TCCException 
	{
		if(name.length() < 6 || name.length() > 20) 
			throw new TCCException("Name should be between 10 and 20 characters long");
		
		if(name.contains(" "))
			throw new TCCException("Name should contain no spaces");		
	}
	
	private void checkValidEmail(String email) throws TCCException 
	{
    	if(email.matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"))
			return;
    	
    	throw new TCCException("Email is invalid");
	}
	
	private void checkValidPassword(String pwd) throws TCCException 
	{
		if(pwd.length() < 8 || pwd.length() > 20) 
			throw new TCCException("Password should be between 10 and 20 characters long");
		
		if(pwd.contains(" "))
			throw new TCCException("Password should contain no spaces");
	}
	
	/*
	 * check that HTTP Basic authentication details for Quizmaster
	 */
	public static QMaster checkBasicAuthentication(QMApp qma, HttpServletRequest req) throws TCCException 
	{
        final String authorisation = req.getHeader("Authorization");

        if (authorisation != null && authorisation.startsWith("Basic"))
        {
            // Authorization: Basic base64credentials
            String base64Credentials = authorisation.substring("Basic".length()).trim();
            byte[] credentials = DatatypeConverter.parseBase64Binary(base64Credentials);
            // credentials = username:password
            String acredentials = new String(credentials);
            final String[] values = acredentials.split(":",2);
            if(values[0] == null || values[1] == null)	// no username or password
            	throw new TCCException("Credentials not provided");
            QMaster qm = MPGMethods.checkQMCredentials(values[0],values[1]);
//        	Log.info("qm id: "+qm.getAppId()+" qma id: "+qma.getAppId());
            if(qm.getAppId().compareTo(qma.getAppId()) != 0)
            	throw new TCCException("Invalid credentials for this App Id");
            	
            return qm;
        }
            
    	throw new TCCException("Credentials not provided");
	}

}