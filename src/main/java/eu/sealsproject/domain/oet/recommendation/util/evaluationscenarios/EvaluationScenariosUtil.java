package eu.sealsproject.domain.oet.recommendation.util.evaluationscenarios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class EvaluationScenariosUtil {

	String dataSourceUrl = "META-INF/eu/sealsproject/domain/oet/recommendation/core/scenarios.properties";
	
	Properties properties;
	
	public EvaluationScenariosUtil(){
		URL url = Thread.currentThread().getContextClassLoader()
		.getResource(dataSourceUrl);
		String path = url.getFile();
		// remove white spaces encoded with %20
		path = path.replaceAll("%20", " ");		
		File dataFile = new File(path);
		properties = new Properties();
		try {
			properties.load(new FileInputStream(dataFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getUrl(String qualityMeasureUri){
		return properties.getProperty(qualityMeasureUri);
	}
	
	
}
