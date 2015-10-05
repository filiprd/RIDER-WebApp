package eu.sealsproject.domain.oet.recommendation.domain.ontology.om;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;

@Namespace(Constants.OM_NS)
@RdfType("Nominal_scale")
public class NominalScale extends MeasurementScale {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5643081803478058809L;
	
	private Collection<String> labels;
	
	public NominalScale(){
		super();
		this.labels = new LinkedList<String>();
	}

	@RdfProperty(Constants.QMO_NS + "hasLabel")
	public Collection<String> getLabels() {
		return labels;
	}

	public void setLabels(Collection<String> labels) {
		this.labels = labels;
	}
	
	public void addLebael(String label){
		if (null != labels && (!getLabels().contains(label)))
			getLabels().add(label);
	}	
	
	
}
