package eu.sealsproject.domain.oet.recommendation.comparisons;


import java.text.NumberFormat;
import java.util.Locale;

import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.AlternativeComparison;
import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.Satisfiable;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.RankingFunction;

public class IntervalScaleComparison implements  AlternativeComparison, Satisfiable{

	NumberFormat format = NumberFormat.getInstance(Locale.UK);
	
	public IntervalScaleComparison(){		
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
	}
	
	/**
	 * Compares value1 to value2
	 */

	public double compare(QualityValue value1, QualityValue value2, Requirement requirement) {
		double result1 = Double.parseDouble(value1.getValue().toString().replace(",", ""));
		double result2 = Double.parseDouble(value2.getValue().toString().replace(",", ""));
		
		
		double threshold = Double.parseDouble(requirement.getThreshold());
		IntervalScale ratioScale = IntervalScale.class.cast(value1.getForMeasure().getScale());
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

	@Override
	public double maxDistanceComparison(Object result1, Object result2, String rankingFunction, double maxResultDifference, Object threshold) {
		double r1;
		double r2;
		double t;
		try {
			r1 = new Double(result1.toString());
			r2 = new Double(result2.toString());
			t = new Double(threshold.toString());
		} catch (Exception e) {
			throw new RuntimeException("Provided argument is not an instance of Double class");
		}
		
		if(r1 == r2)
			return 1;
		
		
		double threesholdComparison = compareWithThreshold(rankingFunction, r1, r2, t);
		if(threesholdComparison != -1)
			return threesholdComparison;
		
		return getComparison(r1, r2, maxResultDifference, rankingFunction,9);	
	}

	public boolean satisfiesRequirement(QualityValue value,
			Requirement requirement) {
		double result = Double.parseDouble(value.getValue().toString());
		double threshold = Double.parseDouble(requirement.getThreshold());
		
		IntervalScale ratioScale = IntervalScale.class.cast(value.getForMeasure().getScale());
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
	
	private double getComparison(double r1, double r2, double distance, String rankingFunction, int outputCardinality){
		if(rankingFunction.equals("Higher")){			
			if(r1 > r2){
				Double d = Math.rint(outputCardinality*(r1-r2)/distance + 0.5);	
				if(d>9)
					return 9;
				if(d<1)
					return 1;
				return d;
			}
			Double d = Math.rint(outputCardinality*(Math.abs(r1-r2))/distance + 0.5);
			if(d>9)
				d = 0.11;
			if(d<1)
				return 1;
			return Double.parseDouble(format.format(1/d));
		}
		
		
		if(rankingFunction.equals("Lower")){				
			if(r1-r2 > 0){
				Double d = Math.rint(outputCardinality*(r1-r2)/distance + 0.5);
				if(d>9)
					d = 0.11;
				if(d<1)
					return 1;
				return Double.parseDouble(format.format(1/d));
			}
			Double d = Math.rint(outputCardinality*Math.abs(r1-r2)/distance + 0.5);	
			if(d>9)
				return 9;
			if(d<1)
				return 1;
			return d;			
		}
		return -1;
	}
	
	private double compareWithThreshold(String rankingFunction, double r1, double r2, double t){
		if(rankingFunction.equals("Higher")){			
			if(r1 >= t && t > r2)
				return 9;			
			if(r2 >= t && t > r1)
				return 0.11;
			
		}
		
		if(rankingFunction.equals("Lower")){				
			if(r1 <= t && t < r2)
				return 9;			
			if(r2 <= t && t < r1)
				return 0.11;
			
		}
		
		return -1;
	}
}
