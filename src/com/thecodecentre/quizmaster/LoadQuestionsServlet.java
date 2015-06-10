package com.thecodecentre.quizmaster;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thecodecentre.quizmaster.QMQuestion.QDiff;
import com.thecodecentre.quizmaster.QMQuestion.QSubCat;
import com.thecodecentre.quizmaster.QMQuestion.QType;

@SuppressWarnings("serial")
public class LoadQuestionsServlet extends HttpServlet 
{
	private static final Logger Log = Logger.getLogger(LoadQuestionsServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
		String jsonrsp;
	    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		resp.setContentType("text/html");

        String filename = req.getParameter("file");
        if(filename != null)
        	filename = "resources/"+filename;
        else
        	filename = "resources/QMQuestions.csv";
        
        try 
        {
			QMQuestion.LoadQuestionsFromFile(filename);
			resp.getWriter().println(QMQuestion.getNumQMQuestions()+" Questions Loaded<br/>");
			resp.getWriter().println(QMQuestion.LAST_QID-1+" is last question id");
			QuestionsByCat qmq = new QuestionsByCat();
			jsonrsp = gson.toJson(qmq);
		} 
        catch (TCCException e) 
        {
			ErrorJsonMsg error = new ErrorJsonMsg("API Error", "This request is invalid");
			jsonrsp = gson.toJson(error);												
		}
        
		resp.getWriter().println(jsonrsp);
	}
	
	// Options for CORS 
	public void doOptions(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{				
        MPGMethods.addResponseHeaders(resp);
		resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println("TCC");
	}

}
