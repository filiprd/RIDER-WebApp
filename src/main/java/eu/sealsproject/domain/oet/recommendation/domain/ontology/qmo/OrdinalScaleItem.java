package eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;

@Namespace(Constants.QMO_NS)
@RdfType("OrdinalScaleItem")
public class OrdinalScaleItem extends Resource{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8812992452336402575L;

	private String label;
	
	private int order;
	
	public OrdinalScaleItem(){
		super();
	}
	
	public OrdinalScaleItem(String name, int ranking){
		super();
		this.label = name;
		this.order = ranking;
	}

	@RdfProperty(Constants.QMO_NS + "hasLabel")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@RdfProperty(Constants.QMO_NS + "hasOrder")
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	
}
