package kornell.gui.client.presentation.admin.courseversion.courseversions;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.to.CourseVersionTO;
import kornell.gui.client.util.view.PaginationPresenter;

public interface AdminCourseVersionsView extends IsWidget {
	public interface Presenter extends PaginationPresenter {
	}
	void setPresenter(AdminCourseVersionsView.Presenter presenter);
	void setCourseVersions(List<CourseVersionTO> courseVersionTOs, Integer count, Integer searchCount);
}