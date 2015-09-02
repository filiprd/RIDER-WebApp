package eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;

@Namespace(Constants.QUALITY_MODEL_NS)
@RdfType("RatioScale")
public class RatioScale extends Scale {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7503306425978855284L;
	private RankingFunction rankingFunction;
	
	@RdfProperty(Constants.QUALITY_MODEL_NS + "hasRankingFunction")
	public RankingFunction getRankingFunction() {
		return rankingFunction;
	}

	public void setRankingFunction(RankingFunction rankingFunction) {
		this.rankingFunction = rankingFunction;
	}
}
