package kornell.gui.client.personnel;

import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.entity.EnrollmentProgress;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.entity.EntitiesC;

public class StudentImpl implements Student {

    private CourseClassTO courseClassTO;

    public StudentImpl(CourseClassTO courseClassTO, UserInfoTO userInfoTO) {
        this.courseClassTO = courseClassTO;
    }

    @Override
    public boolean isEnrolled() {
        return courseClassTO.getEnrollment() != null;
    }

    @Override
    public EnrollmentProgress getEnrollmentProgress() {
        if (courseClassTO.getEnrollment().getProgress() != null) {
            return enrollmentProgressOf(courseClassTO.getEnrollment());
        } else
            return null;
    }

    private EnrollmentProgress enrollmentProgressOf(Enrollment enrollment) {
        Integer progress = enrollment.getProgress();
        EnrollmentProgress ep = EntitiesC.get().newEnrollmentProgress();
        if (progress != null) {
            if (progress <= 0) {
                ep.setProgress(0);
            } else if (progress >= 100) {
                ep.setProgress(100);
            } else {
                ep.setProgress(progress);
            }
            ep.setDescription(EnrollmentCategory.getEnrollmentProgressDescription(courseClassTO.getEnrollment()));
            ep.setCertifiedAt(enrollment.getCertifiedAt());
            return ep;
        } else
            return null;

    }

}
