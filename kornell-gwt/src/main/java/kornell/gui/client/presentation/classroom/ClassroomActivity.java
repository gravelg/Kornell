package kornell.gui.client.presentation.classroom;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ClassroomActivity extends AbstractActivity {

    private ClassroomPresenter presenter;

    public ClassroomActivity(ClassroomPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(presenter);
    }
}