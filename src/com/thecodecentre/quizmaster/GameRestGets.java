package com.thecodecentre.quizmaster;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GameRestGets {
	
  	private static final Logger Log = Logger.getLogger(GameRestGets.class.getName());

	public static String RestGetsFromPath(QMaster qm, HttpServletRequest req) throws TCCException
	{
		String jsonrsp = null;
	    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String path = req.getPathInfo();

	    // /qmgames/* 
		if(path != null && path.length() > 1)	// nothing or just a /
		{
			String[] p = path.split("/");	// first field will always be blank after the split
			Log.info("Path is:"+path);
			long gameid = 0;
			QMGame game = null;
			
			try
			{
				gameid = Long.parseLong(p[1]);
				game = MPGMethods.GetGameFromId(gameid);
			}
			catch (TCCException | NumberFormatException nfe)
			{
				throw new TCCException("Game id is missing");					
			}

			if(p.length == 2)	// just the game id? i.e. /qmgame/<game id>
			{
				jsonrsp = gson.toJson(game);
			}
			else if(p.length == 3)	// next param
			{
				if(p[2].equals("contestants"))	// /qmgame/<game id>/contestants
				{								// used for polling
					Log.info("QM poll");
					List<QMContestant> con = MPGMethods.GetContestantsFromGameId(gameid);
					if(con == null)
						throw new TCCException("There are no contestants setup for this game");
					
					jsonrsp = gson.toJson(new QMContestants(con));	
				}
				else if(p[2].equals("scores"))	// /qmgame/<game id>/scores
				{
					Log.info("Scores for game "+gameid);
					if(game.getGameStatus() < game.getNumQuestions())	// check if game finished
						throw new TCCException("Game has not yet ended");
					
					ScoresJsonMsg jmsg = new ScoresJsonMsg(game);
					jsonrsp = gson.toJson(jmsg);					
				}
				else
				{
					ErrorJsonMsg error = new ErrorJsonMsg("API Error", "This request is invalid");
					jsonrsp = gson.toJson(error);
				}
			}
		}
		else	//	/qmgame only
		{
			List<QMGame> games = MPGMethods.GetMyGames(qm);
			jsonrsp = gson.toJson(new GameJsonMsg(games));
		}
		
		return jsonrsp;
	}

}
