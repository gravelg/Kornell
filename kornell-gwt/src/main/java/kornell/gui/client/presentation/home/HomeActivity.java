package kornell.gui.client.presentation.home;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import kornell.gui.client.ClientFactory;

public class HomeActivity extends AbstractActivity {

    static HomePresenter presenter;

    public HomeActivity(HomePlace place, ClientFactory clientFactory) {
        if (presenter == null) {
            presenter = new HomePresenter(clientFactory);
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(presenter);

    }
}
