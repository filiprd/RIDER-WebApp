package eu.sealsproject.domain.oet.recommendation.util;

import java.util.Collection;
import java.util.LinkedList;

import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityCharacteristic;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityMeasure;

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
			for (QualityMeasure measure : qualityCharacteristic.getQualityMeasures()) {
				requiremetns.add(new Requirement(measure, null));
			}
		}
		return requiremetns;
	}
	
	public static Collection<Requirement> getRequirementsForMeasures(Collection<QualityMeasure> measures){
		LinkedList<Requirement> requiremetns = new LinkedList<Requirement>();
		for (QualityMeasure measure : measures) {
			requiremetns.add(new Requirement(measure, null));
		}
		return requiremetns;
	}
}
