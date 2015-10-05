package eu.sealsproject.domain.oet.recommendation.domain;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.UUID;

import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.EvaluationSubject;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;

public class Alternative {

	private String id;
	
	private LinkedList<EvaluationSubject> evaluationSubjects;
	
	private double result;


	public Alternative() {
		this.evaluationSubjects = new LinkedList<EvaluationSubject>();
		this.id = "https://github.com/filiprd/RIDER-WebApp#Alternatives-"+UUID.randomUUID();
	}

	public Alternative(String id, LinkedList<EvaluationSubject> evaluationSubjects) {
		this.id = id;
		this.evaluationSubjects = evaluationSubjects;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LinkedList<EvaluationSubject> getEvaluationSubjects() {
		return evaluationSubjects;
	}

	public void setEvaluationSubjects(LinkedList<EvaluationSubject> tools) {
		this.evaluationSubjects = tools;
	}
	
	public void addEvaluationSubject(EvaluationSubject evaluationSubject){
		this.getEvaluationSubjects().add(evaluationSubject);
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		NumberFormat format = NumberFormat.getInstance(Locale.UK);
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(8);
		format.setMinimumFractionDigits(8);
		this.result = Double.parseDouble(format.format(result));
		
	}
		
}
