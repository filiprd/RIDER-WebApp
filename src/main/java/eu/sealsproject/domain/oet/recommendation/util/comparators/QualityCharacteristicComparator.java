package eu.sealsproject.domain.oet.recommendation.util.comparators;

import java.util.Comparator;

import com.ibm.icu.text.UTF16.StringComparator;

import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityCharacteristic;

public class QualityCharacteristicComparator implements Comparator<QualityCharacteristic>{

	
	public int compare(QualityCharacteristic ch1, QualityCharacteristic ch2) {
		
		String ch1Id = ch1.getUri().toString();
		String ch2Id = ch2.getUri().toString();
		
		return new StringComparator().compare(ch1Id, ch2Id);
		
	}

	
}
