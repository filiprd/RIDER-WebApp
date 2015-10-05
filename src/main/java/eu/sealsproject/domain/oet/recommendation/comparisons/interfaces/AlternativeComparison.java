package eu.sealsproject.domain.oet.recommendation.comparisons.interfaces;

import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;


public interface AlternativeComparison {

	public double compare(QualityValue value1, QualityValue value2, Requirement requirement);

	public double maxDistanceComparison(Object result1, Object result2, String rankingFunction, double maxResultDifference, Object threshold);
}
