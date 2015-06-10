package com.thecodecentre.quizmaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.thecodecentre.quizmaster.QMQuestion.QSubCat;
import com.thecodecentre.quizmaster.SubCatList;

@PersistenceCapable(detachable="true")
public class QMGame {
	private static final Logger Log = Logger.getLogger(QMGame.class.getName());
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long gameId;
	@Persistent
    private Long QMId;
	@Persistent
    private String gameName;
	@Persistent
    private int numQuestions;
	@Persistent
    private int timeLimit;		// time to answer each question in seconds
	@Persistent
    private int gameStatus;		// 0 means not yet started other wise question no.
	@Persistent					// if more than numquestions then game has finished
    private QMQuestion.QCat category;
	@Persistent					// if more than numquestions then game has finished
    private QMQuestion.QSubCat subCategory;
	@Persistent
    private QMethod questionMethod;
	@Persistent
    private List<Integer> questionList;
    private Question currentQuestion;
//    private int numContestants;
//    private List<MPContestant> contestants;		// not persisted just used for json message
    private QMContestants contestants;		// not persisted just used for json message

	public enum QMethod	// questioning method by game host
	{
		AUTO, MANUAL
	}
		
	public QMGame(long qmid, String name, String cat, String subcat)
	{
		this.QMId = qmid;
		this.gameName = name;
		this.category = QMQuestion.getCatFromString(cat);
		this.subCategory = QMQuestion.getSubCatFromString(subcat);
		this.questionMethod = QMethod.MANUAL;
		this.numQuestions = 0;
		this.timeLimit = 0;	// seconds default
		this.gameStatus = 0;	// not yet started
		this.questionList = new ArrayList<Integer>();
		this.currentQuestion = null;
	}

	public Long getQMId()
	{
		return this.QMId;	
	}

	public Long getGameId()
	{
		return this.gameId;	
	}

	public String getGameName()
	{
		return this.gameName;	
	}

	public void setGameName(String name)
	{
		this.gameName = name;	
	}
	
	public int getNumQuestions()
	{
		return numQuestions;	
	}

	public void setTimeLimit(int num)
	{
		this.timeLimit = num;	
	}
	
	public int getTimeLimit()
	{
		return this.timeLimit;	
	}

	public void setQMethod(String method)
	{
		QMethod qm;
		try
		{
			qm = QMethod.valueOf(method);
			this.questionMethod = qm;
		}
		catch (IllegalArgumentException e)
		{
			// category invalid so dont change
		}
	}
	
	public QMethod getQMethod()
	{
		return this.questionMethod;	
	}
	
	public void setCategory(String cat)
	{
		QMQuestion.QCat qc;
		try
		{
			qc = QMQuestion.QCat.valueOf(cat);
			this.category = qc;
		}
		catch (IllegalArgumentException e)
		{
			// category invalid so dont change
		}
	}
	
	public QMQuestion.QCat getCategory()
	{
		return this.category;	
	}
	
	public List<Integer> getQuestions()
	{
		return this.questionList;	
	}
	
	public void setSubCategory(String scat)
	{
		QSubCat qc;
		try
		{
			qc = QSubCat.valueOf(scat);
			this.subCategory = qc;
		}
		catch (IllegalArgumentException e)
		{
			// category invalid so dont change
		}
	}
	
	public int getNextQuestion()
	{
		if(gameStatus < numQuestions)
		{
			int qid = questionList.get(gameStatus);
			gameStatus++;
			return qid;
		}
		
		return 0;
	}
	
	public int getGameStatus()
	{
		return gameStatus;
	}
	
	public void resetGameStatus()
	{
		gameStatus = 0;	
	}
	
	public Question getCurrentQuestion() {
		return currentQuestion;
	}

	public void setCurrentQuestion(Question currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public void setGameStatus(int i) 
	{
		this.gameStatus = i;
	}

/*	public int getNumContestants() {
		return numContestants;
	}

	public void setNumContestants(int numContestants) {
		this.numContestants = numContestants;
	}
*/
	/*	public void setContestants(List<MPContestant> cons) 
	{
		this.contestants = cons;
	}*/

	public void setContestants(QMContestants con) 
	{
		this.contestants = con;
	}
	
	public void setQuestionsForGame(int numreq) throws TCCException
	{
		int randomNum, qno;
		
		CatList cl = CatList.getCatList(this.category);
		SubCatList scl = cl.getSubCatList(this.subCategory);
		int numCat = scl.getNumQuestions();
		if(numreq > numCat || numCat == 0)		// required is more than what is available
		{
//			Log.info("Num required: "+numreq+" available only "+numCat);
			throw new TCCException("Questions not available. Required: "+numreq+", available: "+numCat);
		}

		Random rand = new Random();
		questionList.clear();
		for(int c = 0; c < numreq; c++)
		{			
			randomNum = rand.nextInt(numCat);
			qno = scl.getQuestion(randomNum);
			if(questionList.contains(qno))
			{
				c--;	// go back as this q will be ignored
//				Log.info("Already contains "+qno);
				continue;
			}
			questionList.add(qno);	// create game question list
		}
		this.numQuestions = numreq;	// set the correct number of questions
	}
	
}
