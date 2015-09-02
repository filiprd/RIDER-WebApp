package eu.sealsproject.domain.oet.recommendation.tapestry.pages;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.domain.Requirement;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;

public class ImportanceExplanation {
	
	@InjectPage
	Recommendation recommendation;
	
	@Persist
	@Property
	LinkedList<LinkedList<String>> influenceList;
	
	@Persist
	@Property
	LinkedList<String> influences;
	
	@Persist
	@Property
	LinkedList<String> qualityMeasures;
	
	@Property
	String measure;
	
	@Property
	String influence;
	
	Matrix matrix;
	
	@Persist
	LinkedList<Requirement> requirements;
	
	@Persist
	LinkedList<String> importances;

	public ImportanceExplanation explain(LinkedList<Requirement> requirements, Matrix weightedMatrix, int alternativesSize) {
		this.requirements = requirements;
		influenceList = new LinkedList<LinkedList<String>>();
		qualityMeasures = new LinkedList<String>();
		MatrixMapping mapping = weightedMatrix.getMapping();
		int size = weightedMatrix.getRowDimension() - alternativesSize;
		this.matrix = weightedMatrix.getMatrix(0, size, 0, size);
		for (int i = 0; i < size; i++) {
			qualityMeasures.add(mapping.getCharacteristicUri(i));
			LinkedList<String> row = new LinkedList<String>();
			for (int j = 0; j < size; j++) {
				row.add(String.valueOf(matrix.get(i, j)));
			}
			influenceList.add(row);
		}
		return this;		
	}
	
	public String getName(String measureUri){
		String name = measureUri.split("#")[1];
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < name.length()-1; i++) {
			buffer.append(name.charAt(i));
			if(Character.isUpperCase(name.charAt(i+1)))
				buffer.append(" ");
		}
		buffer.append(name.charAt(name.length()-1));
		return buffer.toString();
	}
	
	public String getMeasureName(LinkedList<String> listOfInfluences){
		int position = influenceList.indexOf(listOfInfluences);
		return getName(qualityMeasures.get(position));
	}
	
	public String getMeasureUri(LinkedList<String> listOfInfluences){
		int position = influenceList.indexOf(listOfInfluences);
		return qualityMeasures.get(position);
	}
	
	
	public String decimals(String value){
		if(value.equalsIgnoreCase("0.0"))
			return "0";
		if(value.equalsIgnoreCase("1.0"))
			return "1";
		NumberFormat format = NumberFormat.getInstance(Locale.UK);
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		return format.format(Double.parseDouble(value.toString()));
	}
	
	public boolean isRequirement(LinkedList<String> listOfInfluences){
		String measureUri = getMeasureUri(listOfInfluences);
		for (Requirement req : requirements) {
			if(req.getMeasure().getUri().toString().equalsIgnoreCase(measureUri))
				return true;
		}
		return false;
	}

	public void setImportance(LinkedList<String> importances) {
		this.importances = importances;		
	}
	
	public String getImportance(LinkedList<String> listOfInfluences){
		String measureUri = getMeasureUri(listOfInfluences);
		for (Requirement req : requirements) {
			if(req.getMeasure().getUri().toString().equalsIgnoreCase(measureUri))
				return importances.get(requirements.indexOf(req));
		}
		return "0";
	}

}
