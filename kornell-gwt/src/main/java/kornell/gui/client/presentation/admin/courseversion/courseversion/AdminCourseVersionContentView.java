package kornell.gui.client.presentation.admin.courseversion.courseversion;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.Course;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView;

public interface AdminCourseVersionContentView extends IsWidget {
    public interface Presenter extends IsWidget {

        void init(AdminCourseVersionPresenter adminCourseVersionPresenter);

        AdminCourseVersionContentView getView();

        CourseVersion getCourseVersion();

        void setCourseVersion(CourseVersion courseVersion);

        Course getCourse();
    }

    void setPresenter(Presenter presenter);

    void init();

    WizardView getWizardView();

}