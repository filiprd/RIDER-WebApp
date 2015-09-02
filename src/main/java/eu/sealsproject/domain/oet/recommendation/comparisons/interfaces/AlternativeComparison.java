package eu.sealsproject.domain.oet.recommendation.comparisons.interfaces;

import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;


public interface AlternativeComparison {

	public double compare(QualityValue value1, QualityValue value2, Requirement requirement);
	
}
