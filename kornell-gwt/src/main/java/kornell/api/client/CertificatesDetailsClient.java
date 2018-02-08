package kornell.api.client;

import kornell.core.entity.CertificateDetails;
import kornell.core.entity.CourseDetailsEntityType;

public class CertificatesDetailsClient extends RESTClient {

    public CertificatesDetailsClient() {
    }

    public void findByEntityTypeAndUUID(CourseDetailsEntityType entityType, String entityUUID,
            Callback<CertificateDetails> cb) {
        GET("certificatesDetails", entityType.toString(), entityUUID).go(cb);
    }

    public void create(CertificateDetails certificateDetails, Callback<CertificateDetails> callback) {
        POST("certificatesDetails").withContentType(CertificateDetails.TYPE).withEntityBody(certificateDetails)
                .go(callback);
    }

}
