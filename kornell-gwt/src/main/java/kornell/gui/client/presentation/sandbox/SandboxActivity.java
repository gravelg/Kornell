package kornell.gui.client.presentation.sandbox;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class SandboxActivity extends AbstractActivity {

    SandboxPresenter presenter;

    public SandboxActivity(SandboxPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(presenter);
    }
}
