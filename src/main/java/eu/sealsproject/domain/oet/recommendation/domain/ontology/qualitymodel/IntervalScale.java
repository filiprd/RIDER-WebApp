package eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;

@Namespace(Constants.QUALITY_MODEL_NS)
@RdfType("IntervalScale")
public class IntervalScale extends Scale {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4732770086006215217L;

	private int lowerBoundry;
	
	private int upperBoundry;
	
	private RankingFunction rankingFunction;

	@RdfProperty(Constants.QUALITY_MODEL_NS + "lowerBoundry")
	public int getLowerBoundry() {
		return lowerBoundry;
	}

	public void setLowerBoundry(int lowerBoundry) {
		this.lowerBoundry = lowerBoundry;
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "upperBoundry")
	public int getUpperBoundry() {
		return upperBoundry;
	}

	public void setUpperBoundry(int upperBoundry) {
		this.upperBoundry = upperBoundry;
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "hasRankingFunction")
	public RankingFunction getRankingFunction() {
		return rankingFunction;
	}

	public void setRankingFunction(RankingFunction rankingFunction) {
		this.rankingFunction = rankingFunction;
	}
}
