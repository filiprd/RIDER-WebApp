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
			Matrix mat = compareAlternatives(requirement,alternatives, algorithm, service);	
			
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

	// compares alternatives w.r.t characteristic and returns weights
	public static Matrix compareAlternatives(Requirement requirement,
			LinkedList<Alternative> alternatives, String algorithm, DataService service) {
		
		int size = alternatives.size();
		Matrix comparison = new Matrix(size,size);
		
		for (int i = 0; i < size; i++) {
			for (int j = i; j < size; j++) {
				if(i == j){
					comparison.set(i, j, 1);
					continue;
				}
				
				
				Method comparator;
				double result = -1;
				try {
					Class<?> clazz = Class.forName("eu.sealsproject.domain.oet.recommendation.services.ComparisonService");
					Object inst = clazz.newInstance();
					comparator = clazz.getMethod(algorithm + "Comparison", Alternative.class, Alternative.class, Requirement.class,
									DataService.class);
					Object obj = comparator.invoke(inst, alternatives.get(i), alternatives.get(j), requirement, service);
					result = Double.parseDouble(obj.toString());
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.getCause().printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				double result = ComparisonService.compareAlternativesMaxDistance(alternatives.get(i), 
//						alternatives.get(j),requirement, service);
				
				if(result == -1)
					throw new RuntimeException("There was an error in ivoking comparion algorithm "
							+ "when comparing " + alternatives.get(i).getId() + " to " + alternatives.get(j).getId() + 
							" with respect to " + requirement.getIndicator().getName());
				
				comparison.set(i, j, result);
				comparison.set(j, i, 1/result);
			}
		}
		
		
//		for (int i = 0; i < comparison.getRowDimension(); i++) {
//			for (int j = 0; j < comparison.getColumnDimension(); j++) {
//				if(comparison.get(i, j)<0){
//					System.out.println(comparison.get(i, j));
//					System.out.println(comparison.get(j, i));
//					System.out.println("");
//				}
//			}
//		}
		
//		System.out.println("Comparison matrix");
//		comparison.print(1, 3);
		
//		if(comparison.getRowDimension() != comparison.getColumnDimension())
//			System.err.println("DIFFERENT DIMMENSIONS");
		
//		System.out.println("");
		
//		System.out.println("Weights matrix");		
		Matrix weights = comparison.getWeights();
//		weights.print(1, 3);
		
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
			Requirement requirement = getRequirement(requirements, measureUri, service);
			AlternativesFactory.addMeasureToAlternatives(alternatives,measureUri, service);
			Matrix mat = compareAlternatives(requirement,alternatives, algorithm, service);	
						
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
			Requirement r = getRequirement(requirements, measureUri, service);
			LinkedList<Requirement> cluster = new LinkedList<Requirement>();
			cluster.add(r);
			if(alreadyCompared.contains(r.getIndicator().getQualityCharacteristic().getUri().toString()))
				continue;
			alreadyCompared.add(r.getIndicator().getQualityCharacteristic().getUri().toString());
			for (int j = i+1; j < k; j++) {
				String measureUri2 = supermatrix.getMapping().getCharacteristicUri(j);
				Requirement r2 = getRequirement(requirements, measureUri2, service);
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
	
	
	/**
	 * Returns the requirement that is related to a given measure. If the measure is not in the requirements list
	 * a new requirement is created for that measure with the threshold equals to the best value, 
	 * so that a comparison can be performed.
	 * @param requirements
	 * @param measureUri
	 * @param service
	 * @return
	 */
	private static Requirement getRequirement(LinkedList<Requirement> requirements, String measureUri, DataService service){
		for (Requirement requirement : requirements) {
			if(requirement.getIndicator().getUri().toString().equals(measureUri))
				return requirement;
		}

		QualityIndicator indicator = service.getQualityIndicatorObject(measureUri);
		if(indicator.getScale().getClass().getSimpleName().equalsIgnoreCase("RatioScale")){
			RatioScale scale = (RatioScale) indicator.getScale();
			String threshold = "";
			if(scale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
				threshold = "1";
			if(scale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
				threshold = "0";
			return new Requirement(indicator,threshold);						
		}
		if(indicator.getScale().getClass().getSimpleName().equalsIgnoreCase("IntervalScale")){
			IntervalScale scale = (IntervalScale) indicator.getScale();
			String threshold = "";
			if(scale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
				threshold = String.valueOf(scale.getUpperBoundary());
			if(scale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
				threshold = String.valueOf(scale.getLowerBoundary());
			return new Requirement(indicator,threshold);								
		}
		return null;
	}
}
