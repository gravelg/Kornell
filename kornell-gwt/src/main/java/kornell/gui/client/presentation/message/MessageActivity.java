package kornell.gui.client.presentation.message;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import kornell.gui.client.ClientFactory;

public class MessageActivity extends AbstractActivity {
    private static MessagePresenter presenter;

    public MessageActivity(ClientFactory clientFactory) {
        if (presenter == null) {
            presenter = clientFactory.getViewFactory().getMessagePresenter();
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(presenter);
    }

}
