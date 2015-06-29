package com.thecodecentre.quizmaster;

public class Difficulty 
{
	int HARD;
	int MEDIUM;
	int EASY;
	
	public Difficulty(int h, int m, int e)
	{
		HARD = h;
		MEDIUM = m;
		EASY = e;
	}

	public int getDiffHard()
	{
		return this.HARD;
	}

	public int getDiffMedium()
	{
		return this.MEDIUM;
	}

	public int getDiffEasy()
	{
		return this.EASY;
	}

}
