package com.thecodecentre.quizmaster;

import java.util.ArrayList;
import java.util.List;

import com.thecodecentre.quizmaster.QMQuestion.QSubCat;

public class SubCatList {
	
	private QSubCat subCategory;
	private List<Integer> questionId;
	
	public SubCatList(QSubCat subCat)
	{
		this.subCategory = subCat;
		this.questionId = new ArrayList<Integer>();
	}
	
	public QSubCat getSubCategory() 
	{
		return this.subCategory;
	}
	
	public void addQid(int qid) 
	{
		questionId.add(qid);
	}
	
	public int getNumQuestions() 
	{
		return questionId.size();
	}

	public int getQuestion(int index) 
	{
		return questionId.get(index);
	}
}
