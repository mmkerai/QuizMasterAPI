package com.thecodecentre.quizmaster;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.thecodecentre.quizmaster.SubCatList;

public class QMQuestion {
	
	public static final int FIRST_QID = 19710714;	// first question no.
	public static int LAST_QID = FIRST_QID;			// increment as you load questions from file
	public static List<QMQuestion> QMQuestionList = new ArrayList<QMQuestion>();
	public static String qfilename = "resources/QMQuestions.csv";
	
    private int questionId;
    private int used;		// how many times used
    private int correct;		// how many times correct answer
    private QCat category;
    private QSubCat subCategory;
    private QDiff difficulty;
    private QType type;
    private String question;
    private String imageUrl;
    private String[] options;		// if multichoice
    private String answer;			// for multichoice or text

	public enum QType	// type of question
	{
		MULTICHOICE, // multiple choice
		TEXT,
		PUZZLE		// a puzzle for which response is boolean
	}
	
	public enum QCat	// category of questions
	{
		TV, MOVIES, SCIENCE, GEOGRAPHY, HISTORY, LITERATURE, 
		MISC, MUSIC, ANIMALS, FOOD, RELIGION, SPORTS,
		VIDEOGAMES, CARTOONS, TECHNOLOGY
	}

	public enum QSubCat	//  subcategory of questions
	{
		FOOTBALL, FLAGS, CAPITALS, GENERAL, CELEBRITIES, LOGOS, ALBUMCOVERS, MOVIESTARS, 
		ACTORS, ACTRESSES, COUNTRIES, BANDLOGOS, FOODLOGOS, VILLAINS, FOURSTARS, LANDMARKS
	}

	public enum QDiff	//  difficulty of questions
	{
		EASY, MEDIUM, HARD
	}

	// constructor
	public QMQuestion(QType type, QCat cat, QSubCat scat)
	{
		this.questionId = LAST_QID++;
		this.category = cat;
		this.setType(type);
		this.used = 0;
		this.correct = 0;
		CatList cl = CatList.getCatList(cat);
		if(cl != null)
		{		
			SubCatList scl = cl.getSubCatList(scat);
			scl.addQid(this.questionId);
		}
	}
	
	public String getSubCategory() {
		return subCategory.toString();
	}

