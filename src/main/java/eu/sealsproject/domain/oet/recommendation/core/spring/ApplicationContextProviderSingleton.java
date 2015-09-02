package eu.sealsproject.domain.oet.recommendation.core.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContextProviderSingleton implements
		ApplicationContextProvider {

	private static ApplicationContext context;

	public ApplicationContext createContext() {
		AbstractApplicationContext context = new ClassPathXmlApplicationContext(
				getContextLocations());
    	context.registerShutdownHook();
    	return context;
	}

	public String[] getContextLocations() {
		String[] contextLocations = {
				"META-INF/eu/sealsproject/domain/oet/recommendation/core/services.xml",
				"META-INF/eu/sealsproject/domain/oet/recommendation/core/visualization.xml"
			};
		return contextLocations;
	}

	public ApplicationContext getContext() {
		if (context == null) context = createContext();
		return context;
	}

}
