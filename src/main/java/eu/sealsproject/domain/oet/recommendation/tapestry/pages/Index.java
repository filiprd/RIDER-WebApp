package eu.sealsproject.domain.oet.recommendation.tapestry.pages;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

import eu.sealsproject.domain.oet.recommendation.core.spring.SpringBean;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.MeasurementScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.RatioScale;
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
	private String subjectCategory;
	
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
		MeasurementScale scale = requirement.getIndicator().getScale();
		this.isEvaluated = service.isEvaluated(requirement.getIndicator().getUri().toString());		
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
		ScaleValuesUtil.getScaleValues(this.requirement.getIndicator().getScale(), scaleValues);
		return scaleValues;
	}
	
	public String getIntervalValues(MeasurementScale scale) {
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
	
	
	public String getShowAll(){
		return "showAll";
	}
	
	public String getWithResults(){
		return "withResults";
	}
	
	public String getSubjectCategoryName(String qualityIndicatorUri){
		return service.getSubjectCategory(qualityIndicatorUri).getName();
	}

}
