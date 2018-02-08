package kornell.gui.client.presentation.welcome;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

import kornell.gui.client.ClientFactory;

public class WelcomeActivity extends AbstractActivity {
    private static WelcomePresenter presenter;

    public WelcomeActivity(ClientFactory clientFactory) {
        if (presenter == null) {
            presenter = new WelcomePresenter(clientFactory);
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        Widget widget = presenter.asWidget();
        panel.setWidget(widget);
    }

}
