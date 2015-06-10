package com.thecodecentre.quizmaster;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thecodecentre.quizmaster.QMQuestion.QType;

@SuppressWarnings("serial")
public class ContestantServlet extends HttpServlet 
{
  	private static final Logger Log = Logger.getLogger(ContestantServlet.class.getName());
    public Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	public ErrorJsonMsg errors = new ErrorJsonMsg();

	// new game
	public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{	
        String jsonrsp = null;
		String path = req.getPathInfo();
		QMContestant qmc;
		
		try
		{
			qmc = checkContestantAuthToken(req);
			if(path != null && path.length() > 1)	// nothing or just a /
			{
				String[] p = path.split("/");
				// first field will always be blank after the split
				
				if(p.length == 2)	// contestant id? (/qmcontestants/answer)
				{
					if(p[1].equalsIgnoreCase("answer"))
					{
						SuccessJsonMsg jmsg = Answer(qmc, req);
						jsonrsp = gson.toJson(jmsg);
					}
					else
					{
						ErrorJsonMsg error = new ErrorJsonMsg("QM Contestant", "Invalid API request: "+p[1]);
						jsonrsp = gson.toJson(error);
					}
				}
			}
			else	// /qmcontestants
			{
				ErrorJsonMsg error = new ErrorJsonMsg("QM Contestant", "Invalid API request");
				jsonrsp = gson.toJson(error);
			}
		}
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QM Contestant", te.getMessage());
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
		String path = req.getPathInfo();
		QMContestant qmc;
		
		if(path != null && path.length() > 1)	// nothing or just a /
		{
			String[] p = path.split("/"); // first field will always be blank after the split				
//			Log.info("path:"+path);
			if(p.length == 2)		// i.e just /qmcontestants/action
			{
				try
				{
					String action = p[1];
					if(action.equals("authenticate"))
					{
						Log.info("authentication request");
				        String appId = req.getParameter("app_id");
				        QMApp qma = MPGMethods.checkValidAppId(appId);			
						qmc = checkContestantBasicAuthentication(qma, req);
						TCCToken at = new TCCToken(qma.getAppId(), qmc.getContestantId(), qma.getAppSecret());
						AuthTokenJsonMsg jmsg = new AuthTokenJsonMsg(at);
						jsonrsp = gson.toJson(jmsg);
					}
					else if(action.equals("question"))
					{
						qmc = checkContestantAuthToken(req);
						Question qu = MPGMethods.GetQuestion(qmc.getGameId());
						jsonrsp = gson.toJson(qu);						
					}
					else if(action.equals("scores"))
					{
						qmc = checkContestantAuthToken(req);
						QMGame game = MPGMethods.GetGameFromId(qmc.getGameId());
						if(game.getGameStatus() < game.getNumQuestions())	// check if game finished
							throw new TCCException("Game has not yet ended");
						
						ScoresJsonMsg jmsg = new ScoresJsonMsg(game);
						jsonrsp = gson.toJson(jmsg);
					}
					else	// just return contestant details
					{
						ErrorJsonMsg error = new ErrorJsonMsg("API Error", "This request is invalid");
						jsonrsp = gson.toJson(error);												
						qmc = checkContestantAuthToken(req);
						jsonrsp = gson.toJson(qmc);
					}
				}
				catch (TCCException te)
				{
					ErrorJsonMsg jmsg = new ErrorJsonMsg("QMContestant", te.getMessage());
					jsonrsp = gson.toJson(jmsg);
				}
			}
			else
			{
				ErrorJsonMsg jmsg = new ErrorJsonMsg("QMContestant", "API error");
				jsonrsp = gson.toJson(jmsg);
			}
		}
		else
		{
			ErrorJsonMsg jmsg = new ErrorJsonMsg("QMContestant", "API error");
			jsonrsp = gson.toJson(jmsg);
		}

        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
    }

