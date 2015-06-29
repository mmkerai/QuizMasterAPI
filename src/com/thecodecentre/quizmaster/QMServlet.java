package com.thecodecentre.quizmaster;

/*
 * File to process all API calls to /qm/*
 * The POST calls have ?app_id=xxxxxx set (used for stats only)
 */

import java.io.IOException;
import java.util.List;
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
  	private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private ErrorJsonMsg error = null;
    private String jsonrsp = null;

	// new quizmaster or forgot password - no token required
	public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{	
		QMApp qma;
        String appId = req.getParameter("app_id");
		String path = req.getPathInfo();
		
		try
		{
			qma = MPGMethods.checkValidAppId(appId);			
			if(path != null && path.length() > 1)	// nothing or just a /
			{
				String[] p = path.split("/");	// first field will always be blank after the split
				if(p[1].compareTo("forgot") == 0)
				{
					error = new ErrorJsonMsg("QuizMaster", "New password will be emailed to you");
					jsonrsp = gson.toJson(error);
				}
				else
				{
					error = new ErrorJsonMsg("QuizMaster", "Invalid API call");
					jsonrsp = gson.toJson(error);
				}
			}
			else	// register new
			{
				jsonrsp = RegisterNewQM(qma, req);
			}
		}
		catch (TCCException | NumberFormatException te)
		{
			error = new ErrorJsonMsg("QuizMaster", te.getMessage());
			jsonrsp = gson.toJson(error);
		}
       
        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
    }
	
	// view this quizmaster user
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
        String jsonrsp = null;
		QMaster qm = null;
		String path = req.getPathInfo();
		
		try
		{
			if(path != null && path.length() > 1)	// nothing or just a /
			{
				ErrorJsonMsg error = new ErrorJsonMsg("QuizMaster","Invalid API Call");
				jsonrsp = gson.toJson(error);
			}
			else
			{
				qm = GameServlet.checkQMAuthToken(req);
				jsonrsp = gson.toJson(new QMasterJsonMsg(qm));
			}
		}
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QuizMaster",te.getMessage());
			jsonrsp = gson.toJson(error);
		}

        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
    }

	// update quiz master
	public void doPut(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
		QMaster qm;
		String path = req.getPathInfo();
	    String jsonrsp = null;
		
	    try
	    {
			if(path != null && path.length() > 1)	// nothing or just a /
			{
				ErrorJsonMsg error = new ErrorJsonMsg("QuizMaster","Invalid API Call");
				jsonrsp = gson.toJson(error);
			}
			else
			{
				qm = GameServlet.checkQMAuthToken(req);
				UpdateQM(qm, req);
				jsonrsp = gson.toJson(new QMasterJsonMsg(qm));
			}
	    }
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("Quizmaster", te.getMessage());
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
		QMaster qm;
		String path = req.getPathInfo();
		
	    try
	    {
			if(path != null && path.length() > 1)	// nothing or just a /
			{
				ErrorJsonMsg error = new ErrorJsonMsg("QuizMaster","Invalid API Call");
				jsonrsp = gson.toJson(error);
			}
			else
			{
				qm = GameServlet.checkQMAuthToken(req);
				DeleteQuizmaster(qm);
				SuccessJsonMsg jmsg = new SuccessJsonMsg("Success","Quizmaster and associated games deleted");
				jsonrsp = gson.toJson(jmsg);
			}
	    }
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("Quizmaster", te.getMessage());
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
		
		QMName = QMName.toLowerCase();
		MPGMethods.checkUniqueName(QMName);
		
		QMaster qm = new QMaster(QMName, QMEmail, qmapp.getAppId(), ipaddr);
		qm.setPassword(QMPassword);
		MPGMethods.Persist(qm);
		qmapp.incRegUsers();
		Log.info("New QM reg for app "+qmapp.getAppName());
		QMasterJsonMsg jmsg = new QMasterJsonMsg(qm);
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

	/*
	 * check that HTTP Basic authentication details for QMApp
	 */
	public void checkQMAppAuthentication(QMApp qma, HttpServletRequest req) throws TCCException 
	{
        final String authorisation = req.getHeader("Authorization");
        Long appid;

        if (authorisation != null && authorisation.startsWith("Basic"))
        {
            // Authorization: Basic base64credentials
            String base64Credentials = authorisation.substring("Basic".length()).trim();
            byte[] credentials = DatatypeConverter.parseBase64Binary(base64Credentials);
            // credentials = app id:app secret
            String acredentials = new String(credentials);
            final String[] values = acredentials.split(":",2);
            if(values[0] == null || values[1] == null)	// no appid or secret
            	throw new TCCException("Credentials not provided");
			try
			{
				appid = Long.parseLong(values[0]);
//	        	Log.info("App id: "+appid+" secret: "+values[1]);
	            if(appid.compareTo(qma.getAppId()) != 0)	// check id
	            	throw new TCCException("Invalid App Id");
	            if(!qma.getAppSecret().equals(values[1]))	// check app secret
	            	throw new TCCException("Invalid credentials for this App Id");
			}
			catch (TCCException | NumberFormatException nfe)
			{
				throw new TCCException("App id is missing or invalid");					
			}
            
			return;		// all good
        }
            
    	throw new TCCException("Credentials not provided");
	}

	private void UpdateQM(QMaster qm, HttpServletRequest req) throws TCCException 
	{
		String QMName, QMEmail,QMPassword;
		String ipaddr = req.getRemoteAddr();
		if((QMName = req.getParameter("qmname")) == null) QMName = "";
		if((QMEmail = req.getParameter("qmemail")) == null) QMEmail = "";
		if((QMPassword = req.getParameter("qmpassword")) == null) QMPassword = "";
		
		checkValidName(QMName);
		checkValidEmail(QMEmail);
		checkValidPassword(QMPassword);
		
		QMName = QMName.toLowerCase();
		if(!qm.getQMName().equals(QMName))	// name has changed
			MPGMethods.checkUniqueName(QMName);
		
		qm.setQMName(QMName);
		qm.setEmail(QMEmail);
		qm.setPassword(QMPassword);
		qm.setLastIPAddr(ipaddr);
		MPGMethods.Persist(qm);

		Log.info("QM details updated for "+QMName);
		return;
	}

	/*
	 * delete games and contestants attached to a quizmaster
	 * then delete the quizmaster
	 */
	private static void DeleteQuizmaster(QMaster qm) throws TCCException
	{
		List<QMGame> games = MPGMethods.GetMyGames(qm);
		if(games != null)
		{
			for(QMGame game : games)	// for each game
			{
				List<QMContestant> cons = MPGMethods.GetContestantsFromGameId(game.getGameId());
				if(cons != null)
				{
					for(QMContestant con : cons)	// delete all contestants
					{
						MPGMethods.deleteFromDatastore(con);
					}
				}
				
				MPGMethods.deleteFromDatastore(game);	// then the game
			}
		}
		
		MPGMethods.deleteFromDatastore(qm);		// and finally the qmaster
	}

}