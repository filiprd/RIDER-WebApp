package eu.sealsproject.domain.oet.recommendation.tapestry.pages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;

import eu.sealsproject.domain.oet.recommendation.core.spring.SpringBean;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolCategory;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.RatioScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.Scale;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.RequirementsUtil;
import eu.sealsproject.domain.oet.recommendation.util.ScaleValuesUtil;
import eu.sealsproject.domain.oet.recommendation.util.comparators.RequirementsComparator;


/**
 * Start page of application seals-recommendation.
 */
@Import(library={"context:js/jquery-1.3.1.min.js",
"context:js/jquery.betterToolTip.js"})
public class Index{
	
	@InjectPage
	private Recommendation recommendationPage;
	
	@InjectPage
	private Error errorPage;
	
	@Inject
	@SpringBean("RepositoryService")
	private DataService service;
	
	@InjectComponent
	private Form requirementsForm;
	
	@Persist
	private Collection<Requirement> requirements;
		
	private Requirement requirement;
	
	/**
	 * Properties used for filter form
	 */
	@Property
    @Persist
    private boolean ontologyEngineeringTools;
	
	@Property
    @Persist
    private boolean ontologyMatchingTools;
	
	@Property
    @Persist
    private boolean reasoningSystems;
	
	@Property
    @Persist
    private boolean semanticSearchTools;
	
	@Property
    @Persist
    private boolean semanticWebServices;
	
	@Property
    @Persist
    private boolean allTools;
	
	/**
	 * Property for filtering requirements according to results availability
	 */
	@Property
    @Persist
    private boolean	onlyWithResults;
    
    /**
     * Used for removing the threshold entry for non evaluated measures
     */
    @Property
    private boolean isEvaluated;
	
	/**
	 * This property is used in the tapestry page to show the tool type of which the tools
	 * are evaluated with particular quality measure
	 * 
	 */
	@Property
	private String toolCategory;
	
	@Property
	private boolean ratio;
	
	@Property
	private boolean interval;
	
	private Collection<String> scaleValues  = new LinkedList<String>();
	
	@Property
	@Persist
	private String resultsToShow;
	

	
	public void onActivate() {
		if(requirements == null){
			this.requirements = new LinkedList<Requirement>();
			this.requirements = RequirementsUtil.getRequirementsForCharacteristics
				(service.getAllQualityCharacteristics());
			this.allTools = true;
			Collections.sort((LinkedList<Requirement>)requirements, new RequirementsComparator(service));
		}
	}
	
	

	Object onSuccessFromRequirementsForm() {
		LinkedList<Requirement> filledRequirements = new LinkedList<Requirement>();
		for (Requirement r : requirements) {
			if(r.getThreshold() != null)
				filledRequirements.add(r);
		}				
		try {
			return recommendationPage.showRecommendations(filledRequirements);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return errorPage;
		}		
	}
	
	


	public Requirement getRequirement() {
		return this.requirement;
	}


	public void setRequirement(Requirement requirement) {	
		Scale scale = requirement.getMeasure().getScale();
		this.isEvaluated = service.isEvaluated(requirement.getMeasure().getUri().toString());		
		if((scale instanceof RatioScale))
			ratio = true;
		else{
			ratio = false;
			if((scale instanceof IntervalScale))
				interval = true;
			else
				interval = false;
		}
		this.requirement = requirement;
	}
	

	public Collection<String> getScaleValues() {
		scaleValues.clear();
		ScaleValuesUtil.getScaleValues(this.requirement.getMeasure().getScale(), scaleValues);
		return scaleValues;
	}
	
	public String getIntervalValues(Scale scale) {
		String intervalValues = ScaleValuesUtil.getIntervalScaleValues(scale);
		return intervalValues;
	}


	public void setScaleValues(Collection<String> scaleValues) {
		this.scaleValues = scaleValues;
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "requirementsForm")
	public void onValidate() {
		int count = 0;
		for (Requirement r : requirements) {
			if(r.getThreshold() == null){
				count++;
			}
		}
		if(count == requirements.size())
			requirementsForm.recordError("You have to provide at least one criterion");
	}
	

	public Collection<Requirement> getRequirements() {
		return requirements;
	}


	public void setRequirements(Collection<Requirement> requirements) {
		this.requirements = requirements;
	}
	

	Object onSuccessFromResultsFilterForm(){
		if(onlyWithResults){
			LinkedList<Requirement> evaluatedMeasures = new LinkedList<Requirement>();
			for (Requirement requirement : this.requirements) {
				if(service.isEvaluated(requirement.getMeasure().getUri().toString()))
					evaluatedMeasures.add(requirement);
			}
			this.setRequirements(evaluatedMeasures);
		}
		else{
			changeRequirements();
		}
		return this;
	}
	
	Object onSuccessFromToolFilterForm(){
		changeRequirements();
		return this;
	 }
	
	public void changeRequirements(){
		this.requirements = new LinkedList<Requirement>();
		
		if(allTools){
			this.requirements = RequirementsUtil.getRequirementsForCharacteristics
			(service.getAllQualityCharacteristics());
			Collections.sort((LinkedList<Requirement>)requirements, new RequirementsComparator(service));
			return;
		}
			
		
		if(ontologyEngineeringTools)
			this.requirements.addAll(RequirementsUtil.getRequirementsForMeasures(service.getQualityMeasuresForToolType(ToolCategory.ONTOLOGY_ENGINEERING_TOOL.getUri().toString())));
		
		if(ontologyMatchingTools)
			this.requirements.addAll(RequirementsUtil.getRequirementsForMeasures(service.getQualityMeasuresForToolType(ToolCategory.ONTOLOGY_MAPPING_TOOL.getUri().toString())));
		
		if(semanticSearchTools)
			this.requirements.addAll(RequirementsUtil.getRequirementsForMeasures(service.getQualityMeasuresForToolType(ToolCategory.SEMANTIC_SEARCH_TOOL.getUri().toString())));
		
		if(semanticWebServices)
			this.requirements.addAll(RequirementsUtil.getRequirementsForMeasures(service.getQualityMeasuresForToolType(ToolCategory.SEMANTIC_WEB_SERVICE_TOOL.getUri().toString())));
		
		if(reasoningSystems)
			this.requirements.addAll(RequirementsUtil.getRequirementsForMeasures(service.getQualityMeasuresForToolType(ToolCategory.RESONER_OR_STORAGE_TOOL.getUri().toString())));
		
		Collections.sort((LinkedList<Requirement>)requirements, new RequirementsComparator(service));
	}
	
	public String getShowAll(){
		return "showAll";
	}
	
	public String getWithResults(){
		return "withResults";
	}
	
	StreamResponse onReturnPdf() {
		return new StreamResponse() {
			InputStream inputStream;

			
			public void prepareResponse(Response response) {
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				inputStream = classLoader.getResourceAsStream("qmodel.pdf");

				// Set content length to prevent chunking - see
				// http://tapestry-users.832.n2.nabble.com/Disable-Transfer-Encoding-chunked-from-StreamResponse-td5269662.html#a5269662

				try {
					response.setHeader("Content-Length", "" + inputStream.available());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}

			
			public String getContentType() {
				return "application/pdf";
			}

			
			public InputStream getStream() throws IOException {
				return inputStream;
			}
		};
	}

}
