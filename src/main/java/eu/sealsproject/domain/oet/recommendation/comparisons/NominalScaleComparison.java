package eu.sealsproject.domain.oet.recommendation.comparisons;


import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.AlternativeComparison;
import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.Satisfiable;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;

public class NominalScaleComparison implements AlternativeComparison,Satisfiable{

	
	public double compare(QualityValue value1, QualityValue value2, Requirement requirement) {
		String result1 = value1.getValue().toString();
		String result2 = value2.getValue().toString();
		String threshold = requirement.getThreshold();
		
		if(result1.equalsIgnoreCase(threshold) && !result2.equalsIgnoreCase(threshold))
			return 9;
		
		if(!result1.equalsIgnoreCase(threshold) && result2.equalsIgnoreCase(threshold))
			return 0.11;
		
		return 1;
	}

	
	public boolean satisfiesRequirement(QualityValue value,
			Requirement requirement) {

		String result = value.getValue().toString();
		String threshold = requirement.getThreshold();
		if(result.equalsIgnoreCase(threshold))
			return true;
		return false;
	}


	@Override
	public double maxDistanceComparison(Object result1, Object result2,
			String rankingFunction, double maxResultDifference, Object threshold) {
		// TODO Auto-generated method stub
		return 0;
	}

}
