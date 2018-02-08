package kornell.gui.client.presentation.profile;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import kornell.gui.client.ClientFactory;

public class ProfileActivity extends AbstractActivity {
    private static ProfilePresenter presenter;

    public ProfileActivity(ClientFactory clientFactory) {
        if (presenter == null) {
            presenter = new ProfilePresenter(clientFactory);
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(presenter);
    }

}
