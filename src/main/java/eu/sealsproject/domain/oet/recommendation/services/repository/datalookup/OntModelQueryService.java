package eu.sealsproject.domain.oet.recommendation.services.repository.datalookup;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class OntModelQueryService
{

	public Collection<String> executeOneVariableSelectSparqlQuery(String query,
			String variable, Model model)
	{

		List<String> results = new LinkedList<String>();

		Query q = QueryFactory.create(query);
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(q, model);
		ResultSet resultSet = qe.execSelect();

		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();
			RDFNode value = solution.get(variable);
			if (value.isLiteral())
				results.add(((Literal) value).getLexicalForm());
			else
				results.add(((Resource) value).getURI());
		}

		qe.close();

		return results;

	}
	
	public <T> Collection<T> retrieveAllInstances(Class<T> instanceType,
			Model model, boolean loadDeep)
	{

		RDF2Bean binding = new RDF2Bean(model);
		if (loadDeep)
			return binding.loadDeep(instanceType);
		else
			return binding.load(instanceType);

	}

	public <T> T retrieveInstance(Class<T> instanceType, String instanceUri,
			Model model, boolean loadDeep)
	{

		RDF2Bean binding = new RDF2Bean(model);
		if (loadDeep)
			return binding.loadDeep(instanceType, instanceUri);
		else
			return binding.load(instanceType, instanceUri);
	}

}
