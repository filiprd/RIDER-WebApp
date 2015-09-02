package eu.sealsproject.domain.oet.recommendation.comparisons.interfaces;

import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;

public interface Satisfiable {

	public boolean satisfiesRequirement(QualityValue value,
			Requirement requirement);
}
