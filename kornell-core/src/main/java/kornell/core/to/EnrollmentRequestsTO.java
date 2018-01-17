package kornell.core.to;

import java.util.List;

public interface EnrollmentRequestsTO {
    public static final String TYPE = TOFactory.PREFIX + "enrollmentRequests+json";

    List<EnrollmentRequestTO> getEnrollmentRequests();
    void setEnrollmentRequests(List<EnrollmentRequestTO> enrollmentRequests);

}
