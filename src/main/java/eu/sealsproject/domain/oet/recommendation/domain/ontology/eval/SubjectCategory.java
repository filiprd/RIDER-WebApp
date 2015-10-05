package eu.sealsproject.domain.oet.recommendation.domain.ontology.eval;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;

@Namespace(Constants.EVAL_NS)
@RdfType("SubjectCategory")
public class SubjectCategory extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3118757153480402607L;

	private String name;
	
	private SubjectCategory subCategory;
	
	@Deprecated
	public SubjectCategory(){
	}
	
	public SubjectCategory(String name) {
		super();
		this.name = name;
	}

	public SubjectCategory(String name, SubjectCategory subCategory) {
		super();
		this.name = name;
		this.subCategory = subCategory;
	}

	@RdfProperty(Constants.DC_TERMS_NS + "title")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@RdfProperty(Constants.EVAL_NS + "hasSubCategory")
	public SubjectCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubjectCategory subCategory) {
		this.subCategory = subCategory;
	}
}
