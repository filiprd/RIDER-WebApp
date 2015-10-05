package eu.sealsproject.domain.oet.recommendation.services.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import eu.sealsproject.domain.oet.recommendation.config.Constants;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.EvaluationSubject;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.QualityValue;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.SubjectCategory;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityCharacteristic;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityIndicator;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityMeasure;
import eu.sealsproject.domain.oet.recommendation.services.repository.datalookup.OntModelQueryService;
import eu.sealsproject.domain.oet.recommendation.services.repository.ontology.AbstractServiceImpl;
import eu.sealsproject.domain.oet.recommendation.util.comparators.QualityCharacteristicComparator;
import eu.sealsproject.domain.oet.recommendation.util.comparators.QualityMeasureComparator;

public class DataService extends AbstractServiceImpl {
		
	private OntModelQueryService queryService;

	public DataService() {
		super();
		this.queryService = new OntModelQueryService();
	}

	public void setQueryService(OntModelQueryService queryService) {
		this.queryService = queryService;
	}
		
	
	/**
	 * Checks if the evaluation subject covers the specific quality measure
	 * @param evaluationSubjectUri
	 * @param qualityIndicatorUri
	 * @return
	 */
	public boolean coversQualityIndicator(String evaluationSubjectUri,
			String qualityIndicatorUri) {

		String queryString = 		
			"PREFIX eval: <"+Constants.EVAL_NS+">\n " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?qvalue \n" +
			"WHERE {\n" +
			"      ?qvalue rdf:type eval:QualityValue; \n" +
			"      eval:forMeasure <"+qualityIndicatorUri+">; \n" +
			"      eval:obtainedFrom ?evaluation. \n" +
			
			"      ?evaluation rdf:type eval:Evaluation; \n" +
			"      eval:evaluatedSubject <"+evaluationSubjectUri+">; \n" +

			"      }";

//		System.out.println(queryString);

		Collection<String> qualityValueUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "qvalue",
						getDataModel());

