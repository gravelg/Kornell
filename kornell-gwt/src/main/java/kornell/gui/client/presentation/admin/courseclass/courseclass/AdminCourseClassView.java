package kornell.gui.client.presentation.admin.courseclass.courseclass;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.RegistrationType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;
import kornell.gui.client.util.view.PaginationPresenter;

public interface AdminCourseClassView extends IsWidget {
	public interface Presenter extends PaginationPresenter {
		void changeEnrollmentState(EnrollmentTO object, EnrollmentState state);
		void changeCourseClassState(CourseClassTO courseClassTO, CourseClassState toState);
		boolean showActionButton(String actionName, EnrollmentTO enrollmentTO);
		void onAddEnrollmentButtonClicked(String fullName, String email);
		void onAddEnrollmentBatchButtonClicked(String txtAddEnrollmentBatch);
		void onGoToCourseButtonClicked();
		void onModalOkButtonClicked();
        void onModalTransferOkButtonClicked(String enrollmentUUID, String targetCourseClassUUID);
		void onUserClicked(EnrollmentTO enrollmentTO);
		void updateCourseClass(String courseClassUUID);
		void updateCourseClassUI(CourseClassTO courseClassTO);
		List<EnrollmentTO> getEnrollments();
		void deleteEnrollment(EnrollmentTO enrollmentTO);
		void onGenerateCertificate(EnrollmentTO enrollmentTO);
		void upsertCourseClass(CourseClass courseClass);
		void onBatchCancelModalOkButtonClicked(String txtCancelEnrollmentBatch);
		void setOrderBy(String dataStoreName);
		void setAsc(boolean ascending);
		String getOrderBy();
		boolean getAsc();
	}

	void setPresenter(Presenter presenter);
	void setEnrollmentList(List<EnrollmentTO> enrollmentsIn, Integer count, Integer countCancelled, Integer searchCount, boolean refresh);
	void showModal(boolean show, String type);
	void setModalErrors(String title, String lbl1, String errors, String lbl2);
    void setCourseClassTO(CourseClassTO courseClassTO);
	void setHomeTabActive();
	void showEnrollmentsPanel(boolean visible);
	void showTabsPanel(boolean visible);
	void buildAdminsView();
	void buildConfigView(boolean isCreationMode);
	void buildReportsView();
	void buildMessagesView();
	void prepareAddNewCourseClass(boolean addingNewCourseClass);
	void setCanPerformEnrollmentAction(boolean allow);
	void setUserEnrollmentIdentificationType(RegistrationType registrationType);
	void clearEnrollmentFields();
	void clearPagination();
	void buildAssetsView();
}