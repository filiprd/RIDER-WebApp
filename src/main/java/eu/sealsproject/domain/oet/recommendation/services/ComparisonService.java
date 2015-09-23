package eu.sealsproject.domain.oet.recommendation.services;

import eu.sealsproject.domain.oet.recommendation.comparisons.CharacteristicsComparison;
import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.AlternativeComparison;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolVersion;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.RankingFunction;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.RatioScale;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;

public class ComparisonService {
	
//	public static DataService service = new DataService();

	// compares two alternatives w.r.t a criteria
	public static double simpleComparison(Alternative alternative1, Alternative alternative2,
			Requirement requirement, DataService service) {
		
		QualityValue value1 = alternative1.getQualityValue(requirement.getMeasure().getUri().toString());
		QualityValue value2 = alternative2.getQualityValue(requirement.getMeasure().getUri().toString());
		
//		for (ToolVersion tool : alternative1.getTools()) {
//			if(service.coversQualityMeasure(tool.getUri().toString(),
//					requirement.getMeasure().getUri().toString()))
//				value1 = service.getQualityValue(tool.getUri().toString(),
//						requirement.getMeasure().getUri().toString());
//		}
//		
//		for (ToolVersion tool : alternative2.getTools()) {
//			if(service.coversQualityMeasure(tool.getUri().toString(),
//					requirement.getMeasure().getUri().toString()))
//				value2 = service.getQualityValue(tool.getUri().toString(),
//						requirement.getMeasure().getUri().toString());
//		}
		
		try {
			
			AlternativeComparison comparator = 	(AlternativeComparison)
				Class.forName("eu.sealsproject.domain.oet.recommendation.comparisons." + requirement.getMeasure().getScale().getClass().getSimpleName()
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
		
		QualityValue value1 = alternative1.getQualityValue(requirement.getMeasure().getUri().toString());
		QualityValue value2 = alternative2.getQualityValue(requirement.getMeasure().getUri().toString());

		
		try {
			
			AlternativeComparison comparator = 	(AlternativeComparison)
				Class.forName("eu.sealsproject.domain.oet.recommendation.comparisons." + requirement.getMeasure().getScale().getClass().getSimpleName()
						+ "Comparison").newInstance();
			
			
			double maxResultDifference = service.getResultsDifference(requirement.getMeasure().getUri().toString());
			
			double result = -1;
			if(requirement.getMeasure().getScale() instanceof RatioScale){
				RatioScale scale = (RatioScale) requirement.getMeasure().getScale();
				String rankingFunction = "";
				if(scale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
					rankingFunction = "Higher";
				if(scale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
					rankingFunction = "Lower";
				result = comparator.maxDistanceComparison(Double.parseDouble(value1.getValue()), 
						Double.parseDouble(value2.getValue()), rankingFunction, maxResultDifference, Double.parseDouble(requirement.getThreshold()));
			}
			
			if(requirement.getMeasure().getScale() instanceof IntervalScale){
				IntervalScale scale = (IntervalScale) requirement.getMeasure().getScale();
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
			Requirement requirement1, Requirement requirement2) {
		
		QualityValue value1 = alternative.getQualityValue(requirement1.getMeasure().getUri().toString());
		QualityValue value2 = alternative.getQualityValue(requirement2.getMeasure().getUri().toString());
		
//		for (ToolVersion tool : alternative.getTools()) {
//			if(service.coversQualityMeasure(tool.getUri().toString(),
//					requirement1.getMeasure().getUri().toString()))
//				value1 = service.getQualityValue(tool.getUri().toString(),
//						requirement1.getMeasure().getUri().toString());
//			if(service.coversQualityMeasure(tool.getUri().toString(),
//					requirement2.getMeasure().getUri().toString()))
//				value2 = service.getQualityValue(tool.getUri().toString(),
//						requirement2.getMeasure().getUri().toString());
//		}
				
		return CharacteristicsComparison.compare(value1,requirement1,
				value2,requirement2);

	}
	

}
