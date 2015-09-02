package eu.sealsproject.domain.oet.recommendation.comparisons;


import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.AlternativeComparison;
import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.Satisfiable;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.RankingFunction;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.RatioScale;

public class RatioScaleComparison implements  AlternativeComparison, Satisfiable{

	/**
	 * Compares value1 to value2
	 */

	public double compare(QualityValue value1, QualityValue value2, Requirement requirement) {
		double result1 = Double.parseDouble(value1.getValue().toString());
		double result2 = Double.parseDouble(value2.getValue().toString());
		double threshold = Double.parseDouble(requirement.getThreshold());
		RatioScale ratioScale = RatioScale.class.cast(value1.getForMeasure().getScale());
		if(ratioScale.getRankingFunction().equals(RankingFunction.HIGHER_BEST)){
				if(result1 >= threshold && threshold > result2)
					return 9;
				
				if(result2 >= threshold && threshold > result1)
					return 0.11;
				
				if(result1 >= threshold && result2 >= threshold && result1 > result2)
					return 3;
				if(result1 >= threshold && result2 >= threshold && result2 > result1)
					return 0.33;
				
				if(result1 <= threshold && result2 <= threshold && result1 > result2)
					return 3;
				if(result1 <= threshold && result2 <= threshold && result2 > result1)
					return 0.33;
				
				if(result1 == result2)
					return 1;
				
		}
		if(ratioScale.getRankingFunction().equals(RankingFunction.LOWER_BEST)){
			if(result1 <= threshold && threshold < result2)
				return 9;
			
			if(result2 <= threshold && threshold < result1)
				return 0.11;
			
			if(result1 >= threshold && result2 >= threshold && result1 > result2)
				return 0.33;
			if(result1 >= threshold && result2 >= threshold && result2 > result1)
				return 3;
			
			if(result1 <= threshold && result2 <= threshold && result1 > result2)
				return 0.33;
			if(result1 <= threshold && result2 <= threshold && result2 > result1)
				return 3;
			
			if(result1 == result2)
				return 1;
		}
		return -1;
	}
// QualityValue value

	public boolean satisfiesRequirement(QualityValue value,
			Requirement requirement) {

		double result = Double.parseDouble(value.getValue().toString());
		double threshold = Double.parseDouble(requirement.getThreshold());
		
		RatioScale ratioScale = RatioScale.class.cast(value.getForMeasure().getScale());
		if(ratioScale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
			if(result>threshold)
				return true;
			else
				return false;
		if(ratioScale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
			if(result>threshold)
				return false;
			else
				return true;
		return false;
	}
}
