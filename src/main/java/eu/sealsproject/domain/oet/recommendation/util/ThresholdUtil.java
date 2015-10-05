package eu.sealsproject.domain.oet.recommendation.util;

import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.MeasurementScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.NominalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.OrdinalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.RatioScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.OrdinalScaleItem;

public class ThresholdUtil {

	
	/**
	 * Checks if the quality value satisfies the threshold
	 * @param value
	 * @param threshold
	 * @return
	 */
	public static boolean satisfiesThreshold(QualityValue value, String threshold){
		MeasurementScale scale = value.getForMeasure().getScale();
		
		if(scale.getClass().getSimpleName().equalsIgnoreCase("RatioScale")){
			RatioScale ratio = (RatioScale)scale;
			double r = Double.parseDouble(value.getValue());
			double t = Double.parseDouble(threshold);
			if(ratio.getRankingFunction().getUri().toString().equals(Constants.QMO_NS + "HigherBest")){
				if(r >= t)
					return true;
				else
					return false;
			}
				
				
			if(ratio.getRankingFunction().getUri().toString().equals(Constants.QMO_NS + "LowerBest")){
				if(r <= t)
					return true;
				else
					return false;
			}
		}
		
		if(scale.getClass().getSimpleName().equalsIgnoreCase("IntervalScale")){
			IntervalScale interval = (IntervalScale)scale;
			double r = Double.parseDouble(value.getValue().replace(",", ""));
			double t = Double.parseDouble(threshold);
			if(interval.getRankingFunction().getUri().toString().equals(Constants.QMO_NS + "HigherBest")){
				if(r >= t)
					return true;
				else
					return false;
			}
				
				
			if(interval.getRankingFunction().getUri().toString().equals(Constants.QMO_NS + "LowerBest")){
				if(r <= t)
					return true;
				else
					return false;
			}
		}
		
		if(scale.getClass().getSimpleName().equalsIgnoreCase("NominalScale")){
			NominalScale nominal = (NominalScale)scale;
			if(value.getValue().equals(threshold))			
				return true;
			else
				return false;
		}
		
		if(scale.getClass().getSimpleName().equalsIgnoreCase("OrdinalScale")){
			OrdinalScale ordinal = (OrdinalScale)scale;
			int r = 10;
			int t = 0;
			for (OrdinalScaleItem scaleItem : ordinal.getOrdinalScaleItems()) {
				if(scaleItem.getLabel().equals(value.getValue()))
					r = scaleItem.getOrder();
				if(scaleItem.getLabel().equals(threshold))
					t = scaleItem.getOrder();
			}
			if(r<=t)			
				return true;
			else
				return false;			
		}
		return false;
	}
}
