package kornell.api.client;

import kornell.core.to.SimplePeopleTO;

public class ReportClient extends RESTClient {

    public void courseClassCertificateExists(String courseClassUUID, Callback<String> callback) {
        GET("/report/courseClassCertificateExists?courseClassUUID=" + courseClassUUID).go(callback);
    }

    public void generateCourseClassCertificate(String courseClassUUID, SimplePeopleTO people,
            Callback<String> callback) {
        PUT("/report/certificate?courseClassUUID=" + courseClassUUID).withContentType(SimplePeopleTO.TYPE)
                .withEntityBody(people).go(callback);
    }

}
