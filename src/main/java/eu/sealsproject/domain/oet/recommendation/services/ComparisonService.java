package eu.sealsproject.domain.oet.recommendation.services;

import eu.sealsproject.domain.oet.recommendation.comparisons.CharacteristicsComparison;
import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.AlternativeComparison;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.RatioScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.RankingFunction;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;

public class ComparisonService {

	// compares two alternatives w.r.t a criteria
	public static double simpleComparison(Alternative alternative1, Alternative alternative2,
			Requirement requirement, DataService service) {
		
		QualityValue value1 = service.getQualityValueForAlternative(alternative1, requirement.getIndicator().getUri().toString());
		QualityValue value2 = service.getQualityValueForAlternative(alternative2, requirement.getIndicator().getUri().toString());

		

		
		try {
			
			AlternativeComparison comparator = 	(AlternativeComparison)
				Class.forName("eu.sealsproject.domain.oet.recommendation.comparisons." + requirement.getIndicator().getScale().getClass().getSimpleName()
						+ "Comparison").newInstance();
			
			double result = comparator.compare(value1, value2, requirement);
			
			if(result<0){
				System.err.println("RESULT OF COMPARISON IS LESS THAN ZERO");
			}
			
			return result;
			
			
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
			
		
		return -1;
	}
	
	// FOR using max distance comparisons from anp-lib
	public static double maxDistanceComparison(Alternative alternative1, Alternative alternative2,
			Requirement requirement, DataService service) {
		
		QualityValue value1 = service.getQualityValueForAlternative(alternative1, requirement.getIndicator().getUri().toString());
		QualityValue value2 = service.getQualityValueForAlternative(alternative2, requirement.getIndicator().getUri().toString());
		
		
		try {
			
			AlternativeComparison comparator = 	(AlternativeComparison)
				Class.forName("eu.sealsproject.domain.oet.recommendation.comparisons." + requirement.getIndicator().getScale().getClass().getSimpleName()
						+ "Comparison").newInstance();
			
			double maxResultDifference = service.getResultsDifference(requirement.getIndicator().getUri().toString());
			
			double result = -1;
			if(requirement.getIndicator().getScale() instanceof RatioScale){
				RatioScale scale = (RatioScale) requirement.getIndicator().getScale();
				String rankingFunction = "";
				if(scale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
					rankingFunction = "Higher";
				if(scale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
					rankingFunction = "Lower";
				result = comparator.maxDistanceComparison(Double.parseDouble(value1.getValue()), 
						Double.parseDouble(value2.getValue()), rankingFunction, maxResultDifference, Double.parseDouble(requirement.getThreshold()));
			}
			
			if(requirement.getIndicator().getScale() instanceof IntervalScale){
				IntervalScale scale = (IntervalScale) requirement.getIndicator().getScale();
				String rankingFunction = "";
				if(scale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
					rankingFunction = "Higher";
				if(scale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
					rankingFunction = "Lower";
				result = comparator.maxDistanceComparison(Double.parseDouble(value1.getValue()), 
						Double.parseDouble(value2.getValue()), rankingFunction, maxResultDifference, Double.parseDouble(requirement.getThreshold()));
				
			}
			
			
//			System.out.println("v1: " + value1.getValue());
//			System.out.println("v2: " + value2.getValue());
//			System.out.println("th: " + requirement.getThreshold());
//			System.out.println("diff: " + maxResultDifference);
//			System.out.println(requirement.getMeasure().getName());
//			System.out.println("Comparison " + result);
//			System.out.println("");
			
			if(result<0){
				System.err.println("MAX DISTANCE COMPARISON ERROR OR RESULT OF COMPARISON IS LESS THAN ZERO");
				throw new RuntimeException();
			}
			
			return result;
			
			
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
			
		
		return -1;
	}
	
	
	// compares the contribution of two criteria w.r.t an alternative
	public static double compareCharacteristics(Alternative alternative,
			Requirement requirement1, Requirement requirement2, DataService service) {
		
		QualityValue value1 = service.getQualityValueForAlternative(alternative, requirement1.getIndicator().getUri().toString());
		QualityValue value2 = service.getQualityValueForAlternative(alternative, requirement2.getIndicator().getUri().toString());
				
		return CharacteristicsComparison.compare(value1,requirement1,
				value2,requirement2);

	}
	

}
