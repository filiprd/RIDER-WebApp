package eu.sealsproject.domain.oet.recommendation.domain.ontology.om;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.OrdinalScaleItem;

@Namespace(Constants.OM_NS)
@RdfType("Ordinal_scale")
public class OrdinalScale extends MeasurementScale {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7908889264131760704L;
	
	private Collection<OrdinalScaleItem> ordinalScaleItems;
	
	public OrdinalScale(){
		super();
		ordinalScaleItems = new LinkedList<OrdinalScaleItem>();
	}

	@RdfProperty(Constants.QMO_NS + "hasOrdinalScaleItem")
	public Collection<OrdinalScaleItem> getOrdinalScaleItems() {
		return ordinalScaleItems;
	}

	public void setOrdinalScaleItems(Collection<OrdinalScaleItem> ordinalScaleItems) {
		this.ordinalScaleItems = ordinalScaleItems;
	}
	
	public void addOrdinalScaleItem(OrdinalScaleItem item){
		if (null != ordinalScaleItems && (!getOrdinalScaleItems().contains(item)))
			getOrdinalScaleItems().add(item);
	}	
	
}
