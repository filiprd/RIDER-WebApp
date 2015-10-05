package eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.SubjectCategory;

@Namespace(Constants.QMO_NS)
@RdfType("QualityCharacteristic")
public class QualityCharacteristic extends Resource{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8428528803740244920L;
	
	private String name;
	
	private String description;
		
	private Collection<QualityIndicator> qualityIndicators = new LinkedList<QualityIndicator>();
	
	private Collection<QualityCharacteristic> qualitySubCharacteristics = new LinkedList<QualityCharacteristic>();
	
	private SubjectCategory subjectCategory;

	@Deprecated
	public QualityCharacteristic(){
	}
	
	public QualityCharacteristic(URI uri, String name) {
		super(uri);
		this.name = name;
	}
	
	public QualityCharacteristic(String uri, String name) {
		super(uri);
		this.name = name;
	}


	@RdfProperty(Constants.DC_TERMS_NS + "title")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@RdfProperty(Constants.DC_TERMS_NS + "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@RdfProperty(Constants.QMO_NS + "isMeasuredWith")
	public Collection<QualityIndicator> getQualityIndicators() {
		return qualityIndicators;
	}

	public void setQualityIndicators(Collection<QualityIndicator> qualityMeasures) {
		this.qualityIndicators = qualityMeasures;
		for (QualityIndicator qualityIndicator : qualityMeasures) {
			qualityIndicator.setQualityCharacteristic(this);
		}
	}
	
	public void addQualityIndicator(QualityIndicator indicator){
		if(!getQualityIndicators().contains(indicator)){
			getQualityIndicators().add(indicator);
			indicator.setQualityCharacteristic(this);
		}
	}


	@RdfProperty(Constants.QMO_NS + "hasSubCharacteristic")
	public Collection<QualityCharacteristic> getQualitySubCharacteristics() {
		return qualitySubCharacteristics;
	}

	public void setQualitySubCharacteristics(
			Collection<QualityCharacteristic> qualitySubCharacteristics) {
		this.qualitySubCharacteristics = qualitySubCharacteristics;
	}
	
	public void addQualitySubCharacteristic(QualityCharacteristic qualityCharacteristic){
		if(!getQualitySubCharacteristics().contains(qualityCharacteristic))
			getQualitySubCharacteristics().add(qualityCharacteristic);
	}
	
	@RdfProperty(Constants.QMO_NS + "isCharacteristicOf")
	public SubjectCategory getSubjectCategory(){
		return subjectCategory;
	}
	
	public void setSubjectCategory(SubjectCategory subjectCategory){
		this.subjectCategory = subjectCategory;
	}
	
}