		if(qualityValueUris.size() != 0)
			return true;
		return false;

	}

	
	/**
	 * Returns the the quality measure of the specific evaluation subject
	 * @param evaluationSubjectUri
	 * @param qualityIndicatorUri
	 * @return
	 */
	public QualityValue getQualityValue(String evaluationSubjectUri,
			String qualityIndicatorUri) {

		String queryString = 		
				"PREFIX eval: <"+Constants.EVAL_NS+">\n " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"SELECT DISTINCT ?qvalue \n" +
				"WHERE {\n" +
				"      ?qvalue rdf:type eval:QualityValue; \n" +
				"      eval:forMeasure <"+qualityIndicatorUri+">; \n" +
				"      eval:obtainedFrom ?evaluation. \n" +
				
				"      ?evaluation rdf:type eval:Evaluation; \n" +
				"      eval:evaluatedSubject <"+evaluationSubjectUri+">; \n" +

				"      }";

		Collection<String> qualityValueUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "qvalue",
						getDataModel());
		
		Collection<QualityValue> col = null;
		try {
			col = loadResourcesByURIs(QualityValue.class,
					qualityValueUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(col.isEmpty())
			return null;
		
		return col.iterator().next();
		
	}

	
	/**
	 * Returns the Uri of the quality characteristic that is measured by the quality measure
	 * @param qualityIndicatorUri Uri of the quality measure
	 * @return
	 */
	public String getCharacteristicUriOfIndicator(String qualityIndicatorUri) {
		
		if(qualityIndicatorUri.contains("Alternatives"))
			return "Alternatives";
		
		String queryString = 
			"PREFIX eval: <"+Constants.EVAL_NS+">\n " +
			"PREFIX qmo: <"+Constants.QMO_NS+">\n " +			
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri \n" +
			"WHERE {\n" +
			"      <"+qualityIndicatorUri+"> rdf:type qmo:QualityIndicator; \n" +
			"      qmo:measuresCharacteristic ?uri. \n" +
			"      }";

//		System.out.println(queryString);

		Collection<String> qualityMeasureUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		return qualityMeasureUris.iterator().next();
		
	}

	
	/**
	 * Returns the subject category of which evaluation subjects are evaluated with the quality measure
	 * @param qualityIndicatorUri Uri of the quality measure
	 * @return
	 */
	public SubjectCategory getSubjectCategory(String qualityIndicatorUri) {
		
		String queryString = 
			"PREFIX qmo: <"+Constants.QMO_NS+">\n " +
			"PREFIX eval: <"+Constants.EVAL_NS+">\n " +			
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?subjectCategoryUri \n" +
			"WHERE {\n" +
			"      ?qvalue rdf:type eval:QualityValue; \n" +
			"      eval:forMeasure <"+qualityIndicatorUri+">; \n" +
			"      eval:obtainedFrom ?evaluation. \n" +
			
			"      ?evaluation rdf:type eval:Evaluation; \n" +
			"      eval:evaluatedSubject ?evaluationSubject. \n" +
			
			"      ?evaluationSubject rdf:type eval:EvaluationSubject; \n" +
			"      eval:belongsToCategory ?subjectCategoryUri. \n" +

			"      }";

//		System.out.println(queryString);

		Collection<String> subjectCategoryUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "subjectCategoryUri",
						getDataModel());

		
		Collection<SubjectCategory> col = null;
		try {
			col = loadResourcesByURIs(SubjectCategory.class,
					subjectCategoryUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(!col.isEmpty())
			return col.iterator().next();
		return null;
	}

	
	/**
	 * Returns all evaluation subjects that belong to a subject category
	 * @param subjectCategoryUri Uri of the subject category
	 * @return
	 */
	public Collection<EvaluationSubject> getEvaluationSubjects(String subjectCategoryUri) {

		String queryString = 
			"PREFIX qmo: <"+Constants.QMO_NS+">\n " +
			"PREFIX eval: <"+Constants.EVAL_NS+">\n " +					
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?evaluationSubjectUri \n" +
			"WHERE {\n" +
			
			"      ?evaluationSubjectUri rdf:type eval:EvaluationSubject; \n" +
			"      eval:belongsToCategory <"+subjectCategoryUri+">. \n" +

			"      }";

//		System.out.println(queryString);

		Collection<String> evaluationSubjectUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "evaluationSubjectUri",
						getDataModel());
		
		Collection<EvaluationSubject> col = null;
		try {
			col = loadResourcesByURIs(EvaluationSubject.class,
					evaluationSubjectUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return col;

	}
	
	
	/**
	 * Returns all quality characteristics of which evaluated quality measures exist
	 * @return
	 */
	public Collection<QualityCharacteristic> getAllQualityCharacteristics() {		
		String queryString = 
			"PREFIX qmo: <"+Constants.QMO_NS+">\n " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri " +
			"WHERE {\n" +
			"      ?uri rdf:type qmo:QualityCharacteristic. \n" +
			"      }";

//		System.out.println(queryString);
		
		Collection<String> characteristicsUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		Collection<QualityCharacteristic> col = null;
		try {
			col = loadResourcesByURIs(QualityCharacteristic.class,
					characteristicsUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Collections.sort((List<QualityCharacteristic>) col, new QualityCharacteristicComparator());
		
		for (QualityCharacteristic qualityCharacteristic : col) {
			qualityCharacteristic.setQualityIndicators(getQualityIndicatorsForCharacteristic(
					qualityCharacteristic.getUri().toString()));
			
			Collections.sort((List<QualityIndicator>) qualityCharacteristic.getQualityIndicators(), 
					new QualityMeasureComparator());
		}
		
		System.out.println(col.iterator().next().getSubjectCategory().getName());
		
		return col;
	}
	
		
	public Collection<QualityIndicator> getQualityIndicatorsForCharacteristic(String characteristicUri) {
		
		String queryString = 
			"PREFIX qmo: <"+Constants.QMO_NS+">\n " +			
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri \n" +
			"WHERE {\n" +
			"      ?uri rdf:type qmo:QualityIndicator; \n" +
			"      qmo:measuresCharacteristic <"+ characteristicUri + ">. \n" +
			"      }";

//		System.out.println(queryString);

		Collection<String> qualityIndicatorUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());
		
		Collection<QualityIndicator> col = null;
		try {
			col = loadResourcesByURIs(QualityIndicator.class,
					qualityIndicatorUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return col;
		
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	public boolean isEvaluated(String qualityIndicatorUri) {
		if(getSubjectCategory(qualityIndicatorUri)==null)
			return false;
		return true;
	}
	
	
	//-------------------------------------------
	// OLD METHODS
	//-------------------------------------------
	
	/**
	 * Returns all subject categories that are evaluated
	 * @return
	 */
	public Collection<SubjectCategory> getSubjectCategories() {		
		String queryString = 
			"PREFIX eval: <"+Constants.EVAL_NS+">\n " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri " +
			"WHERE {\n" +
			"      ?uri rdf:type eval:SubjectCategory. \n" +
			"      }";

//		System.out.println(queryString);

		Collection<String> toolCategoriUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		Collection<SubjectCategory> col = null;
		try {
			col = loadResourcesByURIs(SubjectCategory.class,
					toolCategoriUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		LinkedList<ToolCategory> sorted = new LinkedList<ToolCategory>();
//		for (ToolCategory toolCategory : col) {
//			if(toolCategory.getUri().toString().equalsIgnoreCase(ToolCategory.ONTOLOGY_ENGINEERING_TOOL.getUri().toString()))
//				sorted.addFirst(toolCategory);
//			else
//				sorted.addLast(toolCategory);
//		}

		return col;
	}
	
	
	public EvaluationSubject getEvaluationSubjectObject(String evaluationSubjectUri) {
		EvaluationSubject evaluationSubject = null;
		try {
			evaluationSubject = loadResourceByURI(EvaluationSubject.class,
					evaluationSubjectUri, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return evaluationSubject;
	}
	
	
	public Collection<QualityValue> getResultsForEvaluationSubject(String evaluationSubjectUri) {
		String queryString = 
			"PREFIX eval: <"+Constants.EVAL_NS+">\n " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri " +
			"WHERE {\n" +
			"      ?uri rdf:type eval:QualityValue; \n" +
			"      eval:obtainedFrom ?evaluation. \n" +
			
			"      ?evaluation rdf:type eval:Evaluation; \n" +
			"      eval:evaluatedSubject <"+ evaluationSubjectUri +">. \n" +
			"      }";

//		System.out.println(queryString);

		Collection<String> qualityValuesUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		Collection<QualityValue> col = null;
		try {
			col = loadResourcesByURIs(QualityValue.class,
					qualityValuesUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	return col;
	
}

	
	public Collection<QualityIndicator> getQualityIndicatorsForSubjectCategory(String subjectCategoryUri) {
		String queryString = 
				"PREFIX qmo: <"+Constants.QMO_NS+">\n " +
				"PREFIX eval: <"+Constants.EVAL_NS+">\n " +			
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"SELECT DISTINCT ?uri \n" +
				"WHERE {\n" +
				"      ?qvalue rdf:type eval:QualityValue; \n" +
				"      eval:forMeasure ?uri; \n" +
				"      eval:obtainedFrom ?evaluation. \n" +
				
				"      ?uri rdf:type qmo:QualityIndicator; \n" +
				
				"      ?evaluation rdf:type eval:Evaluation; \n" +
				"      eval:evaluatedSubject ?evaluationSubject. \n" +
				
				"      ?evaluationSubject rdf:type eval:EvaluationSubject; \n" +
				"      eval:belongsToCategory <"+subjectCategoryUri+">. \n" +

				"      }";
//		System.out.println(queryString);
		
		Collection<String> measuresUri = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		Collection<QualityIndicator> col = null;
		try {
			col = loadResourcesByURIs(QualityIndicator.class,
					measuresUri, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Collections.sort((List<QualityIndicator>) col, new QualityMeasureComparator());
						
		return col;
	}

	
	public Collection<QualityValue> getQualityValuesForRequirements(
			String evaluationSubjectUri, LinkedList<Requirement> requirements) {

		LinkedList<QualityValue> values = new LinkedList<QualityValue>();
		
		for (Requirement requirement : requirements) {
			QualityValue value = getQualityValue(evaluationSubjectUri, requirement.getIndicator().getUri().toString());
			if(value != null)
				values.add(value);
		}
		
		return values;
	}
	
	
	public Collection<String> getQualityIndicatorUrisForSubjectCategory(String subjectCategoryUri) {
		String queryString = 
				"PREFIX qmo: <"+Constants.QMO_NS+">\n " +
				"PREFIX eval: <"+Constants.EVAL_NS+">\n " +			
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"SELECT DISTINCT ?uri \n" +
				"WHERE {\n" +
				"      ?qvalue rdf:type eval:QualityValue; \n" +
				"      eval:forMeasure ?uri; \n" +
				"      eval:obtainedFrom ?evaluation. \n" +
				
				"      ?uri rdf:type qmo:QualityIndicator; \n" +
				
				"      ?evaluation rdf:type eval:Evaluation; \n" +
				"      eval:evaluatedSubject ?evaluationSubject. \n" +
				
				"      ?evaluationSubject rdf:type eval:EvaluationSubject; \n" +
				"      eval:belongsToCategory <"+subjectCategoryUri+">. \n" +

				"      }";

//		System.out.println(queryString);
		
		Collection<String> indicatorUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

								
		return indicatorUris;
	}

	
	public QualityIndicator getQualityIndicatorObject(String qualityIndicatorUri) {
		QualityIndicator m = null;
		try {
			m = loadResourceByURI(QualityIndicator.class,
					qualityIndicatorUri, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
								
		return m;
	}
	
	
	public double getResultsDifference(String qualityIndicatorUri) {
		String queryString = 
				"PREFIX eval: <"+Constants.EVAL_NS+">\n " +
				"PREFIX qmo: <"+Constants.QMO_NS+">\n " +	
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"SELECT DISTINCT ?result \n" +
				"WHERE {\n" +
				"      ?qv a eval:QualityValue; \n" +
				"      eval:hasLiteralValue ?result; \n" +
				"      eval:forMeasure <"+qualityIndicatorUri+">. \n"    +
				"      <"+qualityIndicatorUri+"> rdf:type qmo:QualityIndicator. \n" +
				"      }";

//		System.out.println(queryString);

			Collection<String> results = queryService
					.executeOneVariableSelectSparqlQuery(queryString, "result",
							getDataModel());

			double min = 100;
			double max = 0;
			for (String res : results) {
				double r = Double.parseDouble(res);
				if(r>max)
					max=r;
				if(r<min)
					min=r;
			}
					
		return max-min;
	}

	
	public Collection<QualityIndicator> getAllQualityIndicators() {
		String queryString = 
				"PREFIX qmo: <"+Constants.QMO_NS+">\n " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"SELECT DISTINCT ?uri " +
				"WHERE {\n" +
				"      ?uri rdf:type qmo:QualityIndicator. \n" +
				"      }";

//			System.out.println(queryString);
			
			Collection<String> qualityIndicatorUris = queryService
					.executeOneVariableSelectSparqlQuery(queryString, "uri",
							getDataModel());

			Collection<QualityIndicator> col = null;
			try {
				col = loadResourcesByURIs(QualityIndicator.class,
						qualityIndicatorUris, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return col;
	}

	
	public QualityValue getQualityValueForAlternative(Alternative alternative,
			String qualityIndicatorUri) {
		
		Collection<EvaluationSubject> evaluationSubjects = alternative.getEvaluationSubjects();
		for (EvaluationSubject evaluationSubject : evaluationSubjects) {
			if(coversQualityIndicator(evaluationSubject.getUri().toString(), qualityIndicatorUri))
				return getQualityValue(evaluationSubject.getUri().toString(), qualityIndicatorUri);
		}
		return null;
	}


}
