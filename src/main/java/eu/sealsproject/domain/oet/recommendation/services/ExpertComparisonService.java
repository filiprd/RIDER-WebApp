package eu.sealsproject.domain.oet.recommendation.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.dependencies.QualityMeasureDependencies;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;

public class ExpertComparisonService {

	private LinkedList<Matrix> clusterComparisons = new LinkedList<Matrix>();
//	private LinkedList<Matrix> supermatrixComparisons = new LinkedList<Matrix>();
	
	public ExpertComparisonService(){
	}
	
	
	public ExpertComparisonService(LinkedList<String> characteristicUris){
		clusterComparisons = loadClusterComparisons(characteristicUris);
	}

	
	public void setClusterComparison(Matrix clusterMatrix, LinkedList<String> characteristicsUri, String controlCharacteristic,
			boolean compareOnlyRequirements, LinkedList<String> requirementsCharacteristicsUris,
			Matrix supermatrix, DataService service){
		
		// Extracts the comparison matrix related to a given control criterion
		Matrix comparisonMatrix = null;
		for (Matrix clusterComparsion : clusterComparisons) {
			if(clusterComparsion.getId().equals(controlCharacteristic)){
				comparisonMatrix = clusterComparsion;
				break;
			}			
		}
		
		// If there is no comparison matrix for control criterion then this criterion does not depend on any other
		// Set 1 on alternatives row if needed
		if(comparisonMatrix==null){
			if(compareOnlyRequirements && requirementsCharacteristicsUris.contains(controlCharacteristic))
				clusterMatrix.set(clusterMatrix.getRowDimension()-1,
						clusterMatrix.getMapping().getRowNumber(controlCharacteristic),1);
			if(!compareOnlyRequirements)
				clusterMatrix.set(clusterMatrix.getRowDimension()-1,
						clusterMatrix.getMapping().getRowNumber(controlCharacteristic),1);
			return;
		}
			
		

		// extracts only those comparisons relevant to a given list of characteristics
		MatrixMapping weightsMapping = new MatrixMapping();
		LinkedList<Integer> indexes = new LinkedList<Integer>();
		int k = 0;
		for (String characteristicUri : characteristicsUri) {
			int column = comparisonMatrix.getMapping().getRowNumber(characteristicUri);				
			if(!indexes.contains(column) && column != -1 && existDependence(supermatrix, characteristicUri, controlCharacteristic, service)){
				if(!controlCharacteristic.equals("Alternatives")){
					if(compareOnlyRequirements && characteristicUri.equals("Alternatives") && 
							!requirementsCharacteristicsUris.contains(controlCharacteristic))
						continue;
					indexes.add(column);
					weightsMapping.addMapItem(new MapItem(k++, characteristicUri));
				}	
				else{
					if(compareOnlyRequirements && !requirementsCharacteristicsUris.contains(characteristicUri))
						continue;
					indexes.add(column);
					weightsMapping.addMapItem(new MapItem(k++, characteristicUri));
				}
			}
		}
		
				
		
		//rearranging java objects
		int[] in = new int[indexes.size()];
		int dex = 0;			
		for (int i : indexes) {
			in[dex++] = i;
		}
				
		Matrix weights = comparisonMatrix.getMatrix(in, in).getWeights();

		
		//put weights in appropriate positions in cluster
		int columnInCluster = clusterMatrix.getMapping().getRowNumber(controlCharacteristic);
		for (int i = 0; i < weights.getRowDimension(); i++) {	
			int rowInCluster = clusterMatrix.getMapping().getRowNumber(weightsMapping.getCharacteristicUri(i));
			clusterMatrix.set(rowInCluster, columnInCluster, weights.get(i, 0));
		}	
	}
	
	
	
