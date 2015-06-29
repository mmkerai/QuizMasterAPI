package com.thecodecentre.quizmaster;
/*
 * This is a record of the quiz master host/controller who will
 * control and manage the game
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable="true")
public class QMaster {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long QMId;
	@Persistent
    private Long appId;
	@Persistent
    private String QMName;
	@Persistent
    private byte[] password;		// always store as hash
	@Persistent
    private String email;
	@Persistent
    private String lastIPAddr;	// last IP address used
	@Persistent
    private Date lastLogin;	

	public QMaster(String name, String email, long appId, String ipaddr)
	{
		this.QMName = name;
		this.email = email;
		this.appId = appId;
		this.lastIPAddr = ipaddr;
		this.lastLogin = new Date();
	}
		
	public Long getQMId()
	{
		return this.QMId;	
	}

	public void setQMName(String name)
	{
		this.QMName = name;	
	}
	
	public String getQMName()
	{
		return this.QMName;	
	}

	public void setEmail(String email)
	{
		this.email = email;	
	}
	
	public String getEmail()
	{
		return this.email;	
	}

	public void setPassword(String pass)
	{
		MessageDigest md = null;
		try {
			 md = MessageDigest.getInstance("SHA-256");
			 md.update(pass.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.password = md.digest();	
	}
	
	public Boolean verifyPassword(String pass)
	{
		MessageDigest md = null;
		try {
			 md = MessageDigest.getInstance("SHA-256");
			 md.update(pass.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("User password is "+ new String(this.password));
//		System.out.println("Test password is "+ new String(md.digest()));
		if(MessageDigest.isEqual(md.digest(), this.password))
			return true;
		else
			return false;
	}
	
	public void setAppId(Long appid)
	{
		this.appId = appid;	
	}
	
	public Long getAppId()
	{
		return this.appId;	
	}
	
	public void setLastIPAddr(String ip)
	{
		this.lastIPAddr = ip;	
	}
	
	public String getLastIPAddr()
	{
		return this.lastIPAddr;	
	}
	
	public Date getLastLogin()
	{
		return(this.lastLogin);
	}
	
	public void setLastLogin()
	{
//		this.lastLogin = new Date().getTime();	// converts to unix timestamp (millis since 1970)
		this.lastLogin = new Date();	
	}
}
