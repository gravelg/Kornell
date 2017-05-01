package kornell.api.client;

import kornell.core.entity.CertificateDetails;

public class CertificateDetailsClient extends RESTClient {

	private String certificateDetailsUUID;

	public CertificateDetailsClient(String certificateDetailsUUID) {
		this.certificateDetailsUUID = certificateDetailsUUID;
	}

	public void update(CertificateDetails certificateDetails, Callback<CertificateDetails> cb) {
		PUT("certificatesDetails",certificateDetails.getUUID()).withContentType(CertificateDetails.TYPE).withEntityBody(certificateDetails).go(cb);
	}

	public void delete(Callback<CertificateDetails> cb) {
		DELETE("certificatesDetails", certificateDetailsUUID).go(cb);
	}
	
}
