package eu.sealsproject.domain.oet.recommendation.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.util.comparators.AlternativesComparator;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;

public class ResultsUtil {
	

	/**
	 * Sorts the alternatives w.r.t their recommendation result from the limitMatrtix (from best to worst)
	 * @param limitMatrix
	 * @param alternatives Alternatives to sort
	 * @return A new object, different than in the argument of the method
	 */
	public static LinkedList<Alternative> getRecommendationResults(
			Matrix limitMatrix, LinkedList<Alternative> alternatives) {
		MatrixMapping mapping = limitMatrix.getMapping();
		for (Alternative alternative : alternatives) {
			for (int i = 0; i < limitMatrix.getColumnDimension(); i++) {
				int rowNumber = mapping.getRowNumber(alternative.getId());
				if(limitMatrix.get(rowNumber,i) != 0){
					alternative.setResult(limitMatrix.get(mapping.getRowNumber(alternative.getId()),
							i));
					continue;
				}					
			}
		}
		Collections.sort((List<Alternative>) alternatives, new AlternativesComparator());
		return alternatives;
	}
}
