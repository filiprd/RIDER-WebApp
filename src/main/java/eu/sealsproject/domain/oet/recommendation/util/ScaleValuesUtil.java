package eu.sealsproject.domain.oet.recommendation.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.IntervalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.NominalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.OrdinalScale;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.OrdinalScaleItem;
import eu.sealsproject.domain.oet.recommendation.domain.ontology.qualitymodel.Scale;

public class ScaleValuesUtil {

	/**
	 * Fills the values object with possible values of the scale
	 * @param scale
	 * @param values
	 */
	public static void getScaleValues(Scale scale, Collection<String> values){
		if(scale instanceof OrdinalScale){
			OrdinalScale ordinalScale = OrdinalScale.class.cast(scale);
			for (OrdinalScaleItem scaleItem : ordinalScale.getOrdinalScaleItems()) {
				values.add(scaleItem.getName());
			}
		}
		if(scale instanceof NominalScale){
			NominalScale nominalScale = NominalScale.class.cast(scale);
			for (String label : nominalScale.getLabels()) {
				values.add(label);
			}
		}
	}

	/**
	 * Returns the list of values for the interval scale, from the lower to higher divided in 20
	 * points
	 * @param scale
	 * @return
	 */
	public static String getIntervalScaleValues(Scale scale) {
		Collection<String> values = new LinkedList<String>();
		IntervalScale interval = (IntervalScale)scale;
		
		double lower = Double.valueOf(interval.getLowerBoundry());
		double higher = Double.valueOf(interval.getUpperBoundry());
		
		values.add(String.valueOf(lower));
		
		
		NumberFormat format = NumberFormat.getInstance(Locale.UK);
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		
		double distance = (higher-lower)/20;
		double step = lower;
		for (int i = 0; i < 20; i++) {
			values.add(format.format(step+distance));
			step = step + distance;
		}
		
		String literal = "";
		for (String string : values) {
			literal = literal + string + ",";
		}
		return literal.substring(0, literal.length()-1);
	}
	

}
