package eu.sealsproject.domain.oet.recommendation.comparisons;



import java.util.Collection;

import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.AlternativeComparison;
import eu.sealsproject.domain.oet.recommendation.comparisons.interfaces.Satisfiable;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.OrdinalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.OrdinalScaleItem;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;

public class OrdinalScaleComparison implements  AlternativeComparison, Satisfiable {


	public double compare(QualityValue value1, QualityValue value2, Requirement requirement) {
		Collection<OrdinalScaleItem> values = (OrdinalScale.class.cast(requirement
				.getMeasure().getScale()).getOrdinalScaleItems());
		String result1 = (String)value1.getValue();
		String result2 = (String)value2.getValue();
		String threshold = requirement.getThreshold();
		int resultRanking1 = 0;
		int resultRanking2 = 0;
		int thresholdRanking = 0;
		
		for (OrdinalScaleItem ordinalScaleItem : values) {
			if(ordinalScaleItem.getName().equalsIgnoreCase(threshold))
				thresholdRanking = ordinalScaleItem.getRanking();
			if(ordinalScaleItem.getName().equalsIgnoreCase(result1))
				resultRanking1 = ordinalScaleItem.getRanking();
			if(ordinalScaleItem.getName().equalsIgnoreCase(result2))
				resultRanking2 = ordinalScaleItem.getRanking();
		}
		
		if(resultRanking1 <= thresholdRanking && thresholdRanking < resultRanking2)
			return 9;
		
		if(resultRanking2 <= thresholdRanking && thresholdRanking < resultRanking1)
			return 0.11;
		
		if(resultRanking1 > thresholdRanking && resultRanking2 > thresholdRanking && resultRanking1 > resultRanking2)
			return 0.2;
		if(resultRanking1 > thresholdRanking && resultRanking2 > thresholdRanking && resultRanking2 > resultRanking1)
			return 5;
		
		if(resultRanking1 < thresholdRanking && resultRanking2 < thresholdRanking && resultRanking1 > resultRanking2)
			return 0.2;
		if(resultRanking1 < thresholdRanking && resultRanking2 < thresholdRanking && resultRanking2 > resultRanking1)
			return 5;
		
		if(result1 == result2)
			return 1;
		
		return -1;
	}

	
	public boolean satisfiesRequirement(QualityValue value,
			Requirement requirement) {

		Collection<OrdinalScaleItem> values = (OrdinalScale.class.cast(requirement
				.getMeasure().getScale()).getOrdinalScaleItems());
		String result = (String)value.getValue();
		String threshold = requirement.getThreshold();
		int resultRanking = 0;
		int thresholdRanking = 0;
		
		for (OrdinalScaleItem ordinalScaleItem : values) {
			if(ordinalScaleItem.getName().equalsIgnoreCase(threshold))
				thresholdRanking = ordinalScaleItem.getRanking();
			if(ordinalScaleItem.getName().equalsIgnoreCase(result))
				resultRanking = ordinalScaleItem.getRanking();
		}
		
		if(resultRanking>thresholdRanking)
			return true;
		return false;
	}

}
