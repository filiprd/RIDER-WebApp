package eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;

@Namespace(Constants.QUALITY_MODEL_NS)
@RdfType("QualityCharacteristic")
public class QualityCharacteristic extends Resource{
	
	private String name;
		
	private Collection<QualityMeasure> qualityMeasures = new LinkedList<QualityMeasure>();

	public QualityCharacteristic(){
	}
	
	public QualityCharacteristic(URI uri, String name) {
		super(uri);
		this.name = name;
	}
	
	public QualityCharacteristic(String uri, String name) {
		super(uri);
		this.name = name;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8428528803740244920L;


	@RdfProperty("http://purl.org/dc/terms/title")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "measuredBy")
	public Collection<QualityMeasure> getQualityMeasures() {
		return qualityMeasures;
	}

	public void setQualityMeasures(Collection<QualityMeasure> qualityMeasures) {
		this.qualityMeasures = qualityMeasures;
	}
	
	public void addQualityMeasure(QualityMeasure measure){
		if(!getQualityMeasures().contains(measure))
			getQualityMeasures().add(measure);
	}
	
}
