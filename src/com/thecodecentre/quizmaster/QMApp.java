package com.thecodecentre.quizmaster;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.datanucleus.util.Base64;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class QMApp implements java.io.Serializable {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long appId;
	@Persistent
    private String appName;
	@Persistent
    private String appEmail;
	@Persistent
    private String appUrl;
	@Persistent
    private String appSecret;
	@Persistent
    private String lastIPAddress;
	@Persistent
    private byte[] password;
	@Persistent
    private Date registerDate;
	@Persistent
	private int regUsers;		// number of registered users
	@Persistent
	private long noOfRequests;	// number of API requests using this app
	
	public QMApp(String email, String ipaddr)
	{
		this.registerDate = new Date();
		this.appEmail = email;
		this.lastIPAddress = ipaddr;
		this.regUsers = 0;
		this.noOfRequests = 0;
	}
	
	public String getAppName()
	{
		return this.appName;	
	}

	public void setAppName(String name)
	{
		this.appName = name;	
	}
	
	public long getAppId()
	{
		return this.appId;	
	}

	public long getNoOfRequests()
	{
		return this.noOfRequests;	
	}

	public int getRegUsers()
	{
		return this.regUsers;	
	}

	public void setAppUrl(String url)
	{
		this.appUrl = url;	
	}
	
	public String getAppUrl()
	{
		return this.appUrl;	
	}

	public String getAppSecret()
	{
		return this.appSecret;	
	}

	public void setAppEmail(String email)
	{
		this.appEmail = email;	
	}
	
	public String getAppEmail()
	{
		return this.appEmail;	
	}

	public void setIPAddr(String ipaddr)
	{
		this.lastIPAddress = ipaddr;	
	}
	
	public String getIPAddr()
	{
		return this.lastIPAddress;	
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
	
	public void incRegUsers()
	{
		this.regUsers++;	
	}

	public void setRegisterDate()
	{
		this.registerDate = new Date();	
	}
	
	public Date getRegisterDate()
	{
		return this.registerDate;	
	}

	public void incrementRequests() 
	{
		this.noOfRequests++;		
	}

	public void setAppSecret()
	{
		String secret = "momordica" + Long.valueOf(this.getAppId());	// initial default secret
		MessageDigest md = null;
		try {
			 md = MessageDigest.getInstance("SHA-256");
			 md.update(secret.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] digest = md.digest();		
		//converting byte array to Hexadecimal String 
		StringBuilder sb = new StringBuilder(2*digest.length); 
		for(byte b : digest)
		{ 
			sb.append(String.format("%02x", b&0xff)); // convert to hex string
		}

		String b64 = Base64.encodeString(sb.toString());
//		System.out.println("Digest: "+sb);
//		System.out.println("Base64: "+b64.toString());
//		System.out.println("Base64 decoded: "+Base64.decodeString(b64));
		this.appSecret = b64.substring(0,32);	// use only first 32 chars
		}
}
