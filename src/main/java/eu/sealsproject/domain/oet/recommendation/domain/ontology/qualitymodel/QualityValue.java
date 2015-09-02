package eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel;

import java.net.URI;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ExecutionRequest;

@Namespace(Constants.MUO)
@RdfType("QualityValue")
public class QualityValue extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6513275497132805494L;

	private String value;
	
	private ExecutionRequest obtainedFrom;
	
	private QualityMeasure forMeasure;
	
	/**
	 * This construction is deprecated. QualityValue(String value, EvaluationRequest obtainedFrom, QualityMeasure forMeasure)
	 */
	@Deprecated
	public QualityValue(){
		super();
	}
	
	public QualityValue(URI uri, String value, ExecutionRequest obtainedFrom,
			QualityMeasure forMeasure) {
		this.uri = uri;
		this.value = value;
		this.obtainedFrom = obtainedFrom;
		this.forMeasure = forMeasure;
	}

	@RdfProperty(Constants.MUO + "qualityLiteralValue")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@RdfProperty(Constants.QUALITY_MODEL_NS + "obtainedFrom")
	public ExecutionRequest getObtainedFrom() {
		return obtainedFrom;
	}

	public void setObtainedFrom(ExecutionRequest obtainedFrom) {
		this.obtainedFrom = obtainedFrom;
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "forMeasure")
	public QualityMeasure getForMeasure() {
		return forMeasure;
	}

	public void setForMeasure(QualityMeasure forMeasure) {
		this.forMeasure = forMeasure;
	}
}
