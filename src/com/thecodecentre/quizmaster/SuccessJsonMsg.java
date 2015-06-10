package com.thecodecentre.quizmaster;

import java.util.HashMap;
import java.util.Map;

public class SuccessJsonMsg {
	
	private Map<Object,String> success;

	 public SuccessJsonMsg(String msg, String desc)
	 { 
		 this.success = new HashMap<Object, String>(2);
		 success.put("description", desc);
		 success.put("message", msg);
	 }

	 public Map<Object, String> getSuccess()
	 {
		 return this.success;
	 }

	 public String toJson()
	 {
		 String jstr = "{\"success\":{\"description\":\""+this.success.get("description")+
				 			"\",{\"message\":\""+this.success.get("message")+"\"}";
		 return(jstr);
	 }
}
