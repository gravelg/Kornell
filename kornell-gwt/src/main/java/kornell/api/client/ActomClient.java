package kornell.api.client;

import static kornell.core.util.StringUtils.isSome;

import java.util.Map;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;

import kornell.core.entity.ActomEntries;
import kornell.core.entity.EntityFactory;

public class ActomClient extends RESTClient {
    // TODO: Consider not hurting DI
    private static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

    private String enrollmentUUID;
    private String encodedActomKey;

    public ActomClient(EnrollmentClient enrollmentClient, String actomKey) {
        this.enrollmentUUID = enrollmentClient.getEnrollmentUUID();
        this.encodedActomKey = URL.encodePathSegment(actomKey);
        assert (isSome(enrollmentUUID));
        assert (isSome(encodedActomKey));
    }

    public void put(Map<String, String> entries, String cause, Callback<ActomEntries> callback) {
        ActomEntries actomEntries = entityFactory.newActomEntries().as();
        actomEntries.setActomKey(encodedActomKey);
        actomEntries.setEnrollmentUUID(enrollmentUUID);
        actomEntries.setEntries(entries);
        ExceptionalRequestBuilder put = PUT("enrollments", enrollmentUUID, "actoms", encodedActomKey, "entries")
                .withContentType(ActomEntries.TYPE).withEntityBody(actomEntries);
        put.go(callback);
    }

    public void get(Callback<ActomEntries> callback) {
        GET("enrollments", enrollmentUUID, "actoms", encodedActomKey, "entries").go(callback);
    }

}
