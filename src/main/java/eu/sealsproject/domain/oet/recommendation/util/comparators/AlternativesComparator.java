package eu.sealsproject.domain.oet.recommendation.util.comparators;

import java.util.Comparator;

import eu.sealsproject.domain.oet.recommendation.domain.Alternative;

public class AlternativesComparator implements Comparator<Alternative>{

	
	public int compare(Alternative a1, Alternative a2) {
		
		return Double.compare(a2.getResult(), a1.getResult());
	}

}
