package eu.sealsproject.domain.oet.recommendation.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.comparisons.CharacteristicsComparison;
import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.AlternativeComparison;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.RatioScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.RankingFunction;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;

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
	
	
	// compares alternatives w.r.t characteristic and returns weights
	public static Matrix compareAlternatives(Requirement requirement,
			LinkedList<Alternative> alternatives, String algorithm, DataService service) {
		
		int size = alternatives.size();
		Matrix comparison = new Matrix(size,size);
		
		MatrixMapping comparisonMatrixMapping = new MatrixMapping();
		
		for (int i = 0; i < size; i++) {
			comparisonMatrixMapping.addMapItem(new MapItem(i, alternatives.get(i).getId()));
			
			for (int j = i; j < size; j++) {
				if(i == j){
					comparison.set(i, j, 1);
					continue;
				}
				
				
				Method comparator;
				double result = -1;
				try {
					Class<?> clazz = Class.forName("eu.sealsproject.domain.oet.recommendation.services.ComparisonService");
					Object inst = clazz.newInstance();
					comparator = clazz.getMethod(algorithm + "Comparison", Alternative.class, Alternative.class, Requirement.class,
									DataService.class);
					Object obj = comparator.invoke(inst, alternatives.get(i), alternatives.get(j), requirement, service);
					result = Double.parseDouble(obj.toString());
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.getCause().printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				double result = ComparisonService.compareAlternativesMaxDistance(alternatives.get(i), 
//						alternatives.get(j),requirement, service);
				
				if(result == -1)
					throw new RuntimeException("There was an error in ivoking comparion algorithm "
							+ "when comparing " + alternatives.get(i).getId() + " to " + alternatives.get(j).getId() + 
							" with respect to " + requirement.getIndicator().getName());
				
				comparison.set(i, j, result);
				comparison.set(j, i, 1/result);

			}
		}
		
		

		Matrix weights = comparison.getWeights();
		weights.setMapping(comparisonMatrixMapping);
		weights.setId(requirement.getIndicator().getUri().toString());
		
		return weights;
	}
	

}
