package com.thecodecentre.quizmaster;

import java.util.Date;

public class QMasterJsonMsg 
{	
	private long quizmasterId;
	private long applicationId;
	private String quizmasterName;
	private String quizmasterEmail;
    private String lastIPAddr;	// last IP address used
    private Date lastLogin;	

	public QMasterJsonMsg(QMaster qm)
	{
		this.quizmasterId = qm.getQMId();
		this.applicationId = qm.getAppId();
		this.quizmasterName = qm.getQMName();
		this.quizmasterEmail = qm.getEmail();
		this.lastIPAddr = qm.getLastIPAddr();
		this.lastLogin = qm.getLastLogin();
	}
}
