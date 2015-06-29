package com.thecodecentre.quizmaster;


import java.util.ArrayList;
import java.util.List;

public class QMastersJMsg {

	private int numQuizmasters;
	private List<QMasterJsonMsg> quizmasters;

	public QMastersJMsg(List<QMaster> qms)
	{
		if(qms == null)
		{
			numQuizmasters = 0;
			return;
		}
		
		quizmasters = new ArrayList<QMasterJsonMsg>();
		numQuizmasters = qms.size();
		for(QMaster qm : qms)
		{
			quizmasters.add(new QMasterJsonMsg(qm));
		}
	}
	
}
