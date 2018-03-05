package kornell.gui.client.presentation.admin.courseversion.courseversion;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import kornell.gui.client.ClientFactory;

public class AdminCourseVersionActivity extends AbstractActivity {
    private ClientFactory clientFactory;

    public AdminCourseVersionActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
        AdminCourseVersionPresenter presenter = clientFactory.getViewFactory().getAdminCourseVersionPresenter();
        panel.setWidget(presenter);

    }

}
