package kornell.gui.client.presentation.admin.course.courses;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.to.CourseTO;
import kornell.gui.client.util.view.table.PaginationPresenter;

public interface AdminCoursesView extends IsWidget {
	public interface Presenter extends PaginationPresenter<CourseTO> {
		void deleteCourse(CourseTO courseTO);
		void duplicateCourse(CourseTO courseTO);
	}
	void setPresenter(AdminCoursesView.Presenter presenter);
	void setCourses(List<CourseTO> rowData);
}