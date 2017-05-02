package kornell.api.client;

import kornell.core.entity.CertificateDetails;

public class CourseDetailsSectionClient extends RESTClient {

	private String certificateDetailsUUID;

	public CourseDetailsSectionClient(String certificateDetailsUUID) {
		this.certificateDetailsUUID = certificateDetailsUUID;
	}

	public void update(CertificateDetails certificateDetails, Callback<CertificateDetails> cb) {
		PUT("courseDetailsSections",certificateDetails.getUUID()).withContentType(CertificateDetails.TYPE).withEntityBody(certificateDetails).go(cb);
	}

	public void delete(Callback<CertificateDetails> cb) {
		DELETE("courseDetailsSections", certificateDetailsUUID).go(cb);
	}
	
}
