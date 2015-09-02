package eu.sealsproject.domain.oet.recommendation.domain.ontology;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;

@Namespace(Constants.SEALS_METADATA_NS)
@RdfType("Interpretation")
public class Interpretation extends Resource{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9067307475950395128L;

	public Interpretation(String uri){
		super(uri);
	}

}
