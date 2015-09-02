package eu.sealsproject.domain.oet.recommendation.util.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * This class contains mappings of matrix rows into quality characteristics
 * @author Filip
 *
 */
public class MatrixMapping implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3522378426466888037L;

	private List<MapItem> map = new LinkedList<MapItem>();
	
	/**
	 * The id of the pairwise comparison map, which is the same as the id of the matrix to which the 
	 * map belongs too
	 */
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<MapItem> getMap() {
		return map;
	}

	public void setMap(List<MapItem> map) {
		this.map = map;
	}
	
	public boolean addMapItem(MapItem item){
		if(getMap().contains(item))
			return false;
		getMap().add(item);
		return true;
	}
	
	/**
	 * Returns the characteristic Uri which corresponds to the row in the matrix
	 * @param row
	 * @return
	 */
	public String getCharacteristicUri(int row){
		for (MapItem m : map) {
			if(m.getRowNumber() == row)
				return m.getChracteristicUri();
		}
		return null;
	}
	
	/**
	 * Returns the row number in the matrix which corresponds to the characteristic Uri 
	 * @param characteristicUri
	 * @return
	 */
	public int getRowNumber(String characteristicUri){
		for (MapItem m : map) {
			if(m.getChracteristicUri().equals(characteristicUri))
				return m.getRowNumber();
		}
		return -1;
	}
	
	/**
	 * Returns the characteristics positions in the matrix
	 * @param characteristicUris
	 * @return
	 */
	public int[] getCharacteristicsIndices(List<String> characteristicUris){
		int[] indexes = new int[characteristicUris.size()];
		int i = 0;
		for (String uri : characteristicUris) {
			indexes[i++] = getRowNumber(uri);
		}
		return indexes;
	}
	
	/**
	 * Returns the dependence matrix mapping. This mapping is predefined and stored in an xml file.
	 * @return
	 */
	public static MatrixMapping loadSupermatrixMap(){
		return loadMappinFromFile("matrixmappings/SupermatrixMapping.xml", "http://www.seals-project.eu/supermatrix");
	}
	
	/**
	 * Returns the cluster matrix mapping. This mapping is predefined and stored in an xml file.
	 * @return
	 */
	public static MatrixMapping loadClusterMatrixMap(){
		return loadMappinFromFile("matrixmappings/clusterMatrixMapping.xml", "http://www.seals-project.eu/clustermatrix");
	}
	
	/**
	 * Protected method, called only from the method inside a class when loading dependence or 
	 * cluster mapping.
	 * @param fileName
	 * @param id
	 * @return
	 */
	protected static MatrixMapping loadMappinFromFile(String fileName, String id){
		Properties characteristics = new Properties();
		InputStream in;
		
		URL url = Thread.currentThread().getContextClassLoader()
		.getResource(fileName);
		String path = url.getFile();
		// remove white spaces encoded with %20
		path = path.replaceAll("%20", " ");
		
		File dataFile = new File(path);
		
		try {
			in = new FileInputStream(dataFile);
			characteristics.loadFromXML(in);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MatrixMapping pm = new MatrixMapping();
		Set<Object> keys =  characteristics.keySet();
		for (Object key : keys) {
			pm.addMapItem(new MapItem(Integer.parseInt(characteristics.get(key).toString()), 
					key.toString()));
		}
		
		pm.setId(id);
		return pm;
	}
}
