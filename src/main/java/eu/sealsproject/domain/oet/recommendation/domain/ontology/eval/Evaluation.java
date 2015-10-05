package eu.sealsproject.domain.oet.recommendation.domain.ontology.eval;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.time.Instant;

@Namespace(Constants.EVAL_NS)
@RdfType("Evaluation")
public class Evaluation extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6542994318204497705L;
	
	private EvaluationSubject evaluationSubject;
	
	private EvaluationData evaluationData;
	
	private Instant evaluationTime;
	
	private Collection<QualityValue> qualityValues = new LinkedList<QualityValue>(); 
	
	/**
	 * This construction is deprecated. Use EvaluationRequest(EvaluationSubject evaluatedSubject)
	 */
	@Deprecated
	public Evaluation(){
		super();
	}
	
	/**
	 * This construction is deprecated. Use EvaluationRequest(EvaluationSubject evaluatedSubject)
	 */
	@Deprecated
	public Evaluation(String string) {
		super(string);
	}

	public Evaluation(URI uri, EvaluationSubject evaluatedSubject) {
		this.uri = uri;
		this.evaluationSubject = evaluatedSubject;
	}

	@RdfProperty(Constants.EVAL_NS + "evaluatedSubject")
	public EvaluationSubject getEvaluationSubject() {
		return evaluationSubject;
	}

	public void setEvaluationSubject(EvaluationSubject evaluatedSubject) {
		this.evaluationSubject = evaluatedSubject;
	}

	@RdfProperty(Constants.EVAL_NS + "inputData")
	public EvaluationData getEvaluationData() {
		return evaluationData;
	}

	public void setEvaluationData(EvaluationData evaluationData) {
		this.evaluationData = evaluationData;
	}

	@RdfProperty(Constants.EVAL_NS + "performedOn")
	public Instant getEvaluationTime() {
		return evaluationTime;
	}

	public void setEvaluationTime(Instant evaluationTime) {
		this.evaluationTime = evaluationTime;
	}

	@RdfProperty(Constants.EVAL_NS + "producedQualityValue")
	public Collection<QualityValue> getQualityValues() {
		return qualityValues;
	}

	public void setQualityValues(Collection<QualityValue> qualityValues) {
		this.qualityValues = qualityValues;
	}
	
	public void addQualityValue(QualityValue qualityValue){
		if(!this.qualityValues.contains(qualityValue) && qualityValue != null){
			this.qualityValues.add(qualityValue);
			qualityValue.setObtainedFrom(this);
		}
	}
}
