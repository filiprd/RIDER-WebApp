package eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo;

import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

@Namespace(Constants.QMO_NS)
@RdfType("RankingFunction")
public class RankingFunction extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8233825043596068584L;

	public static RankingFunction HIGHER_BEST = new RankingFunction(Constants.QMO_NS + "HigherBest");
	
	public static RankingFunction LOWER_BEST = new RankingFunction(Constants.QMO_NS + "LowerBest");
	
	public RankingFunction(){}
	
	public RankingFunction(String uri){
		super(uri);
	}
}
