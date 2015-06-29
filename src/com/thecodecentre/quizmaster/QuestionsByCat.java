package com.thecodecentre.quizmaster;

import java.util.ArrayList;
import java.util.List;

import com.thecodecentre.quizmaster.QMQuestion.QCat;
import com.thecodecentre.quizmaster.QMQuestion.QSubCat;

public class QuestionsByCat {
	
	private int totalQuestions;
	private List<QCategory> categories = new ArrayList<QCategory>();
	
	public QuestionsByCat()
	{
		totalQuestions = QMQuestion.getNumQMQuestions();
		categories.clear();
		for(CatList cl : CatList.CategoryList)
		{
			QCategory qc = new QCategory(cl);
			categories.add(qc);
		}
	}

	private class QCategory
	{
		QCat category;
		private List<QSubCategory> subCategories = new ArrayList<QSubCategory>();
		
		private QCategory(CatList cl)
		{
			category = cl.getCategory();
			for(SubCatList sc : cl.getSubCategory())
			{
				subCategories.add(new QSubCategory(sc));
			}
		}
	}
	
	private class QSubCategory
	{
		QSubCat subCategory;
		int numQuestions;
		Difficulty difficulty;
		
		private QSubCategory(SubCatList scl)
		{
			subCategory = scl.getSubCategory();
			numQuestions = scl.getNumAllQuestions();
			difficulty = scl.getDiffForSubCat();
		}
	}
}
