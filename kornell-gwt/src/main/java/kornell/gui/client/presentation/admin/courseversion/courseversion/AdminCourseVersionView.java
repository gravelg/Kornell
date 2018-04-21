package kornell.gui.client.presentation.admin.courseversion.courseversion;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.Course;
import kornell.core.entity.CourseVersion;

public interface AdminCourseVersionView extends IsWidget {
    public interface Presenter extends IsWidget {
        void buildContentView(String courseVersionUUID);

        void upsertCourseVersion(CourseVersion courseVersion);

        CourseVersion getCourseVersion();

        void setCourseVersion(CourseVersion courseVersion);

        Course getCourse();
    }

    void setPresenter(Presenter presenter);

    Presenter getPresenter();

    void init();

    void addContentPanel(AdminCourseVersionContentView adminCourseVersionContentView);
}