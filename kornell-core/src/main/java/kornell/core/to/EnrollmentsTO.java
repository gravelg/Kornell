package kornell.core.to;

import java.util.List;

public interface EnrollmentsTO extends Page {
    public static final String TYPE = TOFactory.PREFIX + "enrollments+json";

    List<EnrollmentTO> getEnrollmentTOs();
    void setEnrollmentTOs(List<EnrollmentTO> enrollmentTOs);

    Integer getCountCancelled();
    void setCountCancelled(Integer countCancelled);
}
