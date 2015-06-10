package com.thecodecentre.quizmaster;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

public class QMContestants {

	private int numContestants;
	private List<QMContestant> contestants;

	public QMContestants(List<QMContestant> cons)
	{
		contestants = new ArrayList<QMContestant>();
		if(cons == null)
		{
			numContestants = 0;
			return;
		}
		
		numContestants = cons.size();
		for(QMContestant con : cons)
		{
			contestants.add(con);
		}
	}
	
	public List<QMContestant> getContestants()
	{
		return contestants;
	}
}