	// Update contestant details
	public void doPut(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{	
	    String jsonrsp = null;
		String path = req.getPathInfo();
		QMContestant qmc;
		
		try
		{
			qmc = checkContestantAuthToken(req);
			if(path != null && path.length() > 1)	// nothing or just a /
			{
				String[] p = path.split("/");
				// first field will always be blank after the split
				QMContestant con = MPGMethods.GetContestantFromId(p[1]);
				con = UpdateContestantDetails(con, req);
				jsonrsp = gson.toJson(con);	
			}
			else
			{
				ErrorJsonMsg error = new ErrorJsonMsg("QM Contestant", "Contestant id is missing");
				jsonrsp = gson.toJson(error);	
			}
	
		}
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QM Contestant", te.getMessage());
			jsonrsp = gson.toJson(error);
		}

        MPGMethods.addResponseHeaders(resp);
		resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
	}

	// Delete contestant 
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{	
	    String jsonrsp = null;
		String path = req.getPathInfo();
        String appId = req.getParameter("app_id");
		QMContestant qmc;
		
		try
		{
			qmc = checkContestantAuthToken(req);
			if(path != null && path.length() > 1)	// nothing or just a /
			{
				MPGMethods.deleteFromDatastore(qmc);
				SuccessJsonMsg jmsg = new SuccessJsonMsg("Success", "Contestant deleted");
				jsonrsp = gson.toJson(jmsg);	
			}
			else
			{
				ErrorJsonMsg error = new ErrorJsonMsg("QM Contestant", "Contestant id is missing");
				jsonrsp = gson.toJson(error);	
			}
	
		}
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QM Contestant", te.getMessage());
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

	private QMContestant UpdateContestantDetails(QMContestant con,HttpServletRequest req) throws TCCException 
	{
		String cName, cEmail, cAccessCode;
		
		if((cName = req.getParameter("contestantname")) == null) cName = "";
		if((cAccessCode = req.getParameter("qmaccesscode")) == null) cAccessCode = "";
		if((cEmail = req.getParameter("contestantemail")) == null) cEmail = "";
		
		if(cName.isEmpty() || cAccessCode.isEmpty())
		{
			throw new TCCException("Name or access code is missing");
		}
				
		con.setAccessCode(cAccessCode);
		con.setContestantName(cName);
		con.setEmail(cEmail);
		MPGMethods.Persist(con);
		return con;
	}
	
	public static SuccessJsonMsg Answer(QMContestant con, HttpServletRequest req) throws TCCException
	{
		QMGame game = null;
		String answer = req.getParameter("answer");
		
		try 
		{
			game = MPGMethods.GetGameFromId(con.getGameId());
		} 
		catch (TCCException e) 
		{
			throw new TCCException("Game id is invalid");
		}
				
		Question q = game.getCurrentQuestion();
		if(q == null)
			throw new TCCException("Game not yet started");

		SuccessJsonMsg jmsg = null;
		
		if(game.getGameStatus() == 0)		// game has not started
		{
			jmsg = new SuccessJsonMsg("Success","Game not yet started");
		}
		else
		{
			QMQuestion qmq = QMQuestion.getQMQuestionFromId(q.getQuestionId());
			if(qmq.getQType() == QType.MULTICHOICE)		// multichoice means this is only a letter
			{
				answer = qmq.getOptionForMultichoice(answer);	// convert a, b, c or d to actual answer
			}
			con.setAnswer(game.getGameStatus(), answer);	// game status contains the current question no
			boolean score = qmq.checkAnswer(answer);
			con.setScore(game.getGameStatus(), score);
			MPGMethods.Persist(con);
			jmsg = new SuccessJsonMsg("Success","Your answer has been registered");
		}
		return jmsg;
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
            // credentials = gamename:username:password
            String acredentials = new String(credentials);
            final String[] values = acredentials.split(":",3);
            if(values[0] == null || values[1] == null || values[2] == null)	// no username or password
            	throw new TCCException("Credentials not provided");
            QMContestant qmc = MPGMethods.checkContestantCredentials(values[0],values[1],values[2]);
            	
            return qmc;
        }
            
    	throw new TCCException("Credentials not provided");
	}

	/*
	 * This function checks the access token and returns a QMContestant id
	 */
		public static QMContestant checkContestantAuthToken(HttpServletRequest req) throws TCCException
		{
	    	long user = MPGMethods.checkAuthToken(req);
	    	QMContestant qmc = MPGMethods.GetContestantFromId(String.valueOf(user));
	    	qmc.setReady();
	    	return(qmc);
		}
}