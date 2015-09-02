package eu.sealsproject.domain.oet.recommendation.services;

import java.util.LinkedList;


import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolCategory;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolVersion;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;

public class AlternativesFactory {
	
//	public static DataService service = new DataService();

	public static LinkedList<Alternative> createAlternativesList(LinkedList<Requirement> requirements, DataService service){
		LinkedList<ToolCategory> toolCategories = new LinkedList<ToolCategory>();
		LinkedList<Alternative> alternatives = new LinkedList<Alternative>();
		
		for (Requirement requirement : requirements) {
			ToolCategory toolCategory = requirement.getMeasure().getToolCategory();
			if(!toolCategories.contains(toolCategory))
				toolCategories.add(toolCategory);
		}
		
		if(toolCategories.size() == 1){
			for (ToolVersion toolVersion : service.getToolVersions(toolCategories.get(0).getUri().toString())) {	
				toolVersion.setQualityValues(service.getQualityValuesForRequirements(toolVersion.getUri().toString(),
						requirements));
				Alternative alternative = new Alternative();
				alternative.addTool(toolVersion);
				alternatives.add(alternative);
			}
		}
		
		if(toolCategories.size() == 2){
			for (ToolVersion toolVersion1 : service.getToolVersions(toolCategories.get(0).getUri().toString())) {	
				toolVersion1.setQualityValues(service.getQualityValuesForRequirements(toolVersion1.getUri().toString(),
						requirements));
				for (ToolVersion toolVersion2 : service.getToolVersions(toolCategories.get(1).getUri().toString())) {
					toolVersion2.setQualityValues(service.getQualityValuesForRequirements(toolVersion2.getUri().toString(),
							requirements));
					Alternative alternative = new Alternative();
					alternative.addTool(toolVersion1);
					alternative.addTool(toolVersion2);
					alternatives.add(alternative);
				}
			}
		}
		
		if(toolCategories.size() == 3){
			for (ToolVersion toolVersion1 : service.getToolVersions(toolCategories.get(0).getUri().toString())) {	
				toolVersion1.setQualityValues(service.getQualityValuesForRequirements(toolVersion1.getUri().toString(),
						requirements));
				for (ToolVersion toolVersion2 : service.getToolVersions(toolCategories.get(1).getUri().toString())) {
					toolVersion2.setQualityValues(service.getQualityValuesForRequirements(toolVersion2.getUri().toString(),
							requirements));
					for (ToolVersion toolVersion3 : service.getToolVersions(toolCategories.get(2).getUri().toString())) {
						toolVersion3.setQualityValues(service.getQualityValuesForRequirements(toolVersion3.getUri().toString(),
								requirements));
						Alternative alternative = new Alternative();
						alternative.addTool(toolVersion1);
						alternative.addTool(toolVersion2);
						alternative.addTool(toolVersion3);
						alternatives.add(alternative);
					}
				}
			}
		}
		return alternatives;
	}

	
	// this method is called in the case when the comparison according to all criteria is needed 
	// (not only w.r.t. requirements), because alternatives and measures are initially formed based on
	// user requirements only
	/**
	 * Adds quality values for a given quality measure to all alternatives in a given list.
	 * @param alternatives
	 * @param measureUri
	 */
	public static void addMeasureToAlternatives(
			LinkedList<Alternative> alternatives, String measureUri, DataService service) {
		
		for (Alternative alternative : alternatives) {
			for (ToolVersion toolVersion : alternative.getTools()) {
				if(service.coversQualityMeasure(toolVersion.getUri().toString(), measureUri))
					toolVersion.addQualityValue(service.getQualityValue(toolVersion.getUri().toString(), 
							measureUri));
			}
		}
		
	}
}
