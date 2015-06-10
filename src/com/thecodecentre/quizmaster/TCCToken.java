package com.thecodecentre.quizmaster;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import java.util.HashMap;

//import org.datanucleus.util.Base64;
import org.apache.commons.codec.binary.Base64;

public class TCCToken
{
	public static HashMap <String, TCCToken> ValidTokenList = new HashMap<String, TCCToken>();
	private String accessToken;
	private long appId;
	private long userId;
	private Date expires;
	
	public TCCToken(long appid, long userid, String secret) 
	{
        String data = Long.toString(appid) + Long.toString(userid) + secret;
		MessageDigest md = null;
		try 
		{
			md = MessageDigest.getInstance("SHA-256");
			md.update(data.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		byte[] digest = md.digest();		
		String b64 = Base64.encodeBase64String(digest);	// using apache commons
		this.accessToken = (b64.substring(0,32));	// use only first 32 chars
		this.setUserId(userid);
		this.setAppId(appid);
		long t = new Date().getTime();
		this.setExpires(new Date(t + 24*60*60*1000));		// expires 1 day later
		ValidTokenList.put(this.accessToken, this);
    }

	public String getAccessToken() {
		return accessToken;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public boolean hasExpired() 
	{
		long expiry = new Date().getTime();
		if(expiry > this.getExpires().getTime())
		{
			ValidTokenList.remove(this.getAccessToken());
			return true;
		}
		
		return false;
	}
}
