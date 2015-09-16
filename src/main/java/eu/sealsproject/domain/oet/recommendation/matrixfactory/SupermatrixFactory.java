package eu.sealsproject.domain.oet.recommendation.matrixfactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolCategory;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityMeasure;
import eu.sealsproject.domain.oet.recommendation.services.AlternativesFactory;
import eu.sealsproject.domain.oet.recommendation.services.ExpertComparisonService;
import eu.sealsproject.domain.oet.recommendation.services.SupermatrixService;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.dependencies.QualityMeasureDependencies;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;


/**
 * This class is used for loading the dependence matrix from file. It also provides a method for 
 * extracting the submatrix based on the requirements
 * @author Filip
 *
 */
public class SupermatrixFactory {
	
	Matrix clusterMatrix;
	
	LinkedList<Alternative> alternatives = new LinkedList<Alternative>();
	
	private Properties config;
	
	private boolean compareOnlyRequirements;
	
	private LinkedList<QualityMeasureDependencies> dependencies;
		
	
	/**
	 * Creates a supermatrix matrix. The supermatrix matrix created based on requirements and criteria comparisons.
	 * @return
	 */
	public Matrix create(LinkedList<Requirement> requirements, DataService service){
		loadConfig();
		LinkedList<Matrix> criteriaComparisons = loadCriteriaComparisons();
		this.dependencies = loadDependencies();
		if(config.getProperty("modelExtraction-comparison").equals("requirements"))
			this.compareOnlyRequirements = true;
		if(config.getProperty("modelExtraction-comparison").equals("complete"))
			this.compareOnlyRequirements = false;
				
		if (config.getProperty("modelExtraction-method").equals("requirements"))
			return getRequirementsSupermatrix(requirements, criteriaComparisons, service);
		if (config.getProperty("modelExtraction-method").equals("partial"))
			return getPartialSupermatrix(requirements, criteriaComparisons, service);
		if (config.getProperty("modelExtraction-method").equals("complete"))
			return getCompleteSupermatrix(requirements, criteriaComparisons, service);
		return null;
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
	}
	
	
	/**
	 * Returns the supermatrix based only on user requirements
	 * @param requirements
	 * @param criteriaComparisons
	 * @param service
	 * @return
	 */
	private Matrix getRequirementsSupermatrix(LinkedList<Requirement> requirements, LinkedList<Matrix> criteriaComparisons, DataService service){
		MatrixMapping supermatrixMapping = new MatrixMapping();
		Matrix supermatrixMatrix = new Matrix(requirements.size(), requirements.size(), 0);
		supermatrixMatrix.setId("supermatrix");
		
		int indexer = 0;
		for (Requirement requirement : requirements) {
			supermatrixMapping.addMapItem(new MapItem(indexer++, requirement.getMeasure().getUri().toString()));
		}
		supermatrixMatrix.setMapping(supermatrixMapping);
		
		return fillSupermatrixWithComparisons(supermatrixMatrix, requirements, supermatrixMapping, criteriaComparisons, service);	
		
	}
	
	
	/**
	 * Returns the supermatrix based on all available quality measures
	 * @param requirements
	 * @param criteriaComparisons
	 * @param service
	 * @return
	 */
	private Matrix getCompleteSupermatrix(LinkedList<Requirement> requirements, LinkedList<Matrix> criteriaComparisons, DataService service){
		MatrixMapping supermatrixMapping = new MatrixMapping();
		Collection<QualityMeasure> qualtyMeasures = service.getAllQualityMeasures();
		
		Matrix supermatrixMatrix = new Matrix(qualtyMeasures.size(), qualtyMeasures.size(), 0);
		supermatrixMatrix.setId("supermatrix");
		
		int indexer = 0;
		for (QualityMeasure qualityMeasure : qualtyMeasures) {
			supermatrixMapping.addMapItem(new MapItem(indexer++, qualityMeasure.getUri().toString()));
		}
		supermatrixMatrix.setMapping(supermatrixMapping);
		
		return fillSupermatrixWithComparisons(supermatrixMatrix, requirements, supermatrixMapping, criteriaComparisons, service);	
	}
	
	
	/**
	 * Returns the supermatrix based on measures from requirements and those that influence them
	 * @param requirements
	 * @param criteriaComparisons
	 * @param service
	 * @return
	 */
	private Matrix getPartialSupermatrix(LinkedList<Requirement> requirements, LinkedList<Matrix> criteriaComparisons, DataService service){
		MatrixMapping supermatrixMapping = new MatrixMapping();
		LinkedList<String> supermatrixCriteria = new LinkedList<String>();
		
		int indexer = 0;
		for (Requirement requirement : requirements) {
			if (!supermatrixCriteria.contains(requirement.getMeasure().getUri()
					.toString())) {
				supermatrixMapping.addMapItem(new MapItem(indexer++,
						requirement.getMeasure().getUri().toString()));
				supermatrixCriteria.add(requirement.getMeasure().getUri()
						.toString());
			}
			for (QualityMeasureDependencies qmDependencies : dependencies) {
				if (qmDependencies.getId().equals(
						requirement.getMeasure().getUri().toString())) {
					for (String criteria : qmDependencies.getDependencies()) {
						if (!supermatrixCriteria.contains(criteria)) {
							supermatrixMapping.addMapItem(new MapItem(
									indexer++, criteria));
							supermatrixCriteria.add(criteria);
						}
					}
				}
			}
		}

		Matrix supermatrixMatrix = new Matrix(indexer, indexer, 0);
		supermatrixMatrix.setId("supermatrix");
		supermatrixMatrix.setMapping(supermatrixMapping);
		
		return fillSupermatrixWithComparisons(supermatrixMatrix, requirements, supermatrixMapping, criteriaComparisons, service);
	}
	
	
	/**
	 * Fills the supermatrix with pairwise comparisons of criteria and of the alternatives
	 * @param supermatrixMatrix - supermatrix containing only criteria
	 * @param requirements - user requirements
	 * @param supermatrixMapping - the mapping of the supermatrix
	 * @param criteriaComparisons - set of criteria pairwise comparisons
	 * @param service
	 * @return
	 */
	private Matrix fillSupermatrixWithComparisons(Matrix supermatrixMatrix, LinkedList<Requirement> requirements, MatrixMapping supermatrixMapping,
			LinkedList<Matrix> criteriaComparisons, DataService service){
		ExpertComparisonService expertSupermatrixComparisons = new ExpertComparisonService();
		for (int i = 0; i < supermatrixMatrix.getRowDimension(); i++) {
			expertSupermatrixComparisons.setSupermatrixComparison(supermatrixMatrix, supermatrixMapping.getCharacteristicUri(i), 
					criteriaComparisons, dependencies);
		}
				
		this.alternatives = AlternativesFactory.createAlternativesList(requirements, service);
		Matrix supermatrix = SupermatrixService.fillSupermatrixWithAlternatives(supermatrixMatrix,requirements,this.alternatives,service,compareOnlyRequirements);
		initializeClusterMatrix(requirements, supermatrix, service,compareOnlyRequirements);
		return supermatrix;
	}

	
	protected void loadConfig(){	
		URL url = Thread.currentThread().getContextClassLoader()
		.getResource("config/config.properties");
		String path = url.getFile();
		// remove white spaces encoded with %20
		path = path.replaceAll("%20", " ");
		this.config = new Properties();
		try {
			config.load(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public LinkedList<Matrix> loadCriteriaComparisons() {
		// Load the JSON file containing data about comparison matrices
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource("matrices/criteriaComparisons.json");
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
		
		return comparisonMatrices;
	}
	
	
	public LinkedList<QualityMeasureDependencies> loadDependencies(){
		// Load the JSON file containing data about comparison matrices
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource("matrices/criteriaDependencies.json");
		String path = url.getFile();
		path = path.replaceAll("%20", " ");

		JSONParser jsonParser = new JSONParser();
		JSONArray jsonDependencies = null;
		try {
			jsonDependencies = (JSONArray) jsonParser
					.parse(new FileReader(new File(path)));
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
		
		LinkedList<QualityMeasureDependencies> qmDependencies = new LinkedList<QualityMeasureDependencies>();
		Iterator<JSONObject> iteratorDependencies = jsonDependencies.iterator();
        while (iteratorDependencies.hasNext()) {
        	JSONObject jsonQualityMeasure = iteratorDependencies.next();
        	
        	QualityMeasureDependencies dependencies = new QualityMeasureDependencies((String)jsonQualityMeasure.get("id"));
        	
        	JSONArray jsonQMDependencies = (JSONArray)jsonQualityMeasure.get("dependencies");
        	for (int i = 0; i < jsonQMDependencies.size(); i++) {
        		dependencies.addDependency((String)jsonQMDependencies.get(i).toString());
			}
        	qmDependencies.add(dependencies);
        }

        return qmDependencies;
	}
	
}
