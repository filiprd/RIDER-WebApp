package eu.sealsproject.domain.oet.recommendation.domain.ontology.eval;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;

@Namespace(Constants.EVAL_NS)
@RdfType("EvaluationSubject")
public class EvaluationSubject extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6098533950546927131L;

	private String name;
	
	private SubjectCategory subjectCategory;
	
	Collection<QualityValue> qualityValues = new LinkedList<QualityValue>();

	
	public EvaluationSubject() {
	}
	
	public EvaluationSubject(String name) {
		this.name = name;
	}
	

	public EvaluationSubject(String name, SubjectCategory subjectCategory) {
		this.name = name;
		this.subjectCategory = subjectCategory;
	}
	
	public EvaluationSubject(URI uri, String name, SubjectCategory subjectCategory) {
		this.uri = uri;
		this.name = name;
		this.subjectCategory = subjectCategory;
	}

	@RdfProperty(Constants.DC_TERMS_NS + "title")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@RdfProperty(Constants.EVAL_NS + "belongsToCategory")
	public SubjectCategory getSubjectCategory() {
		return subjectCategory;
	}

	public void setSubjectCategory(SubjectCategory subjectCategory) {
		this.subjectCategory = subjectCategory;
	}
	
	public Collection<QualityValue> getQualityValues() {
		return qualityValues;
	}
	
	public void setQualityValues(Collection<QualityValue> qualityValues) {
		this.qualityValues = qualityValues;
	}
	
	public void addQualityValue(QualityValue qualityValue){
		if(!qualityValues.contains(qualityValue))
			qualityValues.add(qualityValue);
	}
}
