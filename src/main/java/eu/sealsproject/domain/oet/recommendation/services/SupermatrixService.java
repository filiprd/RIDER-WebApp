package eu.sealsproject.domain.oet.recommendation.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.domain.Alternative;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.om.RatioScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityIndicator;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.QualityMeasure;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qmo.RankingFunction;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataService;
import eu.sealsproject.domain.oet.recommendation.util.RequirementsUtil;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;

public class SupermatrixService {

	
//	public static DataService service = new DataService();
	
	// fills the supermatrix with alternatives comparisons
	public static Matrix fillSupermatrixWithAlternatives(Matrix supermatrix, 
			LinkedList<Requirement> requirements, LinkedList<Alternative> alternatives, String algorithm,
			DataService service, boolean onlyRequirement) {
		if(onlyRequirement)
			return fillSupermatrixWithAlternativesOnlyRequirementsComparison(supermatrix, requirements, alternatives, algorithm, service);
		else
			return fillSupermatrixWithAlternativesALLComparisons(supermatrix, requirements, alternatives, algorithm, service);
	}

	// fills the supermatrix with alternatives comparisons
	public static Matrix fillSupermatrixWithAlternativesOnlyRequirementsComparison(Matrix supermatrix, 
			LinkedList<Requirement> requirements, LinkedList<Alternative> alternatives, String algorithm,
			DataService service) {
						
		int k = supermatrix.getRowDimension();
		supermatrix.extend(alternatives.size());
		
				
		for (Requirement requirement : requirements) {
			Matrix mat = ComparisonService.compareAlternatives(requirement,alternatives, algorithm, service);	
			
			// columnIndex to put the comparison
			int columnIndex = supermatrix.getMapping().
				getRowNumber(requirement.getIndicator().getUri().toString());
			supermatrix.setMatrixColumn(k, k+alternatives.size()-1, columnIndex, mat);			
		}
		
		// maps alternatives
		int toMap = k;
		for (Alternative alternative : alternatives) {					
			supermatrix.getMapping().addMapItem(new MapItem(toMap++, alternative.getId()));						
		}
		
		
		//--------------------------------
		// adds comparisons of characteristics w.r.t. alternatives
		//--------------------------------
		
		
		LinkedList<String> alreadyCompared = new LinkedList<String>();
		for (int i = 0; i < requirements.size(); i++) {
			Requirement r = requirements.get(i);
			LinkedList<Requirement> cluster = new LinkedList<Requirement>();
			cluster.add(r);
			if(alreadyCompared.contains(r.getIndicator().getQualityCharacteristic().getUri().toString()))
				continue;
			alreadyCompared.add(r.getIndicator().getQualityCharacteristic().getUri().toString());
			for (int j = i+1; j < requirements.size(); j++) {
				if(r.getIndicator().getQualityCharacteristic().getUri().
						equals(requirements.get(j).getIndicator().getQualityCharacteristic().getUri()))
					cluster.add(requirements.get(j));
			}
			if(cluster.size() == 1){
				Matrix ones = new Matrix(1,alternatives.size(),1);
				supermatrix.setMatrixRow(supermatrix.getMapping().getRowNumber(cluster.get(0).
						getIndicator().getUri().toString()), k, k+alternatives.size()-1, ones);
			}
			else{				
				for (Alternative alternative : alternatives) {			
					Matrix mat = compareCharacteristics(cluster,alternative, service);	
					
					// test
//					System.err.println("Matrix size: " + supermatrix.getRowDimension());					
//					for (MapItem item : supermatrix.getMapping().getMap()) {
//						System.err.println(item);
//					}					
//					System.err.println(alternative.getId());
					// --
					
					supermatrix.setMatrixColumn(getPositionsForClusterRequirements(supermatrix, cluster),
							supermatrix.getMapping().getRowNumber(alternative.getId()),mat);					
				}
			}
		}	
					
		
//		for (Alternative alternative : alternatives) {
//			System.out.println(alternative.getId());
//			for (ToolVersion tv : alternative.getTools()) {
//				System.out.println(tv.getName());
//			}
//		}
		
		return supermatrix;
		
	}


	// compares the contribution of characteristics w.r.t an alternative and returns weights
	public static Matrix compareCharacteristics(LinkedList<Requirement> requirements,
			Alternative alternative, DataService service) {

		int size = requirements.size();
		Matrix comparison = new Matrix(size,size);
		
		for (int i = 0; i < size; i++) {
			for (int j = i; j < size; j++) {
				if(i == j){
					comparison.set(i, j, 1);
					continue;
				}
				double result = ComparisonService.compareCharacteristics(alternative,
						requirements.get(i), requirements.get(j), service);
				comparison.set(i, j, result);
				comparison.set(j, i, 1/result);
			}
		}

		Matrix weights = comparison.getWeights();
		
		return weights;
	}


	
	// returns the array of row indexes that corresponds to the list of requirements
	private static int[] getPositionsForClusterRequirements(Matrix supermatrix,
			LinkedList<Requirement> cluster) {
		int[] indexes = new int[cluster.size()];
		int k = 0;
		for (Requirement requirement : cluster) {
			indexes[k++] = supermatrix.getMapping().getRowNumber(requirement.getIndicator().
					getUri().toString());
		}
		return indexes;
	}
	
