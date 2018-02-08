package kornell.gui.client.presentation.admin.courseversion.courseversion;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.Course;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView;

public interface AdminCourseVersionContentView extends IsWidget {
    public interface Presenter extends IsWidget {
        void init(CourseVersion courseVersion, Course course);

        AdminCourseVersionContentView getView();

        void upsertCourseVersion(CourseVersion courseVersion, boolean goToListPlace);
    }

    void setPresenter(Presenter presenter);

    void init(CourseVersion courseVersion, Course course);

    WizardView getWizardView();

}