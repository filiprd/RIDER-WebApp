package eu.sealsproject.domain.oet.recommendation.tapestry.pages;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.URLEncoderImpl;
import org.apache.tapestry5.ioc.annotations.Inject;


import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.core.spring.SpringBean;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolCategory;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolVersion;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityMeasure;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;
import eu.sealsproject.domain.oet.recommendation.matrixfactory.ClusterMatrixFactory;
import eu.sealsproject.domain.oet.recommendation.matrixfactory.SupermatrixFactory;
import eu.sealsproject.domain.oet.recommendation.matrixfactory.WeightedMatrixFactory;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.ResultsUtil;
import eu.sealsproject.domain.oet.recommendation.util.Sorter;
import eu.sealsproject.domain.oet.recommendation.util.ThresholdUtil;
import eu.sealsproject.domain.oet.recommendation.util.evaluationscenarios.EvaluationScenariosUtil;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;


public class Recommendation {
	
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
	ToolVersion toolVersion;
	
	@Persist
	Matrix limitSupermatrix;
	
	@Persist
	private Matrix weightedMatrix;
	
	@Inject
	@SpringBean("visualization")
	private EvaluationScenariosUtil visualizationUtil;
	
	//used to link quality measures with the visualization in visualization app
	private URLEncoderImpl tapestryUrlEncoder = new URLEncoderImpl();
	
	public void onActivate() {
					
	}
	
	public Object showRecommendations(LinkedList<Requirement> requirements){
		if(requirements != null){
			this.requirements = requirements;
			
			SupermatrixFactory factory = new SupermatrixFactory();
			Matrix supermatrix = factory.getSubmatrixOnlyRequirements(requirements, service, true);
			
			recommendations = factory.getAlternatives();
			
			Matrix clusterMatrix = factory.getClusterMatrix();
			
			this.weightedMatrix = WeightedMatrixFactory.getWeightedMatrix(supermatrix, 
					clusterMatrix, service);
			
			limitSupermatrix  = weightedMatrix.calculateLimitMatrix();
			
			Sorter.sort(requirements,limitSupermatrix);
			ResultsUtil.getRecommendationResults(limitSupermatrix, recommendations);
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
	
	public QualityValue getQualityValue(Alternative alternative, String qualityMeasureUri){		
		return alternative.getQualityValue(qualityMeasureUri);
	}
	
	public boolean satisfiesThreshold(Alternative alternative, String qualityMeasureUri, String threshold){
		QualityValue value = alternative.getQualityValue(qualityMeasureUri);
		if(ThresholdUtil.satisfiesThreshold(value, threshold))
			return true;
		return false;
	}
	
	public LinkedList<String> getTools(Alternative alternative){
		LinkedList<String> tools = new LinkedList<String>();
		for (ToolVersion tool : alternative.getTools()) {
			tools.add(tool.getName());			
		}
		return tools;
	}
	
	public Object onActionFromShowResults(String toolVersionUri){
		return toolResultsPage.showResults(toolVersionUri);
	}
		
	public String getUrl(String qualityMeasureUri, String toolName){
		return visualizationUtil.getUrl(qualityMeasureUri) + "/" +
		tapestryUrlEncoder.encode(toolName);
	}
	
	public boolean hasVisualization(QualityMeasure measure){
		String measureUri = measure.getUri().toString();
		
		if(measureUri.equalsIgnoreCase("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyLanguageComponentCoverage"))
			return true;
		if(measureUri.equalsIgnoreCase("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyLanguageInterchangeComponentCoverage"))
			return true;
		if(measureUri.equalsIgnoreCase("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyInformationChange"))
			return true;
		if(measureUri.equalsIgnoreCase("http://www.seals-project.eu/ontologies/QualityModel.owl#InterchangeInformationChange"))
			return true;
		if(measureUri.equalsIgnoreCase("http://www.seals-project.eu/ontologies/QualityModel.owl#ImportExportErrors"))
			return true;
		if(measureUri.equalsIgnoreCase("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyInterchangeErrors"))
			return true;

		return false;
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
			importances.add(getImportance(req.getMeasure().getUri().toString()));
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
		NumberFormat format = NumberFormat.getInstance(Locale.UK);
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(4);
		format.setMinimumFractionDigits(4);
		return Double.parseDouble(format.format(result));
	}
	
}