	public static LinkedList<String> getRequirementCharacteristicsUris(LinkedList<Requirement> requirements){
		LinkedList<String> requirementsCharacteristicsUris = new  LinkedList<String>();
		for (Requirement req : requirements) {
			String uri = req.getIndicator().getQualityCharacteristic().getUri().toString();
			if(!requirementsCharacteristicsUris.contains(uri))
				requirementsCharacteristicsUris.add(uri);
		}
		return requirementsCharacteristicsUris;
	}
	
	public static LinkedList<String> getCharacteristicsUris(MatrixMapping 
			supermatrixMapping, DataService service){		
		LinkedList<String> requirementsCharacteristicsUris = new  LinkedList<String>();
		for (MapItem item : supermatrixMapping.getMap()) {
			if(item.getChracteristicUri().contains("Alternatives"))
				continue;
			String uri = service.getCharacteristicUriOfIndicator(
					item.getChracteristicUri());
			if(!requirementsCharacteristicsUris.contains(uri))
				requirementsCharacteristicsUris.add(uri);
		}
		return requirementsCharacteristicsUris;
	}

	
	/**
	 * Fills the supermatrix with alternatives, which are compared not only to requirements but to
	 * all measures in the supermatrix
	 * @param supermatrix
	 * @param requirements
	 * @param alternatives
	 * @param service
	 * @return
	 */
	public static Matrix fillSupermatrixWithAlternativesALLComparisons(Matrix supermatrix, 
			LinkedList<Requirement> requirements, LinkedList<Alternative> alternatives, String algorithm,
			DataService service) {
						
		int k = supermatrix.getRowDimension();
		supermatrix.extend(alternatives.size());
						
		for (int i = 0; i<k; i++) {
			String measureUri = supermatrix.getMapping().getCharacteristicUri(i);
			Requirement requirement = RequirementsUtil.getRequirement(requirements, measureUri, service);
			AlternativesFactory.addMeasureToAlternatives(alternatives,measureUri, service);
			Matrix mat = ComparisonService.compareAlternatives(requirement,alternatives, algorithm, service);	
						
			// columnIndex to put the comparison
			int columnIndex = supermatrix.getMapping().
				getRowNumber(requirement.getIndicator().getUri().toString());
			supermatrix.setMatrixColumn(k, k+alternatives.size()-1, columnIndex, mat);			
		}
		
		// maps alternatives
		int toMap = k;
		for (Alternative alternative : alternatives) {					
			supermatrix.getMapping().addMapItem(new MapItem(toMap++, alternative.getId()));						
		}
		
		
		//--------------------------------
		// adds comparisons of characteristics w.r.t. alternatives
		//--------------------------------
		
		
		LinkedList<String> alreadyCompared = new LinkedList<String>();
		for (int i = 0; i < k; i++) {
			String measureUri = supermatrix.getMapping().getCharacteristicUri(i);
			Requirement r = RequirementsUtil.getRequirement(requirements, measureUri, service);
			LinkedList<Requirement> cluster = new LinkedList<Requirement>();
			cluster.add(r);
			if(alreadyCompared.contains(r.getIndicator().getQualityCharacteristic().getUri().toString()))
				continue;
			alreadyCompared.add(r.getIndicator().getQualityCharacteristic().getUri().toString());
			for (int j = i+1; j < k; j++) {
				String measureUri2 = supermatrix.getMapping().getCharacteristicUri(j);
				Requirement r2 = RequirementsUtil.getRequirement(requirements, measureUri2, service);
				if(r.getIndicator().getQualityCharacteristic().getUri().
						equals(r2.getIndicator().getQualityCharacteristic().getUri()))
					cluster.add(r2);
			}
			if(cluster.size() == 1){
				Matrix ones = new Matrix(1,alternatives.size(),1);
				supermatrix.setMatrixRow(supermatrix.getMapping().getRowNumber(cluster.get(0).
						getIndicator().getUri().toString()), k, k+alternatives.size()-1, ones);
			}
			else{				
				for (Alternative alternative : alternatives) {			
					Matrix mat = compareCharacteristics(cluster,alternative, service);	
					
					supermatrix.setMatrixColumn(getPositionsForClusterRequirements(supermatrix, cluster),
							supermatrix.getMapping().getRowNumber(alternative.getId()),mat);					
				}
			}
		}					
		
		return supermatrix;
		
	}
	
}