	public void setSubCategory(QSubCat subcat) {
		this.subCategory = subcat;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getCategory() {
		return category.toString();
	}

	public void setCategory(QCat category) {
		this.category = category;
	}

	public String getType() {
		return type.toString();
	}

	public QType getQType() {
		return type;
	}

	public void setType(QType type) {
		this.type = type;
	}

	public int getQuestionId() {
		return questionId;
	}

	/*
	 * set the options for a multichoice question.
	 * First one is always right answer so mix them up
	 */
	public void setOptions(String[] opt) 
	{
		shuffleArray(opt);		// mixup the options
		this.options = opt;
	}

	public String[] getOptions() 
	{
		return this.options;
	}

	private void incUsed()	// increment the used count for stats
	{
		this.used++;
	}
	
	private boolean correct() // used to indicate correct answer
	{
		this.correct++;
		return true;
	}
/*
 * Method checks the submitted answer with the correct one.
 * if answer is of type text then min length is 3 and
 * use Damerau-Levenshtein algorithm to get accuracy (edit distance) of answer 	
 */
	public boolean checkAnswer(String testanswer)
	{
		this.used++;			// increment usage count
		String correctAns = answer.toLowerCase();
				
		if(this.type == QType.TEXT)		// text string to compare
		{
			String submitAns = testanswer.toLowerCase();
			if(submitAns.length() < 3)		// answer must be atleast 3 chars
				return false;
			
			if(correctAns.contains(submitAns))
				return(correct());
			
			DamerauLevenshteinAlgorithm dla = new DamerauLevenshteinAlgorithm(1,1,1,1);
			int dl = dla.execute(correctAns, submitAns);
//			Log.info("Answer is "+answer+" DL is "+dl);
			
			if(dl < correctAns.length()/2)		// dl is small enough to be OK
				return(correct());
		}
		else if(this.type == QType.MULTICHOICE)		// multichoice option compare
		{
			if(answer.compareToIgnoreCase(testanswer) == 0)
				return(correct());
		}
		
		return false;
	}

	public QDiff getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(QDiff difficulty) {
		this.difficulty = difficulty;
	}

	/*
	 * This method return the full question from the table of all questions
	 * based on the question id. The id numbers start with FIRST_QID and end
	 * with LAST_QID. If questions have not been loaded the it will load from default 
	 * file which is QMQuestions.csv
	 */
	public static QMQuestion getQMQuestionFromId(int qid) throws TCCException
	{
		if(FIRST_QID == LAST_QID)	// no questions loaded
			LoadQuestionsFromFile(qfilename);
			
		if(qid < FIRST_QID || qid >= LAST_QID)
			throw new TCCException("Question id does not exist");
		
		return (QMQuestionList.get(qid-FIRST_QID));
	}

	public static int getNumQMQuestions() 
	{
		return QMQuestionList.size();
	}

	public static void resetQMQuestionsList() 
	{
		QMQuestionList.clear();
		LAST_QID = FIRST_QID;			// restart from beginning
	}

	/*
	 * Matches subcat string. Default is general
	 */
	public static QSubCat getSubCatFromString(String sc)
	{
		for(QSubCat scat : QSubCat.values())
		{
			if(sc.equalsIgnoreCase(scat.toString())) return(scat);
		}   	

    	return QSubCat.GENERAL;
	}
	
	public static QDiff getDiffFromString(String d)
	{
    	if(d.equalsIgnoreCase(QDiff.MEDIUM.toString())) return(QDiff.MEDIUM);
    	if(d.equalsIgnoreCase(QDiff.HARD.toString())) return(QDiff.HARD);

    	return QDiff.EASY;
	}
	
	/*
	 * Matches category string. Default is MISC
	 */
	public static QCat getCatFromString(String category) 
	{
		for(QCat cat : QCat.values())
		{
			if(category.equalsIgnoreCase(cat.toString())) return(cat);
		}   	
    	return QCat.MISC;
	}

	/*
	 * Load questions from a pre-formatted file in the global QMQuestion list
	 */
    public static void LoadQuestionsFromFile(String qfile) throws TCCException
    
    {
		resetQMQuestionsList();	// clear the list
		CatList.initialiseCatlist();// list of categorised questions
        QType qtype;
        String cat, subcat, diff,ans,question,imagefile;

		Reader csvfile = null;
		CSVParser parser = null;
        try
        {
        	csvfile = new FileReader(qfile);
            parser = new CSVParser(csvfile, CSVFormat.EXCEL.withHeader());
	        for (CSVRecord record : parser) 
	        {
	            cat = record.get("Category");
	            subcat = record.get("Subcategory");
	            diff = record.get("Difficulty");
	            question = record.get("Question");
	            ans = record.get("Answer");
	            imagefile = record.get("Image");
	            
	            // ensure there is a question and answer otherwise ignore
	            if(question.length() == 0 || ans.length() == 0)
	            	continue;
	            
	        	// Options split by # and first one is always the correct answer
	        	String[] options = ans.split("#");
	        	if(ans.length() == options[0].length())	// only one answer i.e. not multichoice
	        		qtype = QType.TEXT;
	        	else
	        		qtype = QType.MULTICHOICE;
	        	
	        	QCat category = getCatFromString(cat);
	            QSubCat scat = getSubCatFromString(subcat);
	            QMQuestion mpq = new QMQuestion(qtype, category, scat);
	        	mpq.setQuestion(question);
	        	String answer = options[0];		// first option being the correct answer
	            mpq.setSubCategory(scat);
	            QDiff difficulty = getDiffFromString(diff);
	            mpq.setDifficulty(difficulty);
	        	mpq.setAnswer(answer);
	        	if(qtype == QType.MULTICHOICE)
	        		mpq.setOptions(options);
	        	
	        	if(imagefile != null)
	        		mpq.setImageUrl(imagefile);
	        	
	    		QMQuestionList.add(mpq);
	        }	
	        
	        parser.close();
	        csvfile.close();
//	        Log.info("No of Questions loaded "+ QMQuestion.getNumQMQuestions());
        }
        catch (IOException fnfe)
        {
    		throw new TCCException("Question file not found: "+qfile);
        }
	}
	
    private void shuffleArray(String[] array)
    {
        int index;
        String temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

	public String getOptionForMultichoice(String answer) 
	{
		String submitAns = answer.toLowerCase();
		if(submitAns.length() > 1)		// answer must be only 1 chars (a,b,c etc)
			return "n/a";
		
		// convert a,b,c,d to index
		int index = 0;
		char opt = submitAns.charAt(0);
		if(opt == 'a') index = 0;
		else if(opt == 'b') index = 1;
		else if(opt == 'c') index = 2;
		else if(opt == 'd') index = 3;
		else if(opt == 'e') index = 4;
		else index = 99;
		
		if(index >= options.length)
			return "n/a";		// not enough options
		
		return options[index];
	}
}
