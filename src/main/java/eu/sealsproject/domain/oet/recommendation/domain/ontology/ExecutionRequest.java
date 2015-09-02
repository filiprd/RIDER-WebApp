package eu.sealsproject.domain.oet.recommendation.domain.ontology;

import java.net.URI;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;

@Namespace(Constants.SEALS_METADATA_NS)
@RdfType("ExecutionRequest")
public class ExecutionRequest extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6542994318204497705L;
	
	private ToolVersion evaluatesTool;
	
	/**
	 * This construction is deprecated. Use EvaluationRequest(ToolVersion evaluatesTool)
	 */
	@Deprecated
	public ExecutionRequest(){
		super();
	}
	
	/**
	 * This construction is deprecated. Use EvaluationRequest(ToolVersion evaluatesTool)
	 */
	@Deprecated
	public ExecutionRequest(String string) {
		super(string);
	}

	public ExecutionRequest(URI uri, ToolVersion evaluatesTool) {
		this.uri = uri;
		this.evaluatesTool = evaluatesTool;
	}

	@RdfProperty(Constants.SEALS_METADATA_NS + "evaluatesTool")
	public ToolVersion getEvaluatesTool() {
		return evaluatesTool;
	}

	public void setEvaluatesTool(ToolVersion evaluatesTool) {
		this.evaluatesTool = evaluatesTool;
	}
}
