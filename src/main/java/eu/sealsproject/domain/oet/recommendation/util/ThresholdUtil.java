package eu.sealsproject.domain.oet.recommendation.util;

import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.NominalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.OrdinalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.OrdinalScaleItem;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.RatioScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.Scale;

public class ThresholdUtil {

	
	/**
	 * Checks if the quality value satisfies the threshold
	 * @param value
	 * @param threshold
	 * @return
	 */
	public static boolean satisfiesThreshold(QualityValue value, String threshold){
		Scale scale = value.getForMeasure().getScale();
		
		if(scale.getClass().getSimpleName().equalsIgnoreCase("RatioScale")){
			RatioScale ratio = (RatioScale)scale;
			double r = Double.parseDouble(value.getValue());
			double t = Double.parseDouble(threshold);
			if(ratio.getRankingFunction().getUri().toString().equals("http://www.seals-project.eu/ontologies/QualityModel.owl#HigherBest")){
				if(r >= t)
					return true;
				else
					return false;
			}
				
				
			if(ratio.getRankingFunction().getUri().toString().equals("http://www.seals-project.eu/ontologies/QualityModel.owl#LowerBest")){
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
			if(interval.getRankingFunction().getUri().toString().equals("http://www.seals-project.eu/ontologies/QualityModel.owl#HigherBest")){
				if(r >= t)
					return true;
				else
					return false;
			}
				
				
			if(interval.getRankingFunction().getUri().toString().equals("http://www.seals-project.eu/ontologies/QualityModel.owl#LowerBest")){
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
				if(scaleItem.getName().equals(value.getValue()))
					r = scaleItem.getRanking();
				if(scaleItem.getName().equals(threshold))
					t = scaleItem.getRanking();
			}
			if(r<=t)			
				return true;
			else
				return false;			
		}
		return false;
	}
}
