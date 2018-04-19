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

    public void courseClassInfoExists(String courseClassUUID, String fileType, Callback<String> callback) {
        GET("/report/courseClassInfoExists?courseClassUUID=" + courseClassUUID+"&fileType="+fileType).go(callback);
    }

    public void generateCourseClassInfo(String courseClassUUID, String fileType, SimplePeopleTO people,
            Callback<String> callback) {
        PUT("/report/courseClassInfo?courseClassUUID=" + courseClassUUID+"&fileType="+fileType).withContentType(SimplePeopleTO.TYPE)
        .withEntityBody(people).go(callback);
    }

    public void generateCourseInfo(String courseUUID, String fileType, SimplePeopleTO people,
            Callback<String> callback) {
        PUT("/report/courseClassInfo?courseUUID=" + courseUUID+"&fileType="+fileType).withContentType(SimplePeopleTO.TYPE)
        .withEntityBody(people).go(callback);
    }

}
