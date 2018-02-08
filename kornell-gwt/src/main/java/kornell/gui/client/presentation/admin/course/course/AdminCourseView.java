package kornell.gui.client.presentation.admin.course.course;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.Course;

public interface AdminCourseView extends IsWidget {
    public interface Presenter extends IsWidget {
        void upsertCourse(Course course);

        Course getNewCourse();
    }

    void setPresenter(Presenter presenter);

    void init();
}