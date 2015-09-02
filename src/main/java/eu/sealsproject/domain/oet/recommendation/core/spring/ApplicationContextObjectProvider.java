package eu.sealsproject.domain.oet.recommendation.core.spring;

import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ObjectProvider;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.springframework.context.ApplicationContext;

public class ApplicationContextObjectProvider implements ObjectProvider{

	private final ApplicationContext _context;

	public ApplicationContextObjectProvider(
			@InjectService("ApplicationContext")
			ApplicationContext context) {
		_context = context;
	}

	/**
	 * The expression is the name of a spring bean inside the context.
	 */
	public <T> T provide(Class<T> objectType,
			AnnotationProvider annotationProvider, ObjectLocator locator) {
		SpringBean annotation = annotationProvider
				.getAnnotation(SpringBean.class);
		if (annotation == null)
			return null;
		String beanName = annotation.value();
		try {
			Object raw = _context.getBean(beanName, objectType);
			return objectType.cast(raw);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
