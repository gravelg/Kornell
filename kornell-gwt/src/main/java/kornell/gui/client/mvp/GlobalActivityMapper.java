package kornell.gui.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.admin.audit.AdminAuditActivity;
import kornell.gui.client.presentation.admin.audit.AdminAuditPlace;
import kornell.gui.client.presentation.admin.course.course.AdminCourseActivity;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePlace;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesActivity;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassActivity;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesActivity;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionActivity;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsActivity;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsPlace;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionActivity;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPlace;
import kornell.gui.client.presentation.classroom.ClassroomActivity;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.classroom.ClassroomPresenter;
import kornell.gui.client.presentation.home.HomeActivity;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.message.MessageActivity;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.profile.ProfileActivity;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.sandbox.SandboxActivity;
import kornell.gui.client.presentation.sandbox.SandboxPlace;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.terms.TermsActivity;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrineActivity;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomeActivity;
import kornell.gui.client.presentation.welcome.WelcomePlace;

/**
 * A mapping of places to activities used by this application.
 */
public class GlobalActivityMapper implements AsyncActivityMapper {
    private ClientFactory clientFactory;

    public GlobalActivityMapper(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    /** TODO: This may suck fast */
    public void getActivity(final Place place, final ActivityCallbackHandler activityCallbackHandler) {
        if (!clientFactory.getKornellSession().isAuthenticated() || place instanceof VitrinePlace) {
            activityCallbackHandler.onReceiveActivity(new VitrineActivity((VitrinePlace) place, clientFactory));
        } else if (place instanceof HomePlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new HomeActivity((HomePlace) place, clientFactory));
                }
            });
        } else if (place instanceof TermsPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new TermsActivity(clientFactory));
                }
            });
        } else if (place instanceof WelcomePlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new WelcomeActivity(clientFactory));
                }
            });
        } else if (place instanceof ProfilePlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new ProfileActivity(clientFactory));
                }
            });
        } else if (place instanceof MessagePlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new MessageActivity(clientFactory));
                }
            });
        } else if (place instanceof ClassroomPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    ClassroomPresenter coursePresenter = clientFactory.getViewFactory().getClassroomPresenter();
                    coursePresenter.setPlace((ClassroomPlace) place);
                    activityCallbackHandler.onReceiveActivity(new ClassroomActivity(coursePresenter));
                }
            });
        } else if (place instanceof SandboxPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    SandboxPresenter sandboxPresenter = clientFactory.getViewFactory().getSandboxPresenter();
                    sandboxPresenter.setPlace((SandboxPlace) place);
                    activityCallbackHandler.onReceiveActivity(new SandboxActivity(sandboxPresenter));
                }
            });
        } else if (place instanceof AdminInstitutionPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new AdminInstitutionActivity(clientFactory));
                }
            });
        } else if (place instanceof AdminCoursesPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new AdminCoursesActivity(clientFactory));
                }
            });
        } else if (place instanceof AdminCoursePlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new AdminCourseActivity(clientFactory));
                }
            });
        } else if (place instanceof AdminCourseVersionsPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new AdminCourseVersionsActivity(clientFactory));
                }
            });
        } else if (place instanceof AdminCourseVersionPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new AdminCourseVersionActivity(clientFactory));
                }
            });
        } else if (place instanceof AdminCourseClassesPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new AdminCourseClassesActivity(clientFactory));
                }
            });
        } else if (place instanceof AdminCourseClassPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new AdminCourseClassActivity(clientFactory));
                }
            });
        } else if (place instanceof AdminAuditPlace) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable err) {
                    Window.Location.reload();
                }

                public void onSuccess() {
                    activityCallbackHandler.onReceiveActivity(new AdminAuditActivity(clientFactory));
                }
            });
        } else {
            activityCallbackHandler.onReceiveActivity(null);
        }
    }

}
