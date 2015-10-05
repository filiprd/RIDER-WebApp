package eu.sealsproject.domain.oet.recommendation.services;

import java.util.LinkedList;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.EvaluationSubject;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.SubjectCategory;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;

public class AlternativesFactory {
	

	public static LinkedList<Alternative> createAlternativesList(LinkedList<Requirement> requirements, DataService service){
		LinkedList<SubjectCategory> subjectCategories = new LinkedList<SubjectCategory>();
		LinkedList<Alternative> alternatives = new LinkedList<Alternative>();
		
		for (Requirement requirement : requirements) {
			SubjectCategory subjectCategory = service.getSubjectCategory(requirement.getIndicator().getUri().toString());
			if(!subjectCategories.contains(subjectCategory))
				subjectCategories.add(subjectCategory);
		}
		
		if(subjectCategories.size() == 1){
			for (EvaluationSubject evaluationSubject : service.getEvaluationSubjects(subjectCategories.get(0).getUri().toString())) {	
//				evaluationSubject.setQualityValues(service.getQualityValuesForRequirements(evaluationSubject.getUri().toString(),
//						requirements));
				Alternative alternative = new Alternative();
				alternative.addEvaluationSubject(evaluationSubject);
				alternatives.add(alternative);
			}
		}
		
		if(subjectCategories.size() == 2){
			for (EvaluationSubject evaluationSubject1 : service.getEvaluationSubjects(subjectCategories.get(0).getUri().toString())) {	
//				evaluationSubject1.setQualityValues(service.getQualityValuesForRequirements(evaluationSubject1.getUri().toString(),
//						requirements));
				for (EvaluationSubject evaluationSubject2 : service.getEvaluationSubjects(subjectCategories.get(1).getUri().toString())) {
//					evaluationSubject2.setQualityValues(service.getQualityValuesForRequirements(evaluationSubject2.getUri().toString(),
//							requirements));
					Alternative alternative = new Alternative();
					alternative.addEvaluationSubject(evaluationSubject1);
					alternative.addEvaluationSubject(evaluationSubject2);
					alternatives.add(alternative);
				}
			}
		}
		
		if(subjectCategories.size() == 3){
			for (EvaluationSubject evaluationSubject1 : service.getEvaluationSubjects(subjectCategories.get(0).getUri().toString())) {	
//				evaluationSubject1.setQualityValues(service.getQualityValuesForRequirements(evaluationSubject1.getUri().toString(),
//						requirements));
				for (EvaluationSubject evaluationSubject2 : service.getEvaluationSubjects(subjectCategories.get(1).getUri().toString())) {
//					evaluationSubject2.setQualityValues(service.getQualityValuesForRequirements(evaluationSubject2.getUri().toString(),
//							requirements));
					for (EvaluationSubject evaluationSubject3 : service.getEvaluationSubjects(subjectCategories.get(2).getUri().toString())) {
//						evaluationSubject3.setQualityValues(service.getQualityValuesForRequirements(evaluationSubject3.getUri().toString(),
//								requirements));
						Alternative alternative = new Alternative();
						alternative.addEvaluationSubject(evaluationSubject1);
						alternative.addEvaluationSubject(evaluationSubject2);
						alternative.addEvaluationSubject(evaluationSubject3);
						alternatives.add(alternative);
					}
				}
			}
		}
		return alternatives;
	}

	
	// this method is called in the case when the comparison according to all criteria is needed 
	// (not only w.r.t. requirements), because alternatives and measures are initially formed based on
	// user requirements only
	/**
	 * Adds quality values for a given quality measure to all alternatives in a given list.
	 * @param alternatives
	 * @param measureUri
	 */
	public static void addMeasureToAlternatives(LinkedList<Alternative> alternatives, String measureUri, DataService service) {
		
		for (Alternative alternative : alternatives) {
			for (EvaluationSubject evaluationSubject : alternative.getEvaluationSubjects()) {
				if(service.coversQualityIndicator(evaluationSubject.getUri().toString(), measureUri))
					evaluationSubject.addQualityValue(service.getQualityValue(evaluationSubject.getUri().toString(), 
							measureUri));
			}
		}
		
	}
}
