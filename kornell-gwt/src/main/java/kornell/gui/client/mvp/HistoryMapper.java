package kornell.gui.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import kornell.gui.client.presentation.admin.audit.AdminAuditPlace;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePlace;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsPlace;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPlace;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.sandbox.SandboxPlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;

@WithTokenizers({ HomePlace.Tokenizer.class, VitrinePlace.Tokenizer.class, TermsPlace.Tokenizer.class,
        WelcomePlace.Tokenizer.class, ProfilePlace.Tokenizer.class, MessagePlace.Tokenizer.class,
        ClassroomPlace.Tokenizer.class, SandboxPlace.Tokenizer.class, AdminInstitutionPlace.Tokenizer.class,
        AdminCoursesPlace.Tokenizer.class, AdminCoursePlace.Tokenizer.class, AdminCourseVersionsPlace.Tokenizer.class,
        AdminCourseVersionPlace.Tokenizer.class, AdminCourseClassPlace.Tokenizer.class,
        AdminCourseClassesPlace.Tokenizer.class, AdminAuditPlace.Tokenizer.class })
public interface HistoryMapper extends PlaceHistoryMapper {
}
