package eu.sealsproject.domain.oet.recommendation.domain.ontology.eval;

import java.net.URI;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.MeasurementScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.UnitOfMeasure;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityMeasure;

@Namespace(Constants.EVAL_NS)
@RdfType("QualityValue")
public class QualityValue extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6513275497132805494L;

	private String value;
	
	private Evaluation obtainedFrom;
	
	private QualityMeasure forMeasure;
	
	/**
	 * Quality measure scale.
	 */
	private MeasurementScale scale;
	
	/**
	 * Unit of measurement.
	 */
	private UnitOfMeasure measurmentUnit;
	
	/**
	 * This construction is deprecated. QualityValue(String value, EvaluationRequest obtainedFrom, QualityMeasure forMeasure)
	 */
	@Deprecated
	public QualityValue(){
		super();
	}
	
	public QualityValue(URI uri, String value, Evaluation obtainedFrom,
			QualityMeasure forMeasure) {
		this.uri = uri;
		this.value = value;
		this.obtainedFrom = obtainedFrom;
		this.forMeasure = forMeasure;
	}

	@RdfProperty(Constants.EVAL_NS + "hasLiteralValue")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@RdfProperty(Constants.EVAL_NS + "obtainedFrom")
	public Evaluation getObtainedFrom() {
		return obtainedFrom;
	}

	public void setObtainedFrom(Evaluation obtainedFrom) {
		this.obtainedFrom = obtainedFrom;
	}

	@RdfProperty(Constants.EVAL_NS + "forMeasure")
	public QualityMeasure getForMeasure() {
		return forMeasure;
	}

	public void setForMeasure(QualityMeasure forMeasure) {
		this.forMeasure = forMeasure;
	}
	
	@RdfProperty(Constants.EVAL_NS + "isMeasuredOnScale")
	public MeasurementScale getScale() {
		return scale;
	}

	public void setScale(MeasurementScale scale) {
		this.scale = scale;
	}

	@RdfProperty(Constants.EVAL_NS + "isMeasuredInUnit")
	public UnitOfMeasure getMeasurmentUnit() {
		return measurmentUnit;
	}

	public void setMeasurmentUnit(UnitOfMeasure measurmentUnit) {
		this.measurmentUnit = measurmentUnit;
	}
}
