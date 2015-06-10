package com.thecodecentre.quizmaster;

import java.util.ArrayList;
import java.util.List;

public class ScoresJsonMsg {
	
	private int numQuestions;
	private List<QandA> questions;
	private int numContestants;
	private List<CAnswers> contestants;

	public ScoresJsonMsg(QMGame game) throws TCCException
	{
		if(game.getGameStatus() <= game.getNumQuestions())	// game has not finished
			throw new TCCException("Game has not yet ended");
		
		numQuestions = game.getNumQuestions();
		questions = new ArrayList<QandA>();
		int qno = 0;
		
		for(int qid : game.getQuestions())
		{
			qno++;
			QMQuestion qu = QMQuestion.getQMQuestionFromId(qid);
			QandA q = new QandA(qu, qno);
			questions.add(q);			
		}
		
		List<QMContestant> cons = MPGMethods.GetContestantsFromGameId(game.getGameId());
		if(cons == null)
			return;
		
		numContestants = cons.size();
		contestants = new ArrayList<CAnswers>();
		for(QMContestant con : cons)
		{
			CAnswers ca = new CAnswers(con);
			contestants.add(ca);
		}
	}
	
	private class QandA			//questions and answers used for final scores
	{
		private int questionNo;
		private int questionId;
		private String question;
		private String answer;
		
		private QandA(QMQuestion qu, int qno)
		{
			questionNo = qno;
			questionId = qu.getQuestionId();
			question = qu.getQuestion();
			answer = qu.getAnswer();
		}
	}

	private class CAnswers			//answers from contestants used for final scores
	{
		private long contestantId;
		private String contestantName;
		private List<String> answers;	// in order of questions
		private byte[] scores;
		
		private CAnswers(QMContestant con)
		{
			contestantId = con.getContestantId();
			contestantName = con.getContestantName();
			answers = con.getAnswers();
			scores = con.getScore();
		}
	}
}
