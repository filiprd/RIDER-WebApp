package eu.sealsproject.domain.oet.recommendation.domain.ontology.om;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.RankingFunction;

@Namespace(Constants.OM_NS)
@RdfType("Measurement_scale")
public class MeasurementScale extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3275089043239043478L;

	private RankingFunction rankingFunction;

	@RdfProperty(Constants.QMO_NS + "RankingFunction")
	public RankingFunction getRankingFunction() {
		return rankingFunction;
	}

	public void setRankingFunction(RankingFunction rankingFunction) {
		this.rankingFunction = rankingFunction;
	}
	
	
}
