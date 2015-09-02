package eu.sealsproject.domain.oet.recommendation.services.repository.ontology;

import java.util.Collection;
import java.util.LinkedList;

import com.hp.hpl.jena.rdf.model.Model;

import eu.sealsproject.domain.oet.recommendation.domain.general.Resource;
import eu.sealsproject.domain.oet.recommendation.services.repository.DataModelManager;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

public class AbstractServiceImpl
{

	protected DataModelManager dataModel;
	
	
	public AbstractServiceImpl() {
		System.out.println("----------------------INITIALIZING SERVICE");
		this.dataModel = new DataModelManager("data/", "RDF/XML", true);
	}

	/**
	 * @return the dataModel
	 */
	protected Model getDataModel()
	{
		return dataModel.getDataModel();
	}

	/**
	 * @return the reader
	 */
	protected RDF2Bean getRdf2BeanBinding()
	{
		return dataModel.getReader();
	}

	/**
	 * @return the writer
	 */
	protected Bean2RDF getBean2RDFBinding()
	{
		return dataModel.getWriter();
	}


	/*
	 * (non-Javadoc)
	 * @see org.intelleo.services.ontologies.AbstractService#loadAllResources(java.lang.Class, boolean)
	 */
	public <T extends Resource> Collection<T> loadAllResources(
			Class<T> clazz, boolean deep) throws Exception
	{
		Collection<T> resources = new LinkedList<T>();

		try {
			if (deep) {
				resources = getRdf2BeanBinding().loadDeep(clazz);
			} else {
				resources = getRdf2BeanBinding().load(clazz);
			}

		} catch (Exception e) {
			throw e;
		}

		return resources;
	}


	/*
	 * (non-Javadoc)
	 * @see org.intelleo.services.ontologies.AbstractService#loadResourcesByURIs(java.lang.Class, java.util.Collection, boolean)
	 */
	public <T extends Resource> Collection<T> loadResourcesByURIs(
			Class<T> clazz, Collection<String> resourceURIs, boolean deep)
			throws Exception
	{
		Collection<T> resources = new LinkedList<T>();

		for (String uri : resourceURIs) {
			resources.add(loadResourceByURI(clazz, uri, deep));
		}

		return resources;
	}


	/*
	 * (non-Javadoc)
	 * @see org.intelleo.services.ontologies.AbstractService#loadResourceByURI(java.lang.Class, java.lang.String, boolean)
	 */
	public <T extends Resource> T loadResourceByURI(Class<T> clazz,
			String resourceURI, boolean deep) throws Exception
	{
		T t = null;

		try {
			if (deep) {				
				t = (T) getRdf2BeanBinding().loadDeep(clazz, resourceURI);
			} else {
				t = (T) getRdf2BeanBinding().load(clazz, resourceURI);
			}

		} catch (Exception e) {
			throw e;
		}

		return t;
	}


}
