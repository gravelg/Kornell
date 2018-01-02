package kornell.gui.client.presentation.admin.courseversion.courseversion;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.Course;
import kornell.core.entity.CourseVersion;

public interface AdminCourseVersionView extends IsWidget {
	public interface Presenter extends IsWidget {
		void buildContentView(CourseVersion courseVersion, Course course);
		void upsertCourseVersion(CourseVersion courseVersion, boolean goToListPlace);
		void upsertCourseVersion(CourseVersion courseVersion);
	}
	void setPresenter(Presenter presenter);
	Presenter getPresenter();
	void init();
	void addContentPanel(AdminCourseVersionContentView adminCourseVersionContentView);
}