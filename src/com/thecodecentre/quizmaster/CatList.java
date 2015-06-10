package com.thecodecentre.quizmaster;

import java.util.ArrayList;
import java.util.List;

import com.thecodecentre.quizmaster.QMQuestion.QCat;
import com.thecodecentre.quizmaster.QMQuestion.QSubCat;

public class CatList {
	
	public static List<CatList> CategoryList = new ArrayList<CatList>();

	private QMQuestion.QCat category;
	private List<SubCatList> subCategories;
	
	public CatList(QMQuestion.QCat cat)
	{
		category = cat;
		subCategories = new ArrayList<SubCatList>();
	}

	public QMQuestion.QCat getCategory() {
		return category;
	}

	public List<SubCatList> getSubCategory() {
		return subCategories;
	}

	public SubCatList getSubCatList(QSubCat scat)
	{
		for(SubCatList scl : this.subCategories)
		{
			if(scl.getSubCategory() == scat)
			{
				return scl;
			}
		}	
		SubCatList scl = new SubCatList(scat);
		subCategories.add(scl);
		return scl;
	}
	
	public static void initialiseCatlist()
	{
		CategoryList.clear(); 		// clear everything first
		for(QCat cat : QCat.values())
		{
			CatList cl = new CatList(cat);
			CategoryList.add(cl);
		}
	}
	
	public static CatList getCatList(QCat cat)
	{
		for(CatList cl : CategoryList)
		{
//			Log.info("clcat is "+cl.getCategory().toString()+" cat is "+cat.toString());
			if(cl.getCategory() == cat)
			{
				return cl;
			}
		}	
		return null;
	}
}
