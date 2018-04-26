package kornell.gui.client.personnel.classroom;

import kornell.core.entity.EnrollmentProgress;

public interface Student {
    boolean isEnrolled();

    EnrollmentProgress getEnrollmentProgress();
}
