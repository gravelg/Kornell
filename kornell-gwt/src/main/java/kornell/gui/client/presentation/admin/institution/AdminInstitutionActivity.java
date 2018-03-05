package kornell.gui.client.presentation.admin.institution;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import kornell.api.client.KornellSession;
import kornell.core.to.TOFactory;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.ViewFactory;

public class AdminInstitutionActivity extends AbstractActivity {
    private ClientFactory clientFactory;

    public AdminInstitutionActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        // TODO: unrefernce client factory
        TOFactory toFactory = GenericClientFactoryImpl.TO_FACTORY;
        KornellSession session = clientFactory.getKornellSession();
        PlaceController placeController = clientFactory.getPlaceController();
        Place defaultPlace = clientFactory.getDefaultPlace();
        ViewFactory viewFactory = clientFactory.getViewFactory();
        AdminInstitutionPresenter presenter = new AdminInstitutionPresenter(session, clientFactory.getEventBus(),
                placeController, defaultPlace, toFactory, viewFactory);
        panel.setWidget(presenter);
    }

}
