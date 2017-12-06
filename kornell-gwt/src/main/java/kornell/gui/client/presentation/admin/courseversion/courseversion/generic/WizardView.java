package kornell.gui.client.presentation.admin.courseversion.courseversion.generic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;

public class WizardView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, WizardView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private EventBus bus;
	private Presenter presenter;
	private CourseVersion courseVersion;

	public WizardView(final KornellSession session, EventBus bus) {
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void init(CourseVersion courseVersion, Presenter presenter) {
		this.courseVersion = courseVersion;
		this.presenter = presenter;
	}
}