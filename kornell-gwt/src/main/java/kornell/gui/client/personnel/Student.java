package kornell.gui.client.personnel;

import kornell.core.entity.EnrollmentProgress;

public interface Student {
    boolean isEnrolled();

    EnrollmentProgress getEnrollmentProgress();
}
