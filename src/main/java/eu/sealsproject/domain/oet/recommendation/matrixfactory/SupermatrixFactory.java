package eu.sealsproject.domain.oet.recommendation.matrixfactory;

import java.util.Collection;
import java.util.LinkedList;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolCategory;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityMeasure;
import eu.sealsproject.domain.oet.recommendation.services.AlternativesFactory;
import eu.sealsproject.domain.oet.recommendation.services.ExpertComparisonService;
import eu.sealsproject.domain.oet.recommendation.services.SupermatrixService;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;


/**
 * This class is used for loading the dependence matrix from file. It also provides a method for 
 * extracting the submatrix based on the requirements
 * @author Filip
 *
 */
public class SupermatrixFactory {
	
//	public DataService service = new DataService();
	
	Matrix clusterMatrix;
	
	LinkedList<Alternative> alternatives = new LinkedList<Alternative>();
	
	/**
	 * Creates a dependence martix. The dependence matrix is loaded from file.
	 * @return
	 */
	public Matrix create(){
		return Matrix.deserialize("matrices/SemTechSupermatrix");
	}
	
	/**
	 * Extracts a submatrix based on the requirements.
	 * The algorithm extracts characteristics from the requirements + the ones that influence them
	 * @param requirements
	 * @param compareOnlyRequirements Whether the comparisons of alternatives will be performed only w.rt.
	 * 		requirements, or w.r.t all criteria in the network
	 * @return
	 */
	public Matrix getSubmatrix(LinkedList<Requirement> requirements, DataService service, boolean 
			compareOnlyRequirements){
		MatrixMapping smMap = MatrixMapping.loadSupermatrixMap();
		Matrix supermatrixMatrix = create();
		
		//submatrix mapping
		MatrixMapping subMap = new MatrixMapping();
		int indexer=0;
		
		
		LinkedList<Integer> indexes = new LinkedList<Integer>();
				
		for (Requirement item : requirements) {
			int column = smMap.getRowNumber(item.getMeasure().getUri().toString());
//			System.out.println("Column "+column);
			if(!indexes.contains(column)){
				indexes.add(column);
				subMap.addMapItem(new MapItem(indexer++, smMap.getCharacteristicUri(column)));
			}
			
			// adds all characteristics that influence the one in the 'column' (requirement)
			for (int i = 0; i < supermatrixMatrix.getRowDimension(); i++) {
				if(supermatrixMatrix.get(i, column) != 0){
//					System.out.println("Row "+i);
					if(!indexes.contains(i)){
						indexes.add(i);
						subMap.addMapItem(new MapItem(indexer++, smMap.getCharacteristicUri(i)));
					}
				}					
			}
		}
		
		int[] in = getIndexes(indexes);

		// creates submatrix 
		Matrix subSupermatrix = supermatrixMatrix.getMatrix(in, in);
		subSupermatrix.setId("http://www.seals-project.eu/supermatrix");
		subSupermatrix.setMapping(subMap);
		

		// normalizes entries in the supermatrix according to clusters, so that in every cluster 
		// in a row the sum is one (has to be because of the comparisons)
		for (int i = 0; i < in.length; i++) {
			LinkedList<String> checked = new LinkedList<String>();
			for (int j = 0; j < in.length; j++) {
				LinkedList<Integer> clusterIndexes = new LinkedList<Integer>();
				clusterIndexes.add(j);
				String cluster =  service.getCharacteristicUriOfMeasure(
						subSupermatrix.getMapping().getCharacteristicUri(j));
				if(checked.contains(cluster))
					continue;
				checked.add(cluster);
				for (int k = j+1; k < in.length; k++) {
					if (cluster.equalsIgnoreCase(service.getCharacteristicUriOfMeasure(
						subSupermatrix.getMapping().getCharacteristicUri(k))))
						clusterIndexes.add(k);
				}
					int[] positions = getIndexes(clusterIndexes);
					Matrix c = subSupermatrix.getMatrix(positions, i, i);
					subSupermatrix.setMatrixColumn(positions, i, c.normalizeColumns());
			}
		}

		
//		return subSupermatrix;
		this.alternatives = AlternativesFactory.createAlternativesList(requirements, service);
//		System.out.println("ALTERNATIVES LIST: " + this.alternatives.size());
		Matrix supermatrix = SupermatrixService.fillSupermatrixWithAlternatives(subSupermatrix,requirements,this.alternatives, service,compareOnlyRequirements);
		initializeClusterMatrix(requirements, supermatrix, service,compareOnlyRequirements);
		return supermatrix;
	}

