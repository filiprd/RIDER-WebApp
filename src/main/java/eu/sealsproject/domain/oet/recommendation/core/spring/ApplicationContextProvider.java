package eu.sealsproject.domain.oet.recommendation.core.spring;

import org.springframework.context.ApplicationContext;

public interface ApplicationContextProvider {

	ApplicationContext getContext();
	
	ApplicationContext createContext();
	
	String[] getContextLocations();
}
