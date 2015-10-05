package eu.sealsproject.domain.oet.recommendation.factories;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;

public class WeightedMatrixFactory {
	
//	public static DataService service = new DataService();

	public static Matrix getWeightedMatrix(Matrix supermatrix, Matrix clusterMatrix, DataService service){
		Matrix copy = supermatrix.copy();
		for (int i = 0; i < copy.getRowDimension(); i++) {
			for (int j = 0; j < copy.getColumnDimension(); j++) {
				double clusterValue = getClusterValue(clusterMatrix, copy.getMapping().getCharacteristicUri(i),
						copy.getMapping().getCharacteristicUri(j), service);
				copy.set(i, j, copy.get(i, j) * clusterValue);
			}
		}
		if(copy.isStohastic())
			return copy;
		System.err.println("Weighted matrix was not stochastic, columns have been normalized");
		return copy.normalizeColumns();
	}

	private static double getClusterValue(Matrix clusterMatrix, String measureUri1,
			String measureUri2, DataService service) {
		String characteristic1 = service.getCharacteristicUriOfIndicator(measureUri1);
		String characteristic2 = service.getCharacteristicUriOfIndicator(measureUri2);
		return clusterMatrix.get(clusterMatrix.getMapping().getRowNumber(characteristic1), 
				clusterMatrix.getMapping().getRowNumber(characteristic2));
	}
}