	/**
	 * Converts a list of integers into the array of integers
	 * @param clusterIndexes
	 * @return
	 */
	private int[] getIndexes(LinkedList<Integer> clusterIndexes) {
		int[] in = new int[clusterIndexes.size()];
		int dex = 0;		
		for (int i : clusterIndexes) {
			in[dex++] = i;
		}
		return in;
	}

	public LinkedList<Alternative> getAlternatives() {
		return alternatives;
	}
	
	
	/**
	 * Extracts a submatrix based on the requirements, which only contains characteristics from the requirements
	 * @param requirements
	 * @param service
	 * @return
	 */
	public Matrix getSubmatrixOnlyRequirements(LinkedList<Requirement> requirements, DataService service, boolean 
			compareOnlyRequirements){
		
		MatrixMapping smMap = MatrixMapping.loadSupermatrixMap();
		Matrix supermatrixMatrix = create();
		
		MatrixMapping subMap = new MatrixMapping();
		int indexer=0;
		
		
		LinkedList<Integer> indexes = new LinkedList<Integer>();
				
		for (Requirement item : requirements) {
			int column = smMap.getRowNumber(item.getMeasure().getUri().toString());
//			System.out.println("Column "+column);
			if(!indexes.contains(column)){
				indexes.add(column);
				subMap.addMapItem(new MapItem(indexer++, smMap.getCharacteristicUri(column)));
			}
		}
		
		int[] in = getIndexes(indexes);


		// creates submatrix 
		Matrix subSupermatrix = supermatrixMatrix.getMatrix(in, in);
		subSupermatrix.setId("http://www.seals-project.eu/supermatrix");
		subSupermatrix.setMapping(subMap);
		

		for (int i = 0; i < in.length; i++) {
			LinkedList<String> checked = new LinkedList<String>();
			for (int j = 0; j < in.length; j++) {
				LinkedList<Integer> clusterIndexes = new LinkedList<Integer>();
				clusterIndexes.add(j);
				String cluster =  service.getCharacteristicUriOfMeasure(
						subSupermatrix.getMapping().getCharacteristicUri(j));
				if(checked.contains(cluster))
					continue;
				checked.add(cluster);
				for (int k = j+1; k < in.length; k++) {
					if (cluster.equalsIgnoreCase(service.getCharacteristicUriOfMeasure(
							subSupermatrix.getMapping().getCharacteristicUri(k))))
						clusterIndexes.add(k);
				}
					int[] positions = getIndexes(clusterIndexes);
					Matrix c = subSupermatrix.getMatrix(positions, i, i);
					subSupermatrix.setMatrixColumn(positions, i, c.normalizeColumns());
			}
		}
		
		this.alternatives = AlternativesFactory.createAlternativesList(requirements, service);
		Matrix supermatrix = SupermatrixService.fillSupermatrixWithAlternatives(subSupermatrix,requirements,this.alternatives, service,compareOnlyRequirements);
		initializeClusterMatrix(requirements, supermatrix, service,compareOnlyRequirements);
		return supermatrix;

	}
	
	
	/**
	 * Extracts a submatrix based on the requirements, which includes all measures 
	 * for a certain type of tool that is going to be recommended
	 * @param requirements
	 * @param service
	 * @return
	 */
	public Matrix getSubmatrixAllMeasures(LinkedList<Requirement> requirements, DataService service, boolean 
			compareOnlyRequirements){
		
		MatrixMapping smMap = MatrixMapping.loadSupermatrixMap();
		Matrix supermatrixMatrix = create();
		
		MatrixMapping subMap = new MatrixMapping();
		int indexer=0;
		
		
		LinkedList<Integer> indexes = new LinkedList<Integer>();
				
		for (Requirement item : requirements) {
			int column = smMap.getRowNumber(item.getMeasure().getUri().toString());
			if(!indexes.contains(column)){
				indexes.add(column);
				subMap.addMapItem(new MapItem(indexer++, smMap.getCharacteristicUri(column)));
			}
			
			Collection<String> measures = service.getQualityMeasureUrisForToolType(
					service.getToolCategory(item.getMeasure().getUri().toString()).getUri().toString());
			
			
			// adds all measures that belong to a tool type which has the measure from the requirement
			for (int i = 0; i < supermatrixMatrix.getRowDimension(); i++) {
				if(!indexes.contains(i)){
					if(measures.contains(smMap.getCharacteristicUri(i))){
						indexes.add(i);
						subMap.addMapItem(new MapItem(indexer++, smMap.getCharacteristicUri(i)));
					}
				}				
			}
		}
				
		
		int[] in = getIndexes(indexes);


		// creates submatrix 
		Matrix subSupermatrix = supermatrixMatrix.getMatrix(in, in);
		subSupermatrix.setId("http://www.seals-project.eu/supermatrix");
		subSupermatrix.setMapping(subMap);
		

		for (int i = 0; i < in.length; i++) {
			LinkedList<String> checked = new LinkedList<String>();
			for (int j = 0; j < in.length; j++) {
				LinkedList<Integer> clusterIndexes = new LinkedList<Integer>();
				clusterIndexes.add(j);
				String cluster =  service.getCharacteristicUriOfMeasure(
						subSupermatrix.getMapping().getCharacteristicUri(j));
				if(checked.contains(cluster))
					continue;
				checked.add(cluster);
				for (int k = j+1; k < in.length; k++) {
					if (cluster.equalsIgnoreCase(service.getCharacteristicUriOfMeasure(
							subSupermatrix.getMapping().getCharacteristicUri(k))))
						clusterIndexes.add(k);
				}
					int[] positions = getIndexes(clusterIndexes);
					Matrix c = subSupermatrix.getMatrix(positions, i, i);
					subSupermatrix.setMatrixColumn(positions, i, c.normalizeColumns());
			}
		}
		
		this.alternatives = AlternativesFactory.createAlternativesList(requirements, service);
		Matrix supermatrix = SupermatrixService.fillSupermatrixWithAlternatives(subSupermatrix,requirements,this.alternatives, service,compareOnlyRequirements);
		initializeClusterMatrix(requirements, supermatrix, service,compareOnlyRequirements);
		return supermatrix;
	}

	
	// method made for testing matrix transformations
	public Matrix getClusterMatrix(){
			return clusterMatrix;
	}
	
	
	private void initializeClusterMatrix(LinkedList<Requirement> requirements, Matrix 
			supermatrix, DataService service, boolean compareOnlyRequirements){

		
		ExpertComparisonService expertClusterComparisons = new ExpertComparisonService();
		
		// Uris of characteristics related to measures from supermatrix
		LinkedList<String> characteristicsUris = 
			SupermatrixService.getCharacteristicsUris(supermatrix.getMapping(), service);
		characteristicsUris.add("Alternatives");
		
		// Uris characteristics related to measures from requirements
		LinkedList<String> requirementsCharacteristicsUris = 
			SupermatrixService.getRequirementCharacteristicsUris(requirements);
		
		//create cluster mapping
		MatrixMapping clusterMapping = new MatrixMapping();
		clusterMapping.setId("clusterMatrixMapping");
		int k = 0;
		for (String characteristicUri : characteristicsUris) {			
			clusterMapping.addMapItem(new MapItem(k++, characteristicUri));
		}
		k--;
		
		// creates a cluster matrix object
		Matrix clusterMatrix = new Matrix(k+1, k+1);
		clusterMatrix.setMapping(clusterMapping);
		
		// fills the cluster matrix with comparisons
		for (int i = 0; i < clusterMatrix.getColumnDimension(); i++) {
			expertClusterComparisons.setClusterComparison(clusterMatrix, characteristicsUris, 
					clusterMapping.getCharacteristicUri(i),
					compareOnlyRequirements, requirementsCharacteristicsUris,
					supermatrix, service);
		}
	

		this.clusterMatrix = clusterMatrix;
		
		
		//-------------------------------------------------------------------------
		//				OLD METHOD
		//-------------------------------------------------------------------------
//		MatrixMapping supermatrixMapping = supermatrix.getMapping();
//		
//		LinkedList<String> characteristicsUris = 
//			SupermatrixService.getCharacteristicsUris(supermatrixMapping, service);
//		
//		LinkedList<String> requirementsCharacteristicsUris = 
//			SupermatrixService.getRequirementCharacteristicsUris(requirements);
//
//		
//		MatrixMapping cmap = MatrixMapping.loadClusterMatrixMap();
//		Matrix clusterMatrix = Matrix.deserialize("matrices/ClusterMatrix");;
//		
//		//mapping for the subcluster matrix
//		MatrixMapping submatrixMapping = new MatrixMapping();
//		int k = 0;
//		
//		//list of matrix indexes to extract
//		LinkedList<Integer> indexes = new LinkedList<Integer>();
//				
//		//extracting every column that is in the requirements
//		for (String item : characteristicsUris) {
//			int column = cmap.getRowNumber(item);
////			System.out.println("Column "+column + "; ch: " + cmap.getCharacteristicUri(column));	
//			if(!indexes.contains(column)){
//				indexes.add(column);
//				submatrixMapping.addMapItem(new MapItem(k++, item));
//			}
//		}
//		
//		//adds the last column which refer to the alternatives
//		indexes.add(clusterMatrix.getColumnDimension()-1);
//		submatrixMapping.addMapItem(new MapItem(k, "Alternatives"));
//		
//		submatrixMapping.setId("clusterMatrix");
//		
//		//rearranging java objects
//		int[] in = new int[indexes.size()];
//		int dex = 0;		
////		Collections.sort(indexes);		
//		for (int i : indexes) {
////			System.out.println("Adding " + i);
//			in[dex++] = i;
//		}
//
//		//creating subcluster matrix
//		Matrix subCluster = clusterMatrix.getMatrix(in, in);
//		subCluster.setId("http://www.seals-project.eu/clusterMatrix");
//		subCluster.setMapping(submatrixMapping);
//		
//
//		if(compareOnlyRequirements){
//			for (int i = 0; i < subCluster.getRowDimension()-1; i++) {
//				if(!requirementsCharacteristicsUris.contains(submatrixMapping.getCharacteristicUri(i))){
//					subCluster.set(k, i, 0);
//					subCluster.set(i, k, 0);
//				}				
//			}
//		}
//		subCluster.set(k, k, 0);
//		
//		
//		// make zeroes in the elements that, because of the selection of requirements, do not depend
//		// e.g. I/E errors in the example => in the cluster, 
//		// 			OntProcRobust should have 0 w.r.t. OntProcRobust 
//		// UPDATE: Previous is not valid, because dependencies between all elements are taken into account
//		//	although alternatives are not compared w.r.t. all of criteria
//		// UPDATE 2: zeroes should be entered because, within a certain cluster, selected requirements
//		//	can be independent, which would mean that this cluster is not dependent on itself.
//		
//		for (int i = 0; i < subCluster.getRowDimension()-1; i++) {
//			for (int j = 0; j < subCluster.getColumnDimension()-1; j++) {
//				if(subCluster.get(i, j) == 0)
//					continue;
//				if(!existDependence(supermatrix, submatrixMapping.getCharacteristicUri(i),
//						submatrixMapping.getCharacteristicUri(j), service))
//					subCluster.set(i, j, 0);
//			}
//		}
//		
//		this.clusterMatrix = subCluster.normalizeColumns();
	}

	
}
