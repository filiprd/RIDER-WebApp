package eu.sealsproject.domain.oet.recommendation.util.dependencies;

import java.util.LinkedList;

public class QualityMeasureDependencies {

	/**
	 * Uri of a quality measure
	 */
	private String id;
	
	/**
	 * A list of Uris of quality measures on which the Id measure depends on
	 */
	private LinkedList<String> dependencies;
	
	public QualityMeasureDependencies(String id, LinkedList<String> dependencies) {
		this.id = id;
		this.dependencies = dependencies;
	}
	
	public QualityMeasureDependencies(String id) {
		this.id = id;
		this.dependencies = new LinkedList<String>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LinkedList<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(LinkedList<String> dependencies) {
		this.dependencies = dependencies;
	}
	
	public void addDependency(String Uri){
		this.dependencies.add(Uri);
	}
	
	public boolean isDependent(String criterion){
		if(dependencies.contains(criterion))
			return true;
		return false;
	}


}
