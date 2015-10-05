package eu.sealsproject.domain.oet.recommendation.util.comparators;

import java.util.Comparator;

import com.ibm.icu.text.UTF16.StringComparator;

import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.eval.SubjectCategory;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;


public class RequirementsComparator implements Comparator<Requirement>{
	
	
	private DataService service;
	
	public RequirementsComparator(DataService service){
		this.service = service;
	}
	
	public int compare(Requirement r1, Requirement r2) {
		
		boolean evaluated1 = service.isEvaluated(r1.getIndicator().getUri().toString());
		boolean evaluated2 = service.isEvaluated(r2.getIndicator().getUri().toString());
		
		if(evaluated1 && !evaluated2)
			return -20;
		
		if(!evaluated1 && evaluated2)
			return 20;

		SubjectCategory category1 = r1.getIndicator().getQualityCharacteristic().getSubjectCategory();
		SubjectCategory category2 = r2.getIndicator().getQualityCharacteristic().getSubjectCategory();
		
		return new StringComparator().compare(category1.getName(), category2.getName());
		
	}
}
