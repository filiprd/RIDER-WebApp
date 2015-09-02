package eu.sealsproject.domain.oet.recommendation.util;

import java.text.DecimalFormat;
import java.util.LinkedList;

import com.ibm.icu.text.UTF16.StringComparator;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;

public class Sorter {

	public static void sort(LinkedList<Requirement> requirements,
			Matrix limitSupermatrix) {
		
		StringComparator comparator = new StringComparator();
		for (int i = 0; i < requirements.size()-1; i++) {
			for (int j = i+1; j < requirements.size(); j++) {
				if(comparator.compare(getImportance(requirements.get(i).getMeasure().getUri().toString(),limitSupermatrix),
						getImportance(requirements.get(j).getMeasure().getUri().toString(),limitSupermatrix)) < 0){
					Requirement temp = requirements.get(i);
					requirements.set(i, requirements.get(j));
					requirements.set(j, temp);
				}					
			}
		}
		
		
		
	}
	
	private static String getImportance(String measureUri, Matrix limitSupermatrix){
		for (MapItem item : limitSupermatrix.getMapping().getMap()) {
			if (item.getChracteristicUri().equals(measureUri)){
				DecimalFormat format = new DecimalFormat();
				format.setMinimumIntegerDigits(1);
				format.setMaximumFractionDigits(3);
				format.setMinimumFractionDigits(3);
				return format.format(limitSupermatrix.get(item.getRowNumber(), 0));
			}
		}
		return "";
	}

	
}
