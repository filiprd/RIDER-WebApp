package eu.sealsproject.domain.oet.recommendation.domain;

import java.io.Serializable;

import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityIndicator;

public class Requirement implements Serializable{
	
	private QualityIndicator indicator;
	
	private String threshold;
	
	public Requirement() {		
	}
	
	public Requirement(QualityIndicator indicator) {
		this.indicator = indicator;
	}

	public Requirement(QualityIndicator indicator, String threshold) {
		this.indicator = indicator;
		this.threshold = threshold;
	}

	public QualityIndicator getIndicator() {
		return indicator;
	}

	public void setIndicator(QualityIndicator indicator) {
		this.indicator = indicator;
	}


	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	@Override
	public String toString() {
		return "Requirement [indicator=" + indicator.getName() + ", threshold="
				+ threshold + "]";
	}
}
