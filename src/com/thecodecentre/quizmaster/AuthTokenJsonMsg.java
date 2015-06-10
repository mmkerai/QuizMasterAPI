package com.thecodecentre.quizmaster;

import java.util.Date;

public class AuthTokenJsonMsg
{
	private long user_id;
	private String access_token;
	private final int expires_in;		// always valid 2 hours in seconds
	private final String token_type = "Bearer";

	public AuthTokenJsonMsg(TCCToken token) 
	{
		this.user_id = token.getUserId();
		this.access_token = token.getAccessToken();
		long time = token.getExpires().getTime() - new Date().getTime(); // time diff in millisec
		expires_in = (int) time/1000;		// covert to seconds
    }

}
