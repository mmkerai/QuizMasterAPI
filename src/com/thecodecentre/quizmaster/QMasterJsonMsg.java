package com.thecodecentre.quizmaster;

public class QMasterJsonMsg 
{	
	private long QuizMasterId;
	private long ApplicationId;
	private String QuizMasterName;
	private String QuizMasterEmail;

	public QMasterJsonMsg(long qmid, long appid, String qmname, String qmemail)
	{
		this.QuizMasterId = qmid;
		this.ApplicationId = appid;
		this.QuizMasterName = qmname;
		this.QuizMasterEmail = qmemail;		
	}
}
