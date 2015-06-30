package com.thecodecentre.quizmaster;

import java.util.ArrayList;
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
//			Log.info("Path is:"+path);
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

			if(p.length == 2)	// just the game id? i.e. /qmgames/<game id>
			{
				jsonrsp = gson.toJson(game);
			}
			else if(p.length == 3 || p.length == 4)	// next 2 params
			{
				if(p[2].equals("contestants"))	// /qmgames/<game id>/contestants
				{								// used for polling
					Log.info("QM poll");
					List<QMContestant> con = MPGMethods.GetContestantsFromGameId(gameid);
					if(con == null)
						throw new TCCException("There are no contestants setup for this game");
					
					jsonrsp = gson.toJson(new QMContestants(con));	
				}
				else if(p[2].equals("scores"))	// /qmgames/<game id>/scores
				{
					Log.info("Scores for game "+gameid);
					if(game.getGameStatus() < game.getNumQuestions())	// check if game finished
						throw new TCCException("Game has not yet ended");
					
					ScoresJsonMsg jmsg = new ScoresJsonMsg(game);
					jsonrsp = gson.toJson(jmsg);					
				}
				else if(p[2].equals("questions"))	// /qmgames/<game id>/questions
				{
					Log.info("Questions for game "+gameid);
					List<Integer> qs = game.getQuestions();
					if(p.length == 4)	// if a question number i.e. /qmgames/<game id>/questions/ques no
					{
						int questionNo = Integer.parseInt(p[3]);
						if(questionNo == 0 || questionNo > qs.size())
							throw new TCCException("Question number invalid for this game");
						int qid = qs.get(questionNo - 1);	// index starts at zero
						QMQuestion qmq = QMQuestion.getQMQuestionFromId(qid);
						jsonrsp = gson.toJson(qmq);				
					}
					else	// all the questions for this game
					{
						List<QMQuestion> qlist = new ArrayList<QMQuestion>();
						for(Integer qno : qs)
						{
							qlist.add(QMQuestion.getQMQuestionFromId(qno));
						}
						jsonrsp = gson.toJson(qlist);				
					}
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
