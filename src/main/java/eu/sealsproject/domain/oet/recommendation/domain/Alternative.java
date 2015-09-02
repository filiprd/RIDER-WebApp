package eu.sealsproject.domain.oet.recommendation.domain;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.UUID;

import eu.sealsproject.domain.oet.recommendation.domain.ontology.ToolVersion;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.QualityValue;

public class Alternative {

	private String id;
	
	private LinkedList<ToolVersion> tools;
	
	private double result;


	public Alternative() {
		this.tools = new LinkedList<ToolVersion>();
		this.id = "http://www.seals-project.eu/metadata.owl#Alternatives-"+UUID.randomUUID();
	}

	public Alternative(String id, LinkedList<ToolVersion> tools) {
		this.id = id;
		this.tools = tools;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LinkedList<ToolVersion> getTools() {
		return tools;
	}

	public void setTools(LinkedList<ToolVersion> tools) {
		this.tools = tools;
	}
	
	public void addTool(ToolVersion toolVersion){
		this.getTools().add(toolVersion);
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		NumberFormat format = NumberFormat.getInstance(Locale.UK);
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(8);
		format.setMinimumFractionDigits(8);
		this.result = Double.parseDouble(format.format(result));
		
	}
	
	public QualityValue getQualityValue(String qualityMeasureUri){
		for (ToolVersion toolVersion : getTools()) {
			for (QualityValue value : toolVersion.getQualityValues()) {
				if(value.getForMeasure().getUri().toString().equals(qualityMeasureUri))
					return value;
			}
		}		
		return null;
	}
		
}
