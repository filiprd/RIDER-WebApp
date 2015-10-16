package eu.sealsproject.domain.oet.recommendation.util;

import java.util.Collection;
import java.util.LinkedList;

import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.RatioScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityCharacteristic;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityIndicator;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityMeasure;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.RankingFunction;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;

/**
 * 
 * @author Filip
 *
 */
public class RequirementsUtil {

	/**
	 * Constructs objects of the Requirement class, which contains Quality measures 
	 * that are used for measuring given Quality characteristics
	 *  
	 * @param characteristics 
	 * @return
	 */
	public static Collection<Requirement> getRequirementsForCharacteristics(Collection<QualityCharacteristic> characteristics){
		LinkedList<Requirement> requiremetns = new LinkedList<Requirement>();
		for (QualityCharacteristic qualityCharacteristic : characteristics) {
			for (QualityIndicator measure : qualityCharacteristic.getQualityIndicators()) {
				requiremetns.add(new Requirement(measure, null));
			}
		}
		return requiremetns;
	}
	
	
	public static Collection<Requirement> getRequirementsForMeasures(Collection<QualityIndicator> measures){
		LinkedList<Requirement> requiremetns = new LinkedList<Requirement>();
		for (QualityIndicator measure : measures) {
			requiremetns.add(new Requirement(measure, null));
		}
		return requiremetns;
	}
	
	
	/**
	 * Returns the requirement that is related to a given measure. If the measure is not in the requirements list
	 * a new requirement is created for that measure with the threshold equals to the best value, 
	 * so that a comparison can be performed.
	 * @param requirements
	 * @param measureUri
	 * @param service
	 * @return
	 */
	public static Requirement getRequirement(LinkedList<Requirement> requirements, String measureUri, DataService service){
		for (Requirement requirement : requirements) {
			if(requirement.getIndicator().getUri().toString().equals(measureUri))
				return requirement;
		}

		QualityIndicator indicator = service.getQualityIndicatorObject(measureUri);
		if(indicator.getScale().getClass().getSimpleName().equalsIgnoreCase("RatioScale")){
			RatioScale scale = (RatioScale) indicator.getScale();
			String threshold = "";
			if(scale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
				threshold = "1";
			if(scale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
				threshold = "0";
			return new Requirement(indicator,threshold);						
		}
		if(indicator.getScale().getClass().getSimpleName().equalsIgnoreCase("IntervalScale")){
			IntervalScale scale = (IntervalScale) indicator.getScale();
			String threshold = "";
			if(scale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
				threshold = String.valueOf(scale.getUpperBoundary());
			if(scale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
				threshold = String.valueOf(scale.getLowerBoundary());
			return new Requirement(indicator,threshold);								
		}
		return null;
	}
}
