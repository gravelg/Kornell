package kornell.gui.client.presentation.admin.assets;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.AssetsEntity;
import kornell.core.entity.CertificateDetails;
import kornell.core.entity.CourseDetailsEntityType;

public interface AdminAssetsView extends IsWidget {
	public interface Presenter extends IsWidget {
		void init(CourseDetailsEntityType entityType, AssetsEntity entity);
		void getUploadURL(String contentType, String elementId, String fileName);
		String getFileURL(String fileName);
		Map<String, String> getInfo();
		void delete(String fileName);
	}
	void setPresenter(Presenter presenter);
	void initData();
	void initThumb(boolean exists);
	void initCertificateDetails(CertificateDetails to);
}