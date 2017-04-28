
package kornell.gui.client;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import kornell.gui.client.presentation.admin.audit.AdminAuditView;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePresenter;
import kornell.gui.client.presentation.admin.course.course.AdminCourseView;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesView;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPresenter;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassView;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPresenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionView;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsView;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.classroom.ClassroomPresenter;
import kornell.gui.client.presentation.classroom.ClassroomView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.message.MessagePresenter;
import kornell.gui.client.presentation.message.MessageView;
import kornell.gui.client.presentation.message.compose.MessageComposeView;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.sandbox.SandboxView;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;

public interface ViewFactory {
	
	void initGUI();
	
	SimplePanel getShell();
	ScrollPanel getScrollPanel();
	
	MenuBarView getMenuBarView();
	SouthBarView getSouthBarView(); 
	
	HomeView getHomeView();
	VitrineView getVitrineView();
	TermsView getTermsView();
	WelcomeView getWelcomeView();
	ProfileView getProfileView();
	MessageView getMessageView();
	MessageComposeView getMessageComposeView();
	MessagePresenter getMessagePresenter();
	MessagePresenter getMessagePresenterCourseClass();
	MessagePresenter getMessagePresenterClassroomGlobalChat();
	MessagePresenter getMessagePresenterClassroomTutorChat();

	ClassroomView getClassroomView();
	ClassroomPresenter getClassroomPresenter();
	SandboxView getSandboxView();
	SandboxPresenter getSandboxPresenter();
	
	//admin
	AdminInstitutionView getAdminInstitutionView();
	AdminCoursesView getAdminCoursesView();
	AdminCourseView getAdminCourseView();
	AdminCoursePresenter getAdminCoursePresenter();
	AdminCourseVersionsView getAdminCourseVersionsView();
	AdminCourseVersionView getAdminCourseVersionView();
	AdminCourseVersionContentView getAdminCourseVersionContentView();
	AdminCourseVersionPresenter getAdminCourseVersionPresenter();
	AdminCourseClassesView getAdminCourseClassesView();
	AdminCourseClassView getAdminCourseClassView();
	AdminCourseClassPresenter getAdminCourseClassPresenter();
	AdminAuditView getAdminAuditView();

}
