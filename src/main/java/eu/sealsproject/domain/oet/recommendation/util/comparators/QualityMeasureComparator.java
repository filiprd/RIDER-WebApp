package eu.sealsproject.domain.oet.recommendation.util.comparators;

import java.util.Comparator;

import com.ibm.icu.text.UTF16.StringComparator;

import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityMeasure;

public class QualityMeasureComparator implements Comparator<QualityMeasure>{

	
	public int compare(QualityMeasure m1, QualityMeasure m2) {
		
		String m1Id = m1.getUri().toString();
		String m2Id = m2.getUri().toString();
		
		return new StringComparator().compare(m1Id, m2Id);
	}

}
