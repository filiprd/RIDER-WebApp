package eu.sealsproject.domain.oet.recommendation.domain.general;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import eu.sealsproject.domain.oet.recommendation.config.Constants;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
@RdfType("Resource")
public class Resource implements Serializable{

	private static final long serialVersionUID = -2234520192109835808L;
	
	protected URI uri;
	
	private String description;
	

	public Resource() {	
		this.setUri("http://seals.eu#Resource" + UUID.randomUUID());
	}
	

	public Resource(URI uri) {
		this.uri = uri;
	}
	
	public Resource(String uri) {
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Id
	public URI getUri() {
		return uri;
	}
	
	public void setUri(URI uri){
		this.uri = uri;
	}
	
	public void setUri(String uri){
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@RdfProperty("http://purl.org/dc/terms/description")
	public String getDescription() {
		if(!description.contains("<br>")){
			String[] list = description.split("\\. ");
			description = "<b>Definition</b>: " + list[0] + "<br/>"; 
			String[] second = list[1].split(",");
			description = description + "<b>Range</b>: " + second[0].split("range")[1] + "<br>";
			String better = second[1].substring(7, second[1].length()-1);
			description = description + "<b>Ranking</b>: a " + better.split("better")[0] + "a better result";
		}
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public boolean equals(Object o) {
		if ( !(o instanceof Resource) )
			return false;
		Resource r = (Resource)o;
		return r.getUri().equals(this.getUri());
	}
	
	public String toString() {
		return getUri().toString();
	}
	

}
