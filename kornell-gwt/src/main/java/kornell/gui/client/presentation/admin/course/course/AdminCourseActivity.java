package kornell.gui.client.presentation.admin.course.course;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import kornell.gui.client.ClientFactory;

public class AdminCourseActivity extends AbstractActivity {
    private ClientFactory clientFactory;

    public AdminCourseActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
        AdminCoursePresenter presenter = clientFactory.getViewFactory().getAdminCoursePresenter();
        panel.setWidget(presenter);

    }

}
