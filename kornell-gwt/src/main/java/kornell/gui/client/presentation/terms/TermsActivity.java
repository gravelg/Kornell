package kornell.gui.client.presentation.terms;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

import kornell.gui.client.ClientFactory;

public class TermsActivity extends AbstractActivity {
    private static TermsPresenter presenter;

    public TermsActivity(ClientFactory clientFactory) {
        if (presenter == null) {
            presenter = new TermsPresenter(clientFactory);
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        Widget widget = presenter.asWidget();
        panel.setWidget(widget);
    }

}
