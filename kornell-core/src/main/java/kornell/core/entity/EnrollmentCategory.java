package kornell.core.entity;

public class EnrollmentCategory {

    public static boolean isFinished(Enrollment enrollment) {
        return enrollment != null && enrollment.getCertifiedAt() != null;
    }

    public static EnrollmentProgressDescription getEnrollmentProgressDescription(Enrollment enrollment) {
        if(isFinished(enrollment)){
            return EnrollmentProgressDescription.completed;
        } else if(enrollment.getProgress() == null || enrollment.getProgress() == 0){
            return EnrollmentProgressDescription.notStarted;
        } else {
            return EnrollmentProgressDescription.inProgress;
        }
    }


}