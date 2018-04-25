package kornell.gui.client.presentation.admin.courseversion.courseversions;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.to.CourseVersionTO;
import kornell.gui.client.util.view.table.PaginationPresenter;

public interface AdminCourseVersionsView extends IsWidget {
    public interface Presenter extends PaginationPresenter<CourseVersionTO> {
        void deleteCourseVersion(CourseVersionTO courseVersionTO);

        void duplicateCourseVersion(CourseVersionTO courseVersionTO);

        void resetSandboxEnrollments(kornell.core.to.CourseVersionTO courseVersionTO);
    }

    void setPresenter(AdminCourseVersionsView.Presenter presenter);

    void setCourseVersions(List<CourseVersionTO> courseVersionTOs);
}