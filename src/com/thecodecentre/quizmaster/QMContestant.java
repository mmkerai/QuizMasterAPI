package com.thecodecentre.quizmaster;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable="true")
public class QMContestant 
{
		@PrimaryKey
	    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	    private Long contestantId;
		@Persistent
	    private Long gameId;
		@Persistent
	    private String contestantName;
		@Persistent
	    private String accessCode;
		@Persistent
	    private String email;
		@Persistent
	    private CStatus status;
		@Persistent
		private List<String> answers;	// space of each answer
		@Persistent
		private byte[] score;			// whether answer correct or not
		
		public enum CStatus		//contestant status
		{
			OFFLINE, READY
		}
	
		public QMContestant(QMGame game, String name, String code, String em)
		{
			gameId = game.getGameId();
			contestantName = name;
			accessCode = code;
			email = em;
			status = CStatus.OFFLINE;
			answers = new ArrayList<String>(game.getNumQuestions()); // list only big as num questions
			for(int c=0; c < game.getNumQuestions(); c++)
				answers.add("");
			
			score = new byte[game.getNumQuestions()];
		}
		
		public long getContestantId()
		{
			return contestantId;
		}
		
		public CStatus getCStatus()
		{
			return this.status;
		}
		
		public String getContestantName()
		{
			return this.contestantName;
		}
		
		public void setContestantName(String cname)
		{
			this.contestantName = cname;
		}
		
		public String getAccessCode()
		{
			return this.accessCode;
		}
		
		public void setAccessCode(String code)
		{
			this.accessCode = code;
		}
		
		public void setEmail(String em)
		{
			this.email = em;
		}
		
		public void setReady()
		{
			this.status = CStatus.READY;;
		}

		public void setNotReady()
		{
			this.status = CStatus.OFFLINE;;
		}

		public List<String> getAnswers() {
			return answers;
		}
		
		public String getLastAnswer() 
		{
			int last = answers.size();
			if(last == 0)	// nothing set
				return " ";
			
			return answers.get(last-1);	// index start with 0
		}

		public void setAnswer(int qno, String answer) 
		{
			if(qno > 0)			// safety check
			{
/*				while(answers.size() < qno-1)
				{
					answers.add(" ");		// add blank for missing answers
				}
				if(answers.size() == qno-1)		// only add once
					answers.add(qno-1, answer);*/
				answers.set(qno-1, answer);
			}
		}

		public void resetGame(int numq) // clear answers array and scores array
		{
			answers.clear();
			for(int c=0; c < numq; c++)
				answers.add("");

//			while(--numq >= 0)
//				score[numq] = 0;
			score = new byte[numq];
			
			// set to offline, if contestant logged on then the poll will set this
			// back to READY
			status = CStatus.OFFLINE;
		}

		public long getGameId() 
		{
			return gameId;
		}

		public byte[] getScore() {
			return score;
		}

		public void setScore(int qno, boolean score) 
		{		
			
			if(qno > 0)			// safety check
			{
				byte value = (byte) ((score) ? 1 : 0);	// 1 for true and 0 for false
				this.score[qno-1] = value;
			}
		}
		

}

