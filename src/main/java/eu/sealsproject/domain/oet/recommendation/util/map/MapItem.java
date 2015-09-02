package eu.sealsproject.domain.oet.recommendation.util.map;

import java.io.Serializable;

/**
 * An item in the matrix mapping. It contains the characteristic Uri that an entry in the
 * matrix represents, and its position in the matrix
 * @author Filip
 *
 */
public class MapItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 838475341076157960L;

	public MapItem(int rowNumber, String chracteristicUri) {
		super();
		this.rowNumber = rowNumber;
		this.chracteristicUri = chracteristicUri;
	}

	/**
	 * Row number of the characteristic
	 */
	private int rowNumber;
	
	/**
	 * Uri of the characteristics 
	 */
	private String chracteristicUri;

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getChracteristicUri() {
		return chracteristicUri;
	}

	public void setChracteristicUri(String chracteristicUri) {
		this.chracteristicUri = chracteristicUri;
	}
	
	public String toString(){
		return "Row: " + this.rowNumber + " ch: " + this.getChracteristicUri();
	}
}
