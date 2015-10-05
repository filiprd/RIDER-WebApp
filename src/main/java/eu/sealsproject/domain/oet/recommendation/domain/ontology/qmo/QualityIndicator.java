package eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo;

import java.net.URI;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.MeasurementScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.UnitOfMeasure;

@Namespace(Constants.QMO_NS)
@RdfType("QualityIndicator")
public class QualityIndicator extends QualityMeasure{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8685394109618562483L;

	@Deprecated
	public QualityIndicator(){
		
	}
	public QualityIndicator(URI uri,String name, MeasurementScale scale, UnitOfMeasure measurmentUnit, 
			QualityCharacteristic characteristic) {
		super(uri, name, scale, measurmentUnit, characteristic);
	}
}
