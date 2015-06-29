package com.thecodecentre.quizmaster;

import java.util.ArrayList;
import java.util.List;

import com.thecodecentre.quizmaster.QMQuestion.QDiff;
import com.thecodecentre.quizmaster.QMQuestion.QSubCat;

public class SubCatList {
	
	private QSubCat subCategory;
	private List<Integer> hardQuestions;
	private List<Integer> mediumQuestions;
	private List<Integer> easyQuestions;
	
	public SubCatList(QSubCat subCat)
	{
		this.subCategory = subCat;
		this.hardQuestions = new ArrayList<Integer>();
		this.mediumQuestions = new ArrayList<Integer>();
		this.easyQuestions = new ArrayList<Integer>();
	}
	
	public QSubCat getSubCategory()
	{
		return this.subCategory;
	}
	
	public void addQid(int qid, QDiff diff) 
	{
		if(diff == QDiff.EASY)
			easyQuestions.add(qid);
		else if (diff == QDiff.MEDIUM)
			mediumQuestions.add(qid);
		else	// must be a hard question
			hardQuestions.add(qid);
	}
	
	public int getNumAllQuestions() 
	{
		return(easyQuestions.size()+mediumQuestions.size()+hardQuestions.size());
	}

	public int getNumQuestions(QDiff diff) 
	{
		if(diff == QDiff.EASY)
			return easyQuestions.size();
		else if (diff == QDiff.MEDIUM)
			return mediumQuestions.size();
		else if (diff == QDiff.HARD)
			return hardQuestions.size();
		else	// must be mixed questions
			return(easyQuestions.size()+mediumQuestions.size()+hardQuestions.size());
	}

	public int getQuestion(int index, QDiff diff) 
	{
		if(diff == QDiff.EASY)
			return easyQuestions.get(index);
		else if (diff == QDiff.MEDIUM)
			return mediumQuestions.get(index);
		else	// must be a hard question
			return hardQuestions.get(index);
	}

	public Difficulty getDiffForSubCat() 
	{		
		int h = hardQuestions.size();
		int m = mediumQuestions.size();
		int e = easyQuestions.size();
		
		return(new Difficulty(h,m,e));
	}
}
