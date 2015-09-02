package eu.sealsproject.domain.oet.recommendation.tapestry.services;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.tapestry5.*;
import org.apache.tapestry5.internal.services.PageMarkupRenderer;
import org.apache.tapestry5.internal.services.PageResponseRenderer;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ObjectProvider;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.MarkupWriterFactory;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.springframework.context.ApplicationContext;

import eu.sealsproject.domain.oet.recommendation.core.spring.ApplicationContextObjectProvider;
import eu.sealsproject.domain.oet.recommendation.core.spring.ApplicationContextProviderSingleton;

/**
 * This module is automatically included as part of the Tapestry IoC Registry, it's a good place to
 * configure and extend Tapestry, or to place your own service definitions.
 */
public class AppModule
{
    public static void bind(ServiceBinder binder)
    {
        // binder.bind(MyServiceInterface.class, MyServiceImpl.class);
        
        // Make bind() calls on the binder object to define most IoC services.
        // Use service builder methods (example below) when the implementation
        // is provided inline, or requires more initialization than simply
        // invoking the constructor.
    	
    	binder.bind(ObjectProvider.class, 
        		ApplicationContextObjectProvider.class)
                .withId("ApplicationContextObjectProvider");
    }
    
    
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
    {
        // Contributions to ApplicationDefaults will override any contributions to
        // FactoryDefaults (with the same key). Here we're restricting the supported
        // locales to just "en" (English). As you add localised message catalogs and other assets,
        // you can extend this list of locales (it's a comma separated series of locale names;
        // the first locale name is the default when there's no reasonable match).
        
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");

        // The factory default is true but during the early stages of an application
        // overriding to false is a good idea. In addition, this is often overridden
        // on the command line as -Dtapestry.production-mode=false
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");

        // The application version number is incorprated into URLs for some
        // assets. Web browsers will cache assets because of the far future expires
        // header. If existing assets are changed, the version number should also
        // change, to force the browser to download new versions.
        configuration.add(SymbolConstants.APPLICATION_VERSION, "1.0-SNAPSHOT");

		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
    }
    
    public static ApplicationContext buildApplicationContext() {
		return new ApplicationContextProviderSingleton().getContext();
	}
    
    public static void contributeMasterObjectProvider(
    		@InjectService("ApplicationContextObjectProvider")
    		ObjectProvider applicationContextObjectProvider,
    		OrderedConfiguration<ObjectProvider> configuration) {
        configuration.add("Spring", applicationContextObjectProvider);
    }
    
}