	/**
	 * For a given characteristics from a cluster matrix, checks if there are elements in a given supermatrix
	 * which are dependent on each other
	 * @param supermatrix
	 * @param rowCharacteristic
	 * @param columnCharacteristic
	 * @param service
	 * @return <i>True</i> if dependence exist, <i>false</i> otherwise
	 */
	private boolean existDependence(Matrix supermatrix, String rowCharacteristic, String 
			columnCharacteristic, DataService service){
		for (int i = 0; i < supermatrix.getRowDimension(); i++) {
			String rowMeasureUri = supermatrix.getMapping().getCharacteristicUri(i);
			if(service.getCharacteristicUriOfMeasure(rowMeasureUri).equals(rowCharacteristic)){
				for (int j = 0; j < supermatrix.getColumnDimension(); j++) {
					String columnMeasureUri = supermatrix.getMapping().getCharacteristicUri(j);
					if(service.getCharacteristicUriOfMeasure(columnMeasureUri).equals(columnCharacteristic)){
						if(supermatrix.get(i, j) != 0)
							return true;
					}
				}
			}
			
		}
		return false;
	}
	
	
	public void setSupermatrixComparison(Matrix supermatrix, String controlCriterion,
			LinkedList<Matrix> supermatrixComparisons, LinkedList<QualityMeasureDependencies> dependencies){
		
		MatrixMapping supermatrixMapping = supermatrix.getMapping();		
		for (Matrix comparisonMatrix : supermatrixComparisons) {			
			if(comparisonMatrix.getId().equals(controlCriterion)){
				int k = 0;
				LinkedList<Integer> indexes = new LinkedList<Integer>();
				MatrixMapping comparisonMatrixMapping = comparisonMatrix.getMapping();
				LinkedList<MapItem> weightsMappingItems = new LinkedList<MapItem>();
				for (int i = 0; i < comparisonMatrix.getRowDimension(); i++) {
					if(isCriterionPresent(supermatrixMapping, comparisonMatrixMapping.getCharacteristicUri(i))
							&& !indexes.contains(i)){
						indexes.add(i);
						weightsMappingItems.add(new MapItem(k++, comparisonMatrixMapping.getCharacteristicUri(i)));
					}
				}
				if(indexes.size() > 1){
					int columnInSupermatrix = supermatrixMapping.getRowNumber(controlCriterion);
					MatrixMapping weightsMapping = new MatrixMapping();
					weightsMapping.setId(controlCriterion);
					weightsMapping.setMap(weightsMappingItems);
					int[] in = getIndexes(indexes);
					Matrix subComparison = new Matrix(comparisonMatrix.getMatrix(in,in).getArray());
					subComparison.setId(controlCriterion);
					subComparison.setMapping(weightsMapping); 
					
					Matrix weights = subComparison.getWeights();
					for (int i = 0; i < weights.getRowDimension(); i++) {
						int rowInSupermatrix = supermatrix.getMapping().getRowNumber(
								weightsMapping.getCharacteristicUri(i));
						supermatrix.set(rowInSupermatrix, columnInSupermatrix, weights.get(i, 0));
					}
				}
				if(indexes.size() == 1)
					supermatrix.set(indexes.get(0), supermatrixMapping.getRowNumber(controlCriterion), 1);
			}
		}
		
		// fills those entries that are related to a dependence of the control criterion on only one element in a cluster
		for (int i = 0; i < supermatrix.getRowDimension(); i++) {
			if(supermatrix.get(i, supermatrixMapping.getRowNumber(controlCriterion)) == 0 &&
					isDependent(controlCriterion, supermatrixMapping.getCharacteristicUri(i), dependencies))
				supermatrix.set(i, supermatrixMapping.getRowNumber(controlCriterion), 1);
		}
		
	}
	
	private boolean isCriterionPresent(MatrixMapping supermatrixMapping,
			String criterionId) {
		List<MapItem> mappingItems = supermatrixMapping.getMap();
		for (MapItem mappingItem : mappingItems) {
			if(mappingItem.getChracteristicUri().equals(criterionId))
				return true;
		}
		return false;
	}
	
	
	/**
	 * Checks if the one supermatrix criterion (the first argument) depends on the other supermatrix criterion (the second argument)
	 * @return
	 */
	private boolean isDependent(String controlCriterion, String criterion, LinkedList<QualityMeasureDependencies> dependencies){
		for (QualityMeasureDependencies qmDependencies : dependencies) {
			if(qmDependencies.getId().equals(controlCriterion) && qmDependencies.isDependent(criterion)){
				return true;
			}
		}
		return false;
	}
	
	private static LinkedList<Matrix> loadClusterComparisons(LinkedList<String> characteristicUris){
		// Load the JSON file containing data about comparison matrices
				URL url = Thread.currentThread().getContextClassLoader()
						.getResource("matrices/pairwiseComparisons.json");
				String path = url.getFile();		
				path = path.replaceAll("%20", " ");		
						
				JSONParser jsonParser = new JSONParser();
				JSONArray jsonComparisonMatrices = null;
				try {
					jsonComparisonMatrices = (JSONArray) jsonParser.parse(new FileReader(new File(path)));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				// Create Matrix Java objects representing comparison matrices from JSON file
				LinkedList<Matrix> comparisonMatrices = new LinkedList<Matrix>();		
				Iterator<JSONObject> iteratorMatrices = jsonComparisonMatrices.iterator();
		        while (iteratorMatrices.hasNext()) {
		        	JSONObject jsonComparisonMatrix = iteratorMatrices.next();
		        	
		        	if(characteristicUris.contains((String) jsonComparisonMatrix.get("id"))){
		        		Integer size = (int)(long)((Long)jsonComparisonMatrix.get("size"));
			        	
			        	JSONArray jsonEntries = (JSONArray)jsonComparisonMatrix.get("entries");
			        	double[] entries = new double [size*size];
			        	for (int i = 0; i < size*size; i++) {
			        		entries[i] = Double.parseDouble(jsonEntries.get(i).toString());
						}
			        	
			        	Matrix comparisonMatrix = new Matrix(entries,size);
			        	comparisonMatrix.setId((String) jsonComparisonMatrix.get("id"));

			        	MatrixMapping matrixMapping = new MatrixMapping();
			        	JSONArray jsonMatrixMappingItems = (JSONArray)jsonComparisonMatrix.get("mapping");
			        	Iterator<JSONObject> iteratorMappings = jsonMatrixMappingItems.iterator();
			            while (iteratorMappings.hasNext()) {
			            	JSONObject jsonMatrixMappingItem = iteratorMappings.next();
			            	matrixMapping.addMapItem(new MapItem((int)(long)((Long)jsonMatrixMappingItem.get("row")), (String) jsonMatrixMappingItem.get("key")));
			            }
			            comparisonMatrix.setMapping(matrixMapping);
			            comparisonMatrices.add(comparisonMatrix);
		        	}
		        }
				
				return comparisonMatrices;
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

}
