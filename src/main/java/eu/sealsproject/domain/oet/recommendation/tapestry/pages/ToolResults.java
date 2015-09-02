package eu.sealsproject.domain.oet.recommendation.tapestry.pages;

import java.util.Collection;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import eu.sealsproject.domain.oet.recommendation.core.spring.SpringBean;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolVersion;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;

public class ToolResults {

	@Inject
	@SpringBean("RepositoryService")
	private DataService service;
	
	@Property
	@Persist
	private ToolVersion toolVersion;
	
	@Property
	private QualityValue _qualityValue;
	
	@Property
	@Persist
	private Collection<QualityValue> qualityValues;
	
	public ToolResults showResults(String toolVersionUri){
		this.toolVersion = service.getToolVersion(toolVersionUri);
		this.qualityValues = service.getResultsForTool(toolVersionUri);
		return this;
	}
	
	public boolean hasVersion(ToolVersion toolVersion){
		if(toolVersion.getVersionNumber().equalsIgnoreCase(""))
			return false;
		if(toolVersion.getVersionNumber() == null)
			return false;
		return true;
	}
}
