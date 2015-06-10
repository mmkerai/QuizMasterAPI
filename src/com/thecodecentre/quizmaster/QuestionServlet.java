package com.thecodecentre.quizmaster;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mortbay.log.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("serial")
public class QuestionServlet extends HttpServlet 
{
  	private static final Logger Log = Logger.getLogger(QuestionServlet.class.getName());
    public Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	public ErrorJsonMsg errors = new ErrorJsonMsg();

	// get question info
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{
        String jsonrsp = null;
		String path = req.getPathInfo();
		QMaster qm = null;
		
		try
		{
			qm = GameServlet.checkQMAuthToken(req);
			if(path != null && path.length() > 1)	// if there is a question no.
			{
				String[] p = path.split("/");
				// first field will always be blank after the split
				if(p.length == 2)	// question id
				{
					int qid = Integer.parseInt(p[1]);	//get question id
					QMQuestion qu = QMQuestion.getQMQuestionFromId(qid);
					jsonrsp = gson.toJson(qu);					
				}
				else
				{
					ErrorJsonMsg error = new ErrorJsonMsg("API Error", "This request is invalid");
					jsonrsp = gson.toJson(error);
				}
			}
			else	// give info on questions
			{
				if(QMQuestion.getNumQMQuestions() == 0)	// no questions loaded
					QMQuestion.LoadQuestionsFromFile(QMQuestion.qfilename);

				QuestionsByCat qmq = new QuestionsByCat();
				jsonrsp = gson.toJson(qmq);
			}
		}
		catch (TCCException te)
		{
			ErrorJsonMsg error = new ErrorJsonMsg("QM Questions", te.getMessage());
			jsonrsp = gson.toJson(error);
		}

        MPGMethods.addResponseHeaders(resp);
    	resp.setStatus(HttpServletResponse.SC_OK);		// request ok
		resp.getWriter().println(jsonrsp);
    }
	
	// Options for CORS 
	public void doOptions(HttpServletRequest req, HttpServletResponse resp)throws IOException 
	{				
        MPGMethods.addResponseHeaders(resp);
		resp.setStatus(HttpServletResponse.SC_OK);		// request ok
	}

}
