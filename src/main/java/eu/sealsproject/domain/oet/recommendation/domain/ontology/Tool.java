package eu.sealsproject.domain.oet.recommendation.domain.ontology;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;

@Namespace(Constants.SEALS_METADATA_NS)
@RdfType("Tool")
public class Tool extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 736793819193524508L;

	private ToolCategory toolCategory;
	
	private Collection<ToolVersion> toolVersions = new LinkedList<ToolVersion>();
	
	private String name;
	
		
	/**
	 * This construction is deprecated. Use Tool(ToolCategory toolCategory, ToolVersion toolVersion, String name)
	 */
	@Deprecated
	public Tool(){}
	
	/**
	 * This construction is deprecated. Use Tool(ToolCategory toolCategory, ToolVersion toolVersion, String name)
	 */
	@Deprecated
	public Tool(String uri){
		super(uri);
	}
	
	public Tool(URI uri, ToolCategory toolCategory, String name) {
		this.uri = uri;
		this.toolCategory = toolCategory;
		this.name = name;
	}


//	
//	private String type;
//	
//	private List<QualityMeasure> characteristics;
//
//	public Tool() {
//	}
//	
//	public Tool(String name, String type) {
//		this.characteristics = new LinkedList<QualityMeasure>();
//		this.name = name;
//		this.type = type;
//	}

	@RdfProperty(Constants.SEALS_METADATA_NS + "hasName")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public String getId() {
//		return type;
//	}
//
//	public void setId(String type) {
//		this.type = type;
//	}
//
//	public void addCharacteristic(QualityMeasure characteristic){
//		if(!this.characteristics.contains(characteristic))
//			getCharacteristics().add(characteristic);
//	}
//	
//	public List<QualityMeasure> getCharacteristics() {
//		return characteristics;
//	}
//
//	public void setCharacteristics(List<QualityMeasure> characteristics) {
//		this.characteristics = characteristics;
//	}

	@RdfProperty(Constants.SEALS_METADATA_NS + "hasToolCategory")
	public ToolCategory getToolCategory() {
		return toolCategory;
	}

	public void setToolCategory(ToolCategory toolCategory) {
		this.toolCategory = toolCategory;
	}
	
	@RdfProperty(Constants.SEALS_METADATA_NS + "hasToolVersion")
	public Collection<ToolVersion> getToolVersions() {
		return toolVersions;
	}

	public void setToolVersions(Collection<ToolVersion> toolVersions) {
		this.toolVersions = toolVersions;
	}
	
	public void addToolVersion(ToolVersion toolVersion){
		if(!this.getToolVersions().contains(toolVersion))
			this.getToolVersions().add(toolVersion);
	}
}
