package kornell.gui.client.presentation.admin.course.courses;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import kornell.api.client.KornellSession;
import kornell.core.to.TOFactory;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.ViewFactory;

public class AdminCoursesActivity extends AbstractActivity {
	private ClientFactory clientFactory;

	public AdminCoursesActivity(ClientFactory clientFactory) {
	    this.clientFactory = clientFactory;
	  }

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		//TODO: unrefernce client factory
		TOFactory toFactory = GenericClientFactoryImpl.TO_FACTORY;
		KornellSession session = clientFactory.getKornellSession();
		PlaceController placeController = clientFactory.getPlaceController();
		ViewFactory viewFactory = clientFactory.getViewFactory();
		AdminCoursesPresenter presenter = new AdminCoursesPresenter(session,clientFactory.getEventBus(),placeController,toFactory,viewFactory);
		panel.setWidget(presenter);
		
	}

}
