package eu.sealsproject.domain.oet.recommendation.tapestry.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.core.spring.SpringBean;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.EvaluationSubject;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;
import eu.sealsproject.domain.oet.recommendation.factories.HierarchyFactory;
import eu.sealsproject.domain.oet.recommendation.factories.SupermatrixFactory;
import eu.sealsproject.domain.oet.recommendation.factories.WeightedMatrixFactory;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.ResultsUtil;
import eu.sealsproject.domain.oet.recommendation.util.Sorter;
import eu.sealsproject.domain.oet.recommendation.util.ThresholdUtil;
import eu.sealsproject.domain.oet.recommendation.util.comparators.AlternativesComparator;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;


public class Recommendation {
	
	@Persist
	private Properties config;
	
	@Inject
	@SpringBean("RepositoryService")
	private DataService service;
	
	@InjectPage
	private ToolResults toolResultsPage;
	
	@InjectPage
	private Index index;
	
	@InjectPage
	private ImportanceExplanation importanceExplanationPage;
	
	@Persist
	private LinkedList<Requirement> requirements;
	
	private Requirement requirement;
	
	@Persist
	@Property
	private LinkedList<Alternative> recommendations;
	
	@Property
	private Alternative alternative;
	
	@Property
	EvaluationSubject evaluationSubject;
	
	@Persist
	Matrix limitSupermatrix;
	
	@Persist
	private Matrix weightedMatrix;

	@Persist
	private Properties criteriaWeights;
	
	@Persist
	@Property
	private boolean isAnp;
	
	DecimalFormat format = new DecimalFormat();
	
	NumberFormat alternativeFormat = NumberFormat.getInstance(Locale.UK);	
	
	public void onActivate() {
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(3);
		format.setMinimumFractionDigits(3);
		alternativeFormat.setMinimumIntegerDigits(1);
		alternativeFormat.setMaximumFractionDigits(4);
		alternativeFormat.setMinimumFractionDigits(4);
	}
	
	public Object showRecommendations(LinkedList<Requirement> requirements){
		if(requirements != null){
			this.requirements = requirements;
			
			loadConfig();
			if(config.get("mcdm-method").equals("anp"))
				makeANPRecommendations(requirements);
			if(config.get("mcdm-method").equals("ahp"))
				makeAHPRecommendations(requirements);
			
		}
		return this;
	}

	public LinkedList<Requirement> getRequirements() {
		
		return requirements;
	}

	public void setRequirements(LinkedList<Requirement> requirements) {
		this.requirements = requirements;
	}
	
	/**
	 * Use for numbering in the results table
	 * @param order
	 * @return
	 */
	public int increaseOrder(int order){
		return order+1;
	}

	public Requirement getRequirement() {
		return requirement;
	}

	public void setRequirement(Requirement requirement) {
		this.requirement = requirement;
	}
	
	public String getImportance(String measureUri){
		if(config.get("mcdm-method").equals("anp")){
			for (MapItem item : limitSupermatrix.getMapping().getMap()) {
				if (item.getChracteristicUri().equals(measureUri)){
					return format.format(limitSupermatrix.get(item.getRowNumber(), 0));
				}
			}
		}
			
		if(config.get("mcdm-method").equals("ahp")){
			return format.format(Double.parseDouble(criteriaWeights.get(measureUri).toString()) *
					Double.parseDouble(criteriaWeights.get(service.getCharacteristicUriOfIndicator(measureUri)).toString()));
		}
		return "";
	}
	
	public QualityValue getQualityValue(Alternative alternative, String qualityIndicatorUri){		
		return service.getQualityValueForAlternative(alternative, qualityIndicatorUri);
	}
	
	public boolean satisfiesThreshold(Alternative alternative, String qualityIndicatorUri, String threshold){
		QualityValue value = service.getQualityValueForAlternative(alternative, qualityIndicatorUri);
		if(ThresholdUtil.satisfiesThreshold(value, threshold))
			return true;
		return false;
	}
	
	public LinkedList<String> getTools(Alternative alternative){
		LinkedList<String> tools = new LinkedList<String>();
		for (EvaluationSubject tool : alternative.getEvaluationSubjects()) {
			tools.add(tool.getName());			
		}
		return tools;
	}
	
	public Object onActionFromShowResults(String evaluationSubjectUri){
		return toolResultsPage.showResults(evaluationSubjectUri);
	}
		
	
	public boolean lessThanTen(int i){
		if(i+1 <= 10)
			return true;
		return false;
	}
	
	public boolean greaterThanTen(int i){
		if(i+1 > 10)
			return true;
		return false;
	}
	
	public Object onActionFromExplainImportance(){
		LinkedList<String> importances = new LinkedList<String>();
		for (Requirement req : requirements) {
			importances.add(getImportance(req.getIndicator().getUri().toString()));
		}
		importanceExplanationPage.setImportance(importances);
		return importanceExplanationPage.explain(requirements, this.weightedMatrix, recommendations.size());
	}
	
	public boolean moreThanTenAlternatives(){
		if(this.recommendations.size() > 10)
			return true;
		return false;
	}
	
	public double getResult(double result){
		return Double.parseDouble(alternativeFormat.format(result));
	}
	
	private void makeANPRecommendations(LinkedList<Requirement> requirements){
		this.isAnp = true;
		SupermatrixFactory factory = new SupermatrixFactory();
		Matrix supermatrix = factory.create(requirements, service);
		
		recommendations = factory.getAlternatives();
		
		Matrix clusterMatrix = factory.getClusterMatrix();
		
		this.weightedMatrix = WeightedMatrixFactory.getWeightedMatrix(supermatrix, 
				clusterMatrix, service);
		
		limitSupermatrix  = weightedMatrix.calculateLimitMatrix();
		
		Sorter.sort(requirements,limitSupermatrix);
		ResultsUtil.getRecommendationResults(limitSupermatrix, recommendations);
	}
	
	
	private void makeAHPRecommendations(LinkedList<Requirement> requirements){
		this.isAnp = false;
		HierarchyFactory factory = new HierarchyFactory();
		recommendations = factory.getRecommendations(requirements, service);
		criteriaWeights = factory.getCriteriaWeights();
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
	
}
