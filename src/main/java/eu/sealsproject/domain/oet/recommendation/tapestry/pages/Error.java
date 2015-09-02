package eu.sealsproject.domain.oet.recommendation.tapestry.pages;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;

public class Error {

	@InjectPage
	private Index indexPage;
	
	@OnEvent(component="index")
	 Object index(){
		return indexPage;
	 }
}
