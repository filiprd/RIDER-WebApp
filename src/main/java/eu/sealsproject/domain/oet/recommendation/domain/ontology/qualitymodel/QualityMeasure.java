package eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel;

import java.net.URI;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolCategory;

/**
 * A quality measure representation. 
 * @author Filip
 *
 */
@Namespace(Constants.QUALITY_MODEL_NS)
@RdfType("QualityMeasure")
public class QualityMeasure extends Resource{

	private static final long serialVersionUID = 4817716974281659695L;
	
	private ToolCategory toolCategory;

	/**
	 * Name of the quality measure.
	 */
	private String name;
	
	/**
	 * Quality measure scale.
	 */
	private Scale scale;
	
	/**
	 * Unit of measurement.
	 */
	private UnitOfMeasurement measurmentUnit;
	
	/**
	 * Quality characteristic that is measured with this quality measure 
	 */
	private QualityCharacteristic qualityCharacteristic;
	
	//this property is a stupid hack used to extract the quality measures for the types of tools 
	//for which there are no results. Once the real results are available, this should not be in the rdf.
//	private ToolCategory toolCategory;
	
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

	public QualityMeasure(URI uri,String name, Scale scale, UnitOfMeasurement measurmentUnit, 
			QualityCharacteristic characteristic) {
		this.uri = uri;
		this.name = name;
		this.scale = scale;
		this.measurmentUnit = measurmentUnit;
		this.qualityCharacteristic = characteristic;
		characteristic.addQualityMeasure(this);
	}

	@RdfProperty("http://purl.org/dc/terms/title")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "hasScale")
	public Scale getScale() {
		return scale;
	}

	public void setScale(Scale scale) {
		this.scale = scale;
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "hasMeasurmentUnit")
	public UnitOfMeasurement getMeasurmentUnit() {
		return measurmentUnit;
	}

	public void setMeasurmentUnit(UnitOfMeasurement measurmentUnit) {
		this.measurmentUnit = measurmentUnit;
	}
	
	@RdfProperty(Constants.QUALITY_MODEL_NS + "measuresCharacteristic")
	public QualityCharacteristic getQualityCharacteristic() {
		return qualityCharacteristic;
	}

	public void setQualityCharacteristic(QualityCharacteristic qualityCharacteristic) {
		this.qualityCharacteristic = qualityCharacteristic;
		this.qualityCharacteristic.addQualityMeasure(this);
	}

	public String toString(){
		return getName();
	}

	public void setToolCategory(ToolCategory oNTOLOGYENGINEERINGTOOL) {
		this.toolCategory = oNTOLOGYENGINEERINGTOOL;
		
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "forToolCategory")
	public ToolCategory getToolCategory() {
		return toolCategory;
	}

}
