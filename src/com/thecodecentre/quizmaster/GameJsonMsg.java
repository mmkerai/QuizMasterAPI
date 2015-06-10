package com.thecodecentre.quizmaster;

import java.util.ArrayList;
import java.util.List;

public class GameJsonMsg 
{	
	private int numGames;
	private List<QMGame> games;

	public GameJsonMsg(List<QMGame> mpgames)
	{
		numGames = mpgames.size();
		games = new ArrayList<QMGame>();
		
		for(QMGame game : mpgames)
		{	// update game details with the contestant list before jsonifying it
			List<QMContestant> cons = MPGMethods.GetContestantsFromGameId(game.getGameId());
//				game.setNumContestants(cons.size());
			game.setContestants(new QMContestants(cons));
			
			games.add(game);
		}		
	}
}
