package eu.sealsproject.domain.oet.recommendation.domain.ontology;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;

@Namespace(Constants.SEALS_METADATA_NS)
@RdfType("ToolCategory")
public class ToolCategory extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2529340489965935427L;

	public static ToolCategory ONTOLOGY_ENGINEERING_TOOL = 
		new ToolCategory();
	
	public static ToolCategory ONTOLOGY_MAPPING_TOOL = 
		new ToolCategory();
	
	public static ToolCategory RESONER_OR_STORAGE_TOOL = 
		new ToolCategory();
	
	public static ToolCategory SEMANTIC_SEARCH_TOOL = 
		new ToolCategory();
	
	public static ToolCategory SEMANTIC_WEB_SERVICE_TOOL = 
		new ToolCategory();
	
	private String name;
	
	static{
		ONTOLOGY_ENGINEERING_TOOL.setUri(Constants.SEALS_METADATA_NS + "OntologyEngineeringTool");
		ONTOLOGY_ENGINEERING_TOOL.setName("Ontology Engineering Tools");
		ONTOLOGY_MAPPING_TOOL.setUri(Constants.SEALS_METADATA_NS + "OntologyMappingTool");
		ONTOLOGY_MAPPING_TOOL.setName("Ontology Matching Tools");
		RESONER_OR_STORAGE_TOOL.setName("Reasoner Systems");
		RESONER_OR_STORAGE_TOOL.setUri(Constants.SEALS_METADATA_NS + "ReasonerOrStorageTool");
		SEMANTIC_SEARCH_TOOL.setName("Semantic Search Tools");
		SEMANTIC_SEARCH_TOOL.setUri(Constants.SEALS_METADATA_NS + "SemanticSearchTool");
		SEMANTIC_WEB_SERVICE_TOOL.setName("Semantic Web Service Tools");
		SEMANTIC_WEB_SERVICE_TOOL.setUri(Constants.SEALS_METADATA_NS + "SemanticWebServiceTool");
	}

	@RdfProperty(Constants.SEALS_METADATA_NS + "hasName")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
