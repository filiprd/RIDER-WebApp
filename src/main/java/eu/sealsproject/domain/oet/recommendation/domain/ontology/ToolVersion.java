package eu.sealsproject.domain.oet.recommendation.domain.ontology;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;

@Namespace(Constants.SEALS_METADATA_NS)
@RdfType("ToolVersion")
public class ToolVersion extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2178542377244768755L;

	private String versionNumber;
	
	private String name;
	
	private Tool ofTool;
	
	Collection<QualityValue> qualityValues = new LinkedList<QualityValue>();
	
	/**
	 * This construction is deprecated. Use ToolVersion(String versionNumber, String name)
	 */
	@Deprecated
	public ToolVersion(){}
	
	/**
	 * This construction is deprecated. Use ToolVersion(String versionNumber, String name)
	 */
	@Deprecated
	public ToolVersion(String uri){
		super(uri);
	}

	public ToolVersion(URI uri, String versionNumber, String name, Tool ofTool) {
		this.uri = uri;
		this.versionNumber = versionNumber;
		this.name = name;
		this.ofTool = ofTool;
		ofTool.addToolVersion(this);
	}

	@RdfProperty(Constants.SEALS_METADATA_NS + "hasVersionNumber")
	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	@RdfProperty(Constants.SEALS_METADATA_NS + "hasName")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@RdfProperty(Constants.SEALS_METADATA_NS + "isToolVersionOf")
	public Tool getOfTool() {
		return ofTool;
	}

	public void setOfTool(Tool ofTool) {
		this.ofTool = ofTool;
		ofTool.addToolVersion(this);
	}
	
	
	public Collection<QualityValue> getQualityValues() {
		return qualityValues;
	}

	public void setQualityValues(Collection<QualityValue> qualityValues) {
		this.qualityValues = qualityValues;
	}
	
	public void addQualityValue(QualityValue qualityValue){
		if(!qualityValues.contains(qualityValue))
			qualityValues.add(qualityValue);
	}
}
