package eu.sealsproject.domain.oet.recommendation.factories;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityIndicator;
import eu.sealsproject.domain.oet.recommendation.services.AlternativesFactory;
import eu.sealsproject.domain.oet.recommendation.services.ComparisonService;
import eu.sealsproject.domain.oet.recommendation.services.ExpertComparisonService;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.RequirementsUtil;
import eu.sealsproject.domain.oet.recommendation.util.comparators.AlternativesComparator;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;

public class HierarchyFactory {

	private Properties config;
	
	private LinkedList<Requirement> originalRequirements;
	
	// will also contain indicators not originaly specified as requirements (for complete hierarchy)
	private LinkedList<Requirement> _requirements;
	
	private Collection<QualityIndicator> qualityIndicators = new LinkedList<QualityIndicator>();
	
	private LinkedList<Matrix> criteriaComparisons;
	
	LinkedList<Alternative> alternatives;
	
	LinkedList<Matrix> alternativeComparisons;
	
	private Properties criteriaWeights = new Properties();

	
	public LinkedList<Alternative> getRecommendations(LinkedList<Requirement> requirements,
			DataService service) {
		
		loadConfig();
		LinkedList<Matrix> criteriaComparisons = loadCriteriaComparisons();
		this.originalRequirements = (LinkedList<Requirement>)requirements.clone();
		_requirements = requirements;
		
		if (config.getProperty("modelExtraction-method").equals("requirements"))
			return getRequirementsHierarchyRecommendation(criteriaComparisons, service);
		if (config.getProperty("modelExtraction-method").equals("complete"))
			return getCompleteHierarchyRecommendation(criteriaComparisons, service);
		return null;
	}
	

	private LinkedList<Alternative> getRequirementsHierarchyRecommendation(LinkedList<Matrix> criteriaComparisons, 
			DataService service) {
		
		this.criteriaComparisons = criteriaComparisons;
		
		compareAlternatives(service);
		compareHierarchyCriteria();
		
		return calculateAHPRecommendation();
	}
	
	
	private LinkedList<Alternative> getCompleteHierarchyRecommendation(LinkedList<Matrix> criteriaComparisons, 
			DataService service) {
		
		this.criteriaComparisons = criteriaComparisons;	
		this.qualityIndicators = service.getAllQualityIndicators();
		
		for (QualityIndicator qualityIndicator : qualityIndicators) {
			if(!isSpecifiedRequirement(qualityIndicator.getUri().toString()))
				_requirements.add(RequirementsUtil.getRequirement(_requirements, qualityIndicator.getUri().toString(), 
					service));
		}
		
		compareAlternatives(service);
		compareHierarchyCriteria();
		
		return calculateAHPRecommendation();
	}
	
	
	private boolean isSpecifiedRequirement(String indicatorUri) {
		for (Requirement requirement : originalRequirements) {
			if(requirement.getIndicator().getUri().toString().equals(indicatorUri))
				return true;				
		}
		return false;
	}


	private void compareAlternatives(DataService service){
		this.alternativeComparisons = new LinkedList<Matrix>();
		this.alternatives = AlternativesFactory.createAlternativesList(_requirements, service);
		
		for (Requirement requirement : _requirements) {
			alternativeComparisons.add(ComparisonService.compareAlternatives(requirement, 
					alternatives, config.getProperty("comparisonAlgorithm"), service));
		}
	}
	
	
	private void compareHierarchyCriteria(){
		ExpertComparisonService expertComparisonService = new ExpertComparisonService();
		expertComparisonService.calculateHierarchyWeights(criteriaWeights, _requirements, criteriaComparisons);	
	}
	
	
	private LinkedList<Alternative> calculateAHPRecommendation(){
		for (Alternative alternative : alternatives) {
			double result = 0;
			for (Requirement requirement : _requirements) {							
				double weight = Double.parseDouble(criteriaWeights.get(requirement.getIndicator().getUri().toString()).toString()) *
						Double.parseDouble(criteriaWeights.get(requirement.getIndicator().getQualityCharacteristic().getUri().toString()).toString());
				for (Matrix comparison : alternativeComparisons) {
					if(comparison.getId().equals(requirement.getIndicator().getUri().toString())){
						double comparisonResult = 
								comparison.get(comparison.getMapping().getRowNumber(alternative.getId()), 0);
						result = result + weight * comparisonResult;
					}
				}
			}
			alternative.setResult(result);
		}	
		Collections.sort((List<Alternative>) alternatives, new AlternativesComparator());
		return alternatives;
	}



	/**
	 * Loads the configuration properties file
	 */
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
	
	
	/**
	 * Loads criteria pairwise comparison matrices from the JSON file
	 * @return
	 */
	public LinkedList<Matrix> loadCriteriaComparisons() {
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
	
	public Properties getCriteriaWeights(){
		return this.criteriaWeights;
	}
	

}
