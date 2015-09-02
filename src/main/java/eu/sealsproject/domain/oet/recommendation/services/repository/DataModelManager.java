package eu.sealsproject.domain.oet.recommendation.services.repository;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

public class DataModelManager {

	private Bean2RDF writer;
	private RDF2Bean reader;
	private Model dataSource;
	protected String dataSourceUrl;
	protected String rdfLang;
	
	public DataModelManager(String dataSourceUrl, String rdfLang, boolean multipleFiles) {
		this.dataSourceUrl = dataSourceUrl;
		this.rdfLang = rdfLang;
		if(multipleFiles)
			this.dataSource = readDataFromAllFiles(dataSourceUrl, rdfLang);
		else
			this.dataSource = readData(dataSourceUrl, rdfLang);
		this.writer = new Bean2RDF(dataSource);
		this.reader = new RDF2Bean(dataSource);
		System.out.println("Model size: " + dataSource.size());
	}
	

	public Model getDataModel()
	{
		return dataSource;
	}

	public Model readData(String dataSourceUrl, String rdfLang) {
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		model.setNsPrefix("seals", "http://www.seals-project.eu/metadata.owl#");

		
		File dataFile = new File(dataSourceUrl);

		if (dataFile.exists()) {
			try {
				System.err.println("Reading file: " + dataSourceUrl);
				model.read(new FileInputStream(dataFile.toString()),
						"http://www.seals-project.eu/metadata.owl#", rdfLang);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return model;
	}
	
	public Model readDataFromAllFiles(String dataSourceUrl, String rdfLang) {
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		
	
		URL url = Thread.currentThread().getContextClassLoader()
		.getResource(dataSourceUrl);
		String path = url.getFile();
		// remove white spaces encoded with %20
		path = path.replaceAll("%20", " ");
		
		System.err.println("Path to folder" + path);
		
		File dataFile = new File(path);
		
		if (!dataFile.exists())
			return model;
		
		File[] files = dataFile.listFiles();
		for (File file : files) {
			if(file.isFile() && file.toString().endsWith(".rdf")){
				model.add(readData(file.toString(), "RDF/XML"));				
			}
		}
		
		return model;
	}
	
	public <T extends Resource> Collection<T> loadAllResources(Class<T> clazz){
		try {
			return reader.loadDeep(clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public <T extends Resource> T loadResource(Class<T> clazz,
			String resourceUri, boolean loadDeep) {

		if ( loadDeep )
			return reader.loadDeep(clazz, resourceUri);
		else
			return reader.load(clazz, resourceUri);
	}
	
	
	public void writeModel(Resource res){
		try {
			writer.saveDeep(res);
			dataSource.write(new FileOutputStream(dataSourceUrl), rdfLang,
					"http://seals-project.eu/ns#");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public Bean2RDF getWriter() {
		return writer;
	}

	public void setWriter(Bean2RDF writer) {
		this.writer = writer;
	}

	public RDF2Bean getReader() {
		return reader;
	}

	public void setReader(RDF2Bean reader) {
		this.reader = reader;
	}
}
