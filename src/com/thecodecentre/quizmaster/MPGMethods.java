package com.thecodecentre.quizmaster;
/* 
 * This file contains all datastore methods used by the service and other useful stuff
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.thecodecentre.quizmaster.QMQuestion.QType;

public class MPGMethods {

	private static final Logger Log = Logger.getLogger(MPGMethods.class.getName());
	private static PersistenceManager pm = PMF.get().getPersistenceManager();

    public static Object Persist(Object obj)
    {
//		PersistenceManager pm = PMF.get().getPersistenceManager();
		Object savedobj = null;
        try 
        {
            savedobj = pm.makePersistent(obj);
        }
        finally
        {
//           	pm.close();
        }
        
		if(savedobj == null)
		{
			Log.info("Persistence error");
		}

        return savedobj;
    }
    
    public static void deleteFromDatastore(Object obj)
    {

            pm.deletePersistent(obj);
        
    }

	public static QMaster GetQMasterFromId(long qmid) throws TCCException
	{
		QMaster qm = null;
		
		try
		{
			qm = pm.getObjectById(QMaster.class, qmid);
		}
		catch (Exception e)
		{
			throw new TCCException("Game id does not exist: "+qmid);
		}		
		
		return qm;
	}

	@SuppressWarnings("unchecked")
	private static QMaster GetQMasterFromEmail(String email)
	{
		List<QMaster> results = new ArrayList<QMaster>();
		String q1 = "email == '"+email+"'";
	    results = (List<QMaster>) pm.newQuery(QMaster.class, q1).execute();
	    
	    if (results.isEmpty())	// no matches
	    	return null;
	    else
	    	return (QMaster) results.get(0);
	}

	@SuppressWarnings("unchecked")
	private static QMApp GetQMAppFromEmail(String email) throws TCCException
	{
		List<QMApp> results = new ArrayList<QMApp>();
		String q1 = "appEmail == '"+email+"'";
	    results = (List<QMApp>) pm.newQuery(QMApp.class, q1).execute();
	    
	    if (results.isEmpty())	// no matches
	    	throw new TCCException("App email not found: "+email);
	    else
	    	return (QMApp) results.get(0);
	}

	public static QMGame AddGame(QMaster qm, HttpServletRequest req) throws TCCException
	{
		String gName, gCategory,gSubCategory;
		String numQuestions,timeLimit, quMethod;
		
		if((gName = req.getParameter("qmgname")) == null) gName = "";
		if((gCategory = req.getParameter("qmgcat")) == null) gCategory = "";
		if((gSubCategory = req.getParameter("qmgsubcat")) == null) gSubCategory = "";
		if((numQuestions = req.getParameter("qmgnumqu")) == null) numQuestions = "";
		if((timeLimit = req.getParameter("qmgtimelimit")) == null) timeLimit = "";
		if((quMethod = req.getParameter("qmgqumethod")) == null) quMethod = "";
		
		if(gName.isEmpty())
		{
			throw new TCCException("Name is missing");
		}
		
		QMGame game = null;
		String gname = gName.toLowerCase();
		
		try
		{
			game = GetGameFromName(gName);		
		}
		catch (TCCException te)
		{
//			Log.info("Game name unique: "+gName);// all OK
		}
		
		if(game != null)
			throw new TCCException(gName+ " already exists, try another name");
		
		game = new QMGame(qm.getQMId(), gname, gCategory, gSubCategory);
		
		if(!numQuestions.isEmpty())
		{
			int nq = Integer.parseInt(numQuestions);
			if(nq > 20)	nq = 20;	// max 20 questions for now
				game.setQuestionsForGame(nq);
		}

		if(!timeLimit.isEmpty())
		{
			int tl = Integer.parseInt(timeLimit);
			if(tl > 30)	tl = 30;	// max 30 seconds for now
				game.setTimeLimit(tl);
		}
		
		game.setQMethod(quMethod);
		
		if(Persist(game) == null)
			throw new TCCException("Cannot save this game");
		
		return game;
	}

	@SuppressWarnings("unchecked")
	public static List<QMGame> GetMyGames(QMaster qm) throws TCCException
	{
		List<QMGame> results = new ArrayList<QMGame>();
		String q1 = "QMId == "+String.valueOf(qm.getQMId());
	    results = (List<QMGame>) pm.newQuery(QMGame.class, q1).execute();
	    
	    if (results.isEmpty())	// no matches
	    	throw new TCCException("There are no games set up");
	    else
	    	return results;
	}

/*	public static QMGame GetGameFromId(QMaster qm, long gameid) throws TCCException
	{
		QMGame game = null;
		
		try
		{
			game = pm.getObjectById(QMGame.class, gameid);
		}
		catch (Exception e)
		{
			throw new TCCException("Game id does not exist: "+gameid);
		}
		
		if (game.getQMId().compareTo(qm.getQMId()) != 0)	// game does not match host - not allowed
	    	throw new TCCException("This game does not belong to this quiz master");

		return game;
	}*/

	/*
	 * Get the game object from the game id.
	 */
	public static QMGame GetGameFromId(long gameid) throws TCCException
	{
		QMGame game = null;
		
		try
		{
			game = pm.getObjectById(QMGame.class, gameid);
		}
		catch (Exception e)
		{
			throw new TCCException("Game id does not exist: "+gameid);
		}
		
//		if (game.getQMId().compareTo(qm.getQMId()) != 0)	// game does not match host - not allowed
//	    	throw new TCCException("This game does not belong to this quiz master");

		return game;
	}
	@SuppressWarnings("unchecked")
	private static QMGame GetGameFromName(String gamename) throws TCCException 
	{
		String gname = gamename.toLowerCase();
		List<QMGame> results = new ArrayList<QMGame>();
		String q1 = "gameName == '"+gname+"'";
//		Log.info("JDO query is:"+q1+":");
	    results = (List<QMGame>) pm.newQuery(QMGame.class, q1).execute();
	    
	    if (results.isEmpty())	// no matches
	    {
			throw new TCCException("Game does not exist: "+gamename);
	    }
	    else
	    {
	    	return results.get(0);	// there should only be one match 
	    }
				
	}

	@SuppressWarnings("unchecked")
	public static List<QMContestant> GetContestantsFromGameId(long gameid)
	{
		List<QMContestant> results = new ArrayList<QMContestant>();
		String q1 = "gameId == "+gameid;
	    results = (List<QMContestant>) pm.newQuery(QMContestant.class, q1).execute();
	    
	    if (results.isEmpty())	// no matches
	    	return null;
	    else
	    	return results;
	}

	public static void ResetGameContestants(QMGame game)
	{
		List<QMContestant> cons = null;
		cons = GetContestantsFromGameId(game.getGameId());

		if(cons == null)
			return;
		
		for(QMContestant con : cons)
		{
			con.resetGame(game.getNumQuestions());
		}
	}

	@SuppressWarnings("unchecked")
	public static QMContestant GetContestant(QMGame game, String cname)
	{
		List<QMContestant> results = new ArrayList<QMContestant>();
		String q1 = "gameId == "+game.getGameId()+" && contestantName == '"+cname+"'";
	    results = (List<QMContestant>) pm.newQuery(QMContestant.class, q1).execute();
	    
	    if (results.isEmpty())	// no matches
	    	return null;
	    else
	    	return results.get(0);	// there should only be one
	}

	public static QMContestant GetContestantFromId(String cidstr) throws TCCException
	{
		QMContestant con;
		try
		{
			long cid = Long.parseLong(cidstr);
			con = pm.getObjectById(QMContestant.class, cid);
		}
		catch (NumberFormatException nfe)
		{
			throw new TCCException("Contestant id is invalid: "+cidstr);
		}		
		catch (Exception e)
		{
			throw new TCCException("Contestant id does not exist: "+cidstr);
		}		
		
//		if (game.getHostId() != QUIZMASTER_ID)	// game does not match host - not allowed
//	    	throw new TCCException("This game does not belong to this host");

		return con;
	}

	public static QMContestant AddContestant(QMGame game, HttpServletRequest req) throws TCCException
	{
		String cName, cEmail, cAccessCode;
		
		if((cName = req.getParameter("contestantname")) == null) cName = "";
		if((cAccessCode = req.getParameter("qmaccesscode")) == null) cAccessCode = "";
		if((cEmail = req.getParameter("contestantemail")) == null) cEmail = "";
		
		if(cName.isEmpty() || cAccessCode.isEmpty())
		{
			throw new TCCException("Name or access code is missing");
		}
		
		String cname = cName.toLowerCase();
		String code = cAccessCode.toLowerCase();
		if(GetContestant(game,cname) != null)	// if already exists
		{
			throw new TCCException("Name already exists");			
		}
		
		QMContestant con = new QMContestant(game, cname, code, cEmail);
		Persist(con);
		return con;
	}

	/* 
	 * This function used by contestant and quizmaster to get the current question for this game
	 */
	public static Question GetQuestion(long gameid) throws TCCException
	{
		QMGame mpg = null;
		
		try 
		{
			mpg = MPGMethods.GetGameFromId(gameid);
		} 
		catch (TCCException e) 
		{
			throw new TCCException("Game id is invalid");
		}
				
		Question q = mpg.getCurrentQuestion();
		if(q == null)
//			throw new TCCException("Game not yet started");
			throw new TCCException("Game not started or has finished");

		return q;
	}

	/* 
	 * This function sets the next question for this game
	 */
	public static Question SetNextQuestion(QMaster qm, long gameid) throws TCCException
	{
		QMGame mpg = null;
		
		try 
		{
			mpg = GetGameFromId(gameid);
		} 
		catch (TCCException e) 
		{
			throw new TCCException("Game id is invalid");
		}
		
		int qid = mpg.getNextQuestion();
		if(qid == 0)
			throw new TCCException("No more questions");
		
		QMQuestion mpq = QMQuestion.getQMQuestionFromId(qid);
		
		Question q = new Question(mpg, mpq);
		mpg.setCurrentQuestion(q);
		return q;
	}

	@SuppressWarnings("unchecked")
	public static QMContestant JoinGame(HttpServletRequest req) throws TCCException
	{
		String gameName, contestant,accessCode;
		
		if((gameName = req.getParameter("gamename")) == null) gameName = "";
		if((contestant = req.getParameter("contestantname")) == null) contestant = "";
		if((accessCode = req.getParameter("qmaccesscode")) == null) accessCode = "";
		
		if(contestant.isEmpty() || gameName.isEmpty() || accessCode.isEmpty())
			throw new TCCException("Game, contestant or access code missing");
		
		QMGame game = GetGameFromName(gameName);
		
		List<QMContestant> results = new ArrayList<QMContestant>();
		String q1 = "accessCode == '"+accessCode+"' && contestantName == '"+contestant+"' && gameId == "+game.getGameId();
	    results = (List<QMContestant>) pm.newQuery(QMContestant.class, q1).execute();
	    
	    if (results.isEmpty())	// no matches
	    	throw new TCCException("Login credentials incorrect");

		QMContestant con = results.get(0);	// there should only be one	match	
		con.setReady();
		return con;
	}



	public static QMApp getAppDetails(HttpSession session,String email,String ipAddr) 
	{
		QMApp app = null;
		
		try
		{
			app = GetQMAppFromEmail(email);
			app.setIPAddr(ipAddr);			
		}
		catch (TCCException te)	// new app user
		{
			Log.info(te.getMessage());
			app = new QMApp(email, ipAddr);
			Persist(app);
			app.setAppSecret();
		}

		return app;
	}

	@SuppressWarnings("unchecked")
	public static void checkUniqueName(String name) throws TCCException
	{
		List<QMaster> results = new ArrayList<QMaster>();
		String q1 = "QMName == '"+name+"'";
	    results = (List<QMaster>) pm.newQuery(QMaster.class, q1).execute();
       
        if (results.isEmpty())	// no matches
        	return;

        throw new TCCException("This name is already registered");	
	}

	public static QMApp checkValidAppId(String app_id) throws TCCException
	{
		QMApp qma;
		long appid;
		
		try
		{
			appid = Long.parseLong(app_id);
			qma = pm.getObjectById(QMApp.class, appid);
		}
		catch (Exception e)
		{
			throw new TCCException("Invalid App Id: "+app_id);
		}		
		
		qma.incrementRequests();
		return qma;
	}

	/*
	 * Get the app object and update usage stats
	 */
	public static void UpdateAppStats(Long appid) throws TCCException
	{
		QMApp qma;
		
		try
		{
			qma = pm.getObjectById(QMApp.class, appid);
		}
		catch (Exception e)
		{
			throw new TCCException("Invalid App Id: "+appid);
		}		
		
		qma.incrementRequests();
	}

	/*
     * This method checks that quizmaster username and password are valid 
     * returns user if correct null otherwise
     */
	@SuppressWarnings("unchecked")
	public static QMaster checkQMCredentials(String name, String password) throws TCCException
	{
		List<QMaster> results = new ArrayList<QMaster>();
		String q1 = "QMName == '"+name+"'";
	    results = (List<QMaster>) pm.newQuery(QMaster.class, q1).execute();
	    if(results.isEmpty())		// no matching name
	    {
	    	throw new TCCException("Username does not exist: "+name);	// username incorrect
	    }

//	    Log.info("QMaster results "+results.size());
	    QMaster user = results.get(0); 	//get full user details
    	if(user.verifyPassword(password))	// if passwords match
    		return user;
    	
    	throw new TCCException("Password is incorrect");	// password incorrect
	}

	public static long checkAuthToken(HttpServletRequest req) throws TCCException
	{
        final String authorisation = req.getHeader("Authorization");

        if (authorisation != null && authorisation.startsWith("Bearer"))
        {
            // Authorization: Bearer accesstoken
            String access_token = authorisation.substring("Bearer".length()).trim();
    		TCCToken token = TCCToken.ValidTokenList.get(access_token);
        	if(token == null)	// not in list therefore already expired or non existant
            	throw new TCCException("Invalid token");
    			
    		if(token.hasExpired())
            	throw new TCCException("Token expired");
    		
    		UpdateAppStats(token.getAppId());
    		return(token.getUserId());
        }
            
    	throw new TCCException("Credentials not provided");				
	}

    /*
     * This method checks that contestant username and password are valid 
     * returns user if correct null otherwise
     */
	@SuppressWarnings("unchecked")
	public static QMContestant checkContestantCredentials(String gname, String name, String code) throws TCCException
	{
		QMGame game = GetGameFromName(gname);
		List<QMContestant> results = new ArrayList<QMContestant>();
		String q1 = "contestantName == '"+name+"' && gameId == "+game.getGameId();
	    results = (List<QMContestant>) pm.newQuery(QMContestant.class, q1).execute();
	    if(results.isEmpty())		// no matching name
	    {
	    	throw new TCCException("Game or User does not exist: "+gname+","+name);	// username incorrect
	    }

//	    Log.info("QMaster results "+results.size());
	    QMContestant user = results.get(0); 	//get full user details
    	if(user.getAccessCode().equalsIgnoreCase(code))	// if access code match
    		return user;
    	
    	throw new TCCException("Access code is incorrect");	// password incorrect
	}

	/*
	 * Set all response headers including CORS stuff
	 */
	public static void addResponseHeaders(HttpServletResponse resp) 
	{
		resp.setContentType("application/json");
		resp.addHeader("Access-Control-Allow-Origin", "*");	// change * to exact url if required
//		resp.addHeader("Access-Control-Allow-Credentials", "true");
//      resp.addHeader("Access-Control-Max-Age", "86400");
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
		
	}



}
