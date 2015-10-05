package eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo;

import java.net.URI;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.MeasurementScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.UnitOfMeasure;

/**
 * A quality measure representation. 
 * @author Filip
 *
 */
@Namespace(Constants.QMO_NS)
@RdfType("QualityMeasure")
public class QualityMeasure extends Resource{

	private static final long serialVersionUID = 4817716974281659695L;

	/**
	 * Name of the quality measure.
	 */
	private String name;
	
	private String description;
	
	/**
	 * Quality measure scale.
	 */
	private MeasurementScale scale;
	
	/**
	 * Unit of measurement.
	 */
	private UnitOfMeasure measurmentUnit;
	
	/**
	 * Quality characteristic that is measured with this quality measure 
	 */
	private QualityCharacteristic qualityCharacteristic;
	
	
	/**
	 * This construction is deprecated. Use QualityMeasure(String name, Scale scale, String measurmentUnit)
	 */
	@Deprecated
	public QualityMeasure() {}
	
	/**
	 * This construction is deprecated. Use QualityMeasure(String name, Scale scale, String measurmentUnit)
	 */
	@Deprecated
	public QualityMeasure(String uri) {
		super(uri);
	}

	public QualityMeasure(URI uri,String name, MeasurementScale scale, UnitOfMeasure measurmentUnit, 
			QualityCharacteristic characteristic) {
		this.uri = uri;
		this.name = name;
		this.scale = scale;
		this.measurmentUnit = measurmentUnit;
		this.qualityCharacteristic = characteristic;
		if(this instanceof QualityIndicator)
			characteristic.addQualityIndicator((QualityIndicator)this);
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

	@RdfProperty(Constants.QMO_NS + "hasScale")
	public MeasurementScale getScale() {
		return scale;
	}

	public void setScale(MeasurementScale scale) {
		this.scale = scale;
	}

	@RdfProperty(Constants.QMO_NS + "hasUnitOfMeasurement")
	public UnitOfMeasure getMeasurmentUnit() {
		return measurmentUnit;
	}

	public void setMeasurmentUnit(UnitOfMeasure measurmentUnit) {
		this.measurmentUnit = measurmentUnit;
	}
	
	@RdfProperty(Constants.QMO_NS + "measuresCharacteristic")
	public QualityCharacteristic getQualityCharacteristic() {
		return qualityCharacteristic;
	}

	public void setQualityCharacteristic(QualityCharacteristic qualityCharacteristic) {
		this.qualityCharacteristic = qualityCharacteristic;
		if(this instanceof QualityIndicator)
			this.qualityCharacteristic.addQualityIndicator((QualityIndicator)this);
	}

	public String toString(){
		return getName();
	}

}
