package eu.sealsproject.domain.oet.recommendation.matrixfactory;

import java.util.Collections;
import java.util.LinkedList;


import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityCharacteristic;
import eu.sealsproject.domain.oet.recommendation.services.SupermatrixService;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;


/**
 * This class is used for loading the cluster matrix from file
 * @author Filip
 *
 */
public class ClusterMatrixFactory {

	/**
	 * Creates a cluster matrix. The cluster matrix is loaded form file
	 * @return
	 */
	public static Matrix create(){
		return Matrix.deserialize("matrices/ClusterMatrix");
	}
	
	/**
	 * Extracts the submatrix of the cluster matrix that contains only requirements related 
	 * characteristics
	 * @param characteristicsUris - Uris of all characteristics to include
	 * @param requirementsCharacteristicsUris - Uris of only characteristics that are in the requirements
	 * @return
	 */
	public static Matrix getSubmatrix(LinkedList<Requirement> requirements, MatrixMapping 
			supermatrixMapping, DataService service){
				
		LinkedList<String> characteristicsUris = 
			SupermatrixService.getCharacteristicsUris(supermatrixMapping, service);
		
		LinkedList<String> requirementsCharacteristicsUris = 
			SupermatrixService.getRequirementCharacteristicsUris(requirements);

		
		MatrixMapping cmap = MatrixMapping.loadClusterMatrixMap();
		Matrix clusterMatrix = create();
		
		//mapping for the subcluster matrix
		MatrixMapping submatrixMapping = new MatrixMapping();
		int k = 0;
		
		//list of matrix indexes to extract
		LinkedList<Integer> indexes = new LinkedList<Integer>();
				
		//extracting every column that is related to a given supermatrix
		for (String item : characteristicsUris) {
			int column = cmap.getRowNumber(item);
//			System.out.println("Column "+column + "; ch: " + cmap.getCharacteristicUri(column));	
			if(!indexes.contains(column)){
				indexes.add(column);
				submatrixMapping.addMapItem(new MapItem(k++, item));
			}
		}
		
		//adds the last column which refer to the alternatives
		indexes.add(clusterMatrix.getColumnDimension()-1);
		submatrixMapping.addMapItem(new MapItem(k, "Alternatives"));
		
		submatrixMapping.setId("clusterMatrix");
		
		//rearranging java objects
		int[] in = new int[indexes.size()];
		int dex = 0;		
//		Collections.sort(indexes);		
		for (int i : indexes) {
//			System.out.println("Adding " + i);
			in[dex++] = i;
		}

		//creating subcluster matrix
		Matrix subCluster = clusterMatrix.getMatrix(in, in);
		subCluster.setId("http://www.seals-project.eu/clusterMatrix");
		subCluster.setMapping(submatrixMapping);
		

		for (int i = 0; i < subCluster.getRowDimension()-1; i++) {
			if(!requirementsCharacteristicsUris.contains(submatrixMapping.getCharacteristicUri(i))){
				subCluster.set(k, i, 0);
				subCluster.set(i, k, 0);
			}				
		}
		subCluster.set(k, k, 0);
		
		// TODO
		// make zeroes in the elements that, because of the selection of requirements, do not depend
		// e.g. I/E errors in the example => in the cluster, 
		// 			OntProcRobust should have 0 w.r.t. OntProcRobust 
		
//		return subCluster;
		return subCluster.normalizeColumns();
	}
	
	
}
