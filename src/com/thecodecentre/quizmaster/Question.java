package com.thecodecentre.quizmaster;

public class Question {
	
	private int questionId;
	private int questionNo;
	private int numQuestions;
	private int timeLimit;
	private String category;
	private String subCategory;
	private String type;
	private String question;
	private String[] options;
	private String imageUrl;
	
	public Question(QMGame mpg, QMQuestion mpq)
	{
		questionId = mpq.getQuestionId();
		questionNo = mpg.getGameStatus();
		numQuestions = mpg.getNumQuestions();
		timeLimit = mpg.getTimeLimit();
		category = mpq.getCategory();
		subCategory = mpq.getSubCategory();
		type = mpq.getType();
		question = mpq.getQuestion();
		imageUrl = mpq.getImageUrl();
		options = mpq.getOptions();
	}
	
	public int getQuestionId()
	{
		return questionId;
	}
}