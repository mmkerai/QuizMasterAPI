package com.thecodecentre.quizmaster;

import java.util.HashMap;
import java.util.Map;

public class ErrorJsonMsg {
	
	private Map<Object,String> error;

	 public ErrorJsonMsg()
	 { 
		 this.error = new HashMap<Object, String>(2);
	 }

	 public ErrorJsonMsg(String msg, String desc)
	 { 
		 this.error = new HashMap<Object, String>(2);
		 error.put("description", desc);
		 error.put("message", msg);
	 }

	 public void setMessage(String msg)
	 {
		 this.error.put("message", msg);
	 }

	 public void setDescription(String desc)
	 {
		 this.error.put("description", desc);
	 }

	 /*	private List<String> error;
	private String message;
	private String description;
	
	public ErrorMsg(String msg, String desc)
	{
		this.error = new ArrayList();
		this.message = msg;
		this.description = desc;
		
	}*/
	 
	 public String toJson()
	 {
		 String jstr = "{\"error\":{\"description\":\""+this.error.get("description")+
				 			"\",{\"message\":\""+this.error.get("message")+"\"}";
		 return(jstr);
	 }

}
