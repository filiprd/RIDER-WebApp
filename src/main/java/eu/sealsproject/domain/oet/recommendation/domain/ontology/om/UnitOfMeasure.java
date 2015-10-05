package eu.sealsproject.domain.oet.recommendation.domain.ontology.om;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;

@Namespace(Constants.OM_NS)
@RdfType("Unit_of)measure")
public class UnitOfMeasure {

	private String symbol;
	
	public UnitOfMeasure() {
	}

	public UnitOfMeasure(String symbol) {
		this.symbol = symbol;
	}

	@RdfProperty(Constants.QMO_NS + "symbol")
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	
}
