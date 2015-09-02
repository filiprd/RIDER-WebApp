package eu.sealsproject.domain.oet.recommendation.comparisons;

import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.Satisfiable;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;

public class CharacteristicsComparison {

	/**
	 * Compares the contribution of two criteria (measures) 
	 * @param value1
	 * @param requirement1
	 * @param value2
	 * @param requirement2
	 * @return
	 */
	public static double compare(QualityValue value1, Requirement requirement1,
			QualityValue value2, Requirement requirement2) {
		
		double result1 = Double.parseDouble(value1.getValue().toString());
		double result2 = Double.parseDouble(value2.getValue().toString());
		
		if(satisfiesRequirement(value1,requirement1) && !satisfiesRequirement(value2,requirement2))
			return 9;
		if(!satisfiesRequirement(value1,requirement1) && satisfiesRequirement(value2,requirement2))
			return 0.11;
		
		if(result1 == result2)
			return 1;
		
		if(result2 == 0)
			return result1;
		
		double res = result1/result2;
		return res;
	}

	private static boolean satisfiesRequirement(QualityValue value,
			Requirement requirement) {
		
		Satisfiable satisfiable;
		try {
			satisfiable = (Satisfiable)
			Class.forName("eu.sealsproject.domain.oet.recommendation.comparisons." + value.getForMeasure().getScale().getClass().getSimpleName() +
					"Comparison").newInstance();
			return satisfiable.satisfiesRequirement(value, requirement);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
