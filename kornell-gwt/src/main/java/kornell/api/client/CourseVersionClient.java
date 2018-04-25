package kornell.api.client;

import kornell.core.entity.CourseVersion;
import kornell.core.to.CourseVersionTO;

public class CourseVersionClient extends RESTClient {

    private String courseVersionUUID;

    public CourseVersionClient(String courseVersionUUID) {
        this.courseVersionUUID = courseVersionUUID;
    }

    public void get(Callback<CourseVersionTO> cb) {
        GET("courseVersions",courseVersionUUID).go(cb);
    }

    public void update(CourseVersion courseVersion, Callback<CourseVersion> cb) {
        update(courseVersion, false, cb);
    }

    public void update(CourseVersion courseVersion, Boolean skipAudit, Callback<CourseVersion> cb) {
        PUT("courseVersions",courseVersion.getUUID()+"?skipAudit="+skipAudit).withContentType(CourseVersion.TYPE).withEntityBody(courseVersion).go(cb);
    }

    public void delete(Callback<CourseVersion> cb) {
        DELETE("courseVersions",courseVersionUUID).go(cb);
    }

    public void copy(Callback<CourseVersion> cb) {
        POST("courseVersions",courseVersionUUID,"copy").go(cb);
    }

    public void getContentUploadURL(Callback<String> callback) {
        GET("courseVersions", courseVersionUUID, "contentUploadUrl").go(callback);
    }

    public void resetSandbox(Callback<Void> callback) {
        PUT("courseVersions", courseVersionUUID, "resetSandbox").go(callback);
    }
}
