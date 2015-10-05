package eu.sealsproject.domain.oet.recommendation.domain.ontology.om;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;

@Namespace(Constants.OM_NS)
@RdfType("Cardinal_scale")
public class CardinalScale extends MeasurementScale{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6150416524254785162L;

	private double lowerBoundary;
	
	private double upperBoundary;

	@RdfProperty(Constants.QMO_NS + "hasLowerBoundary")
	public double getLowerBoundary() {
		return lowerBoundary;
	}

	public void setLowerBoundary(double lowerBoundary) {
		this.lowerBoundary = lowerBoundary;
	}

	@RdfProperty(Constants.QMO_NS + "hasUpperBoundary")
	public double getUpperBoundary() {
		return upperBoundary;
	}

	public void setUpperBoundary(double upperBoundary) {
		this.upperBoundary = upperBoundary;
	}
	
	
}
