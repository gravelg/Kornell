package kornell.gui.client.presentation.admin.courseclass.courseclass;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.ViewFactory;

public class AdminCourseClassActivity extends AbstractActivity {
    private ClientFactory clientFactory;

    public AdminCourseClassActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        ViewFactory viewFactory = clientFactory.getViewFactory();
        AdminCourseClassPresenter presenter = viewFactory.getAdminCourseClassPresenter();
        panel.setWidget(presenter);
    }

}
