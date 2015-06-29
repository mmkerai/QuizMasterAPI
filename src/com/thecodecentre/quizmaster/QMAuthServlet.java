package com.thecodecentre.quizmaster;

/*
 * File to process all API calls to /qmauth?app_id=xxxxxx
 */

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("serial")
public class QMAuthServlet extends HttpServlet 
{
  	private static final Logger Log = Logger.getLogger(QMAuthServlet.class.getName());
    public Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	public ErrorJsonMsg errors = new ErrorJsonMsg();

	// allow 
	public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{	
		ErrorJsonMsg error = new ErrorJsonMsg("QuizMaster", "Invalid API call");
		String jsonrsp = gson.toJson(error);
       
        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
    }
	
	// authenticate quizmaster and/or contestant
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
        String jsonrsp = null;
		QMApp qma = null;
		String path = req.getPathInfo();
        String appId = req.getParameter("app_id");
		
		try
		{
			qma = MPGMethods.checkValidAppId(appId);
			if(path != null && path.length() > 1)	// nothing or just a /
			{
				String[] p = path.split("/");	// first field will always be blank after the split
	
				if(p[1].compareTo("quizmaster") == 0)
				{
					QMaster qm = checkQMBasicAuthentication(qma, req);
					TCCToken at = new TCCToken(qma.getAppId(), qm.getQMId(), qma.getAppSecret());
					AuthTokenJsonMsg jmsg = new AuthTokenJsonMsg(at);
					jsonrsp = gson.toJson(jmsg);
				}
				else if(p[1].compareTo("contestant") == 0)
				{
					QMContestant qmc = checkContestantBasicAuthentication(qma, req);
					TCCToken at = new TCCToken(qma.getAppId(), qmc.getContestantId(), qma.getAppSecret());
					AuthTokenJsonMsg jmsg = new AuthTokenJsonMsg(at);
					jsonrsp = gson.toJson(jmsg);
				}
				else
				{
					Log.info("param is "+p[1]);
					ErrorJsonMsg error = new ErrorJsonMsg("QM Authentication","Invalid API request");
					jsonrsp = gson.toJson(error);
				}
			}
			else
			{
				Log.info("path is "+path);
				ErrorJsonMsg error = new ErrorJsonMsg("QM Authentication","Invalid API request");
				jsonrsp = gson.toJson(error);
			}
		}
		catch (TCCException e)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QM Authentication",e.getMessage());
			jsonrsp = gson.toJson(error);
		}

        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
    }

	//
	public void doPut(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
		doPost(req, resp);
	}
	
	
	// Options for CORS 
	public void doOptions(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{				
        MPGMethods.addResponseHeaders(resp);
		resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println("TCC");
	}

	/*
	 * check that HTTP Basic authentication details for Quizmaster
	 */
	public static QMaster checkQMBasicAuthentication(QMApp qma, HttpServletRequest req) throws TCCException 
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
	 * check that HTTP Basic authentication details for contestant
	 */
	public static QMContestant checkContestantBasicAuthentication(QMApp qma, HttpServletRequest req) throws TCCException 
	{
        final String authorisation = req.getHeader("Authorization");

        if (authorisation != null && authorisation.startsWith("Basic"))
        {
            // Authorization: Basic base64credentials
            String base64Credentials = authorisation.substring("Basic".length()).trim();
            byte[] credentials = DatatypeConverter.parseBase64Binary(base64Credentials);
            // credentials = gamename:username:accesscode
            String acredentials = new String(credentials);
            final String[] values = acredentials.split(":",3);
            if(values[0] == null || values[1] == null || values[2] == null)	// no username or password
            	throw new TCCException("Credentials not provided");
            QMContestant qmc = MPGMethods.checkContestantCredentials(values[0],values[1],values[2]);
            	
            return qmc;
        }
            
    	throw new TCCException("Credentials not provided");
	}

}