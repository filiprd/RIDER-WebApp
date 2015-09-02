package eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;

@Namespace(Constants.MUO)
@RdfType("UnitOfMeasurement")
public class UnitOfMeasurement {

	private String symbol;
	
	public UnitOfMeasurement() {
	}

	public UnitOfMeasurement(String symbol) {
		this.symbol = symbol;
	}

	@RdfProperty(Constants.MUO + "prefSymbol")
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	
}
