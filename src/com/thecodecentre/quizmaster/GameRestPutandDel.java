package com.thecodecentre.quizmaster;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GameRestPutandDel {
	
  	private static final Logger Log = Logger.getLogger(GameRestPutandDel.class.getName());

	public static String RestPutsFromPath(QMaster qm, HttpServletRequest req) throws TCCException
	{
		String jsonrsp = null;
	    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String path = req.getPathInfo();
//		Log.info("Put path: "+path);
	    // /qmgames/<game id>/* 
		if(path != null && path.length() > 1)	// nothing or just a /
		{
			String[] p = path.split("/");
			// first field will always be blank after the split
			long gameid = 0;
			QMGame game = null;
			
			try
			{
				gameid = Long.parseLong(p[1]);
				game = MPGMethods.GetGameFromId(gameid);
			}
			catch (NumberFormatException nfe)
			{
				throw new TCCException("Game id is invalid");					
			}
			catch (TCCException te)
			{
				throw new TCCException(te.getMessage());					
			}

			if(p.length == 2)	// just the gameid i.e. /qmgame/<game id>
			{
				game = UpdateGame(game, req);
				jsonrsp = gson.toJson(game);
			}
			else if(p.length == 3)	// There is a control action after gameid 
			{					// i.e. /qmgame/<game id>/action
				String action = p[2];
				if(action.equals("start"))
				{
					Log.info("Start game "+gameid);
					game.resetGameStatus();
					MPGMethods.ResetGameContestants(game);
					SuccessJsonMsg success = new SuccessJsonMsg("Success","Game started");
					jsonrsp = gson.toJson(success);					
				}
				else if(action.equals("next"))
				{
					Log.info("QM Next question "+gameid);
					Question qu = MPGMethods.SetNextQuestion(qm, gameid);
					jsonrsp = gson.toJson(qu);
				}
				else if(action.equals("resume"))
				{
					Log.info("Resume "+gameid);
					if(game.getGameStatus() > game.getNumQuestions())	// check if game finished
						throw new TCCException("Game has ended");

					Question qu = MPGMethods.GetQuestion(gameid);
					jsonrsp = gson.toJson(qu);
				}
				else if(action.equals("finish"))
				{
					Log.info("Finish game "+gameid);
					game.setGameStatus(game.getNumQuestions() + 1);	// this means it has finished
					game.setCurrentQuestion(null); 		// no more questions
					ScoresJsonMsg jmsg = new ScoresJsonMsg(game);
					jsonrsp = gson.toJson(jmsg);					
				}
				else
				{
					ErrorJsonMsg error = new ErrorJsonMsg("API Error", "This request is invalid");
					jsonrsp = gson.toJson(error);												
				}
			}
			else
			{
				ErrorJsonMsg error = new ErrorJsonMsg("API Error", "This request is invalid");
				jsonrsp = gson.toJson(error);												
			}
		}
		else
		{
			throw new TCCException("Game id is missing");					
		}

		return jsonrsp;
	}

	public static String RestDelFromPath(QMaster qm, HttpServletRequest req) throws TCCException
	{
		String jsonrsp = null;
	    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String path = req.getPathInfo();

		if(path != null && path.length() > 1)	// nothing or just a /
		{
			String[] p = path.split("/");
			// first field will always be blank after the split
			long gameid = 0;
			QMGame game = null;
			
			try
			{
				gameid = Long.parseLong(p[1]);
				game = MPGMethods.GetGameFromId(gameid);
				DeleteGame(game);
				SuccessJsonMsg jmsg = new SuccessJsonMsg("Success","Game deleted");
				jsonrsp = gson.toJson(jmsg);
			}
			catch (NumberFormatException nfe)
			{
				throw new TCCException("Game id is missing");					
			}
		}
		else
		{
			throw new TCCException("Game id is missing");					
		}

		return jsonrsp;
	}

	public static QMGame UpdateGame(QMGame game, HttpServletRequest req) throws TCCException 
	{
		String gName, gCategory, gSubCategory;
		String numQuestions,timeLimit, quMethod;

		if((gName = req.getParameter("qmgname")) == null) gName = "";
		if((gCategory = req.getParameter("qmgcat")) == null) gCategory = "";
		if((gSubCategory = req.getParameter("qmgsubcat")) == null) gSubCategory = "";
		if((numQuestions = req.getParameter("qmgnumqu")) == null) numQuestions = "";
		if((timeLimit = req.getParameter("qmgtimelimit")) == null) timeLimit = "";
		if((quMethod = req.getParameter("qmgqumethod")) == null) quMethod = "";
		
		if(!gName.isEmpty())
		{
			game.setGameName(gName);
		}
		
		game.setQMethod(quMethod);
		game.setCategory(gCategory);
		game.setSubCategory(gSubCategory);
		
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
		
		MPGMethods.ResetGameContestants(game);
		MPGMethods.Persist(game);
		return game;
	}

	/*
	 * delete contestants attached to a game
	 * then delete the game
	 */
	private static void DeleteGame(QMGame game)
	{
		List<QMContestant> cons = MPGMethods.GetContestantsFromGameId(game.getGameId());
		if(cons != null)
		{
			for(QMContestant con : cons)
			{
				MPGMethods.deleteFromDatastore(con);
			}
		}
		
		MPGMethods.deleteFromDatastore(game);
	}

}
