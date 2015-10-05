package eu.sealsproject.domain.oet.recommendation.tapestry.pages;

import java.util.Collection;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import eu.sealsproject.domain.oet.recommendation.core.spring.SpringBean;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.EvaluationSubject;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;

public class ToolResults {

	@Inject
	@SpringBean("RepositoryService")
	private DataService service;
	
	@Property
	@Persist
	private EvaluationSubject evaluationSubject;
	
	@Property
	private QualityValue _qualityValue;
	
	@Property
	@Persist
	private Collection<QualityValue> qualityValues;
	
	public ToolResults showResults(String evaluationSubjectUri){
		this.evaluationSubject = service.getEvaluationSubjectObject(evaluationSubjectUri);
		this.qualityValues = service.getResultsForEvaluationSubject(evaluationSubjectUri);
		return this;
	}
	
}
