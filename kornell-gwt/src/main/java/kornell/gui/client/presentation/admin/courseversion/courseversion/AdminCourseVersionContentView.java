package kornell.gui.client.presentation.admin.courseversion.courseversion;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.ContentSpec;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView;

public interface AdminCourseVersionContentView extends IsWidget {
	public interface Presenter extends IsWidget {
		void init(CourseVersion courseVersion, ContentSpec contentSpec);
		AdminCourseVersionContentView getView();
	}
	void setPresenter(Presenter presenter);
	void init(CourseVersion courseVersion, boolean isWizard);
	WizardView getWizardView();

}