package kornell.gui.client.presentation.admin.assets;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.LearningEntity;
import kornell.core.entity.CertificateDetails;
import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.to.CourseDetailsHintsTO;
import kornell.core.to.CourseDetailsLibrariesTO;
import kornell.core.to.CourseDetailsSectionsTO;

public interface AdminAssetsView extends IsWidget {
    public interface Presenter extends IsWidget {
        void init(CourseDetailsEntityType entityType, LearningEntity entity);

        void getUploadURL(String contentType, String elementId, String fileName);

        String getFileURL(String fileName);

        Map<String, String> getInfo();

        void delete(String fileName);

        CourseDetailsSectionsTO getCourseDetailsSectionsTO();

        CourseDetailsHintsTO getCourseDetailsHintsTO();

        CourseDetailsLibrariesTO getCourseDetailsLibrariesTO();

        void initCourseDetailsSections();

        void initCourseDetailsHints();

        void initCourseDetailsLibraries();
    }

    void setPresenter(Presenter presenter);

    void initData(CourseDetailsEntityType entityType, String entityUUID);

    void initThumb(boolean exists);

    void initCertificateDetails(CertificateDetails to);

    void initCourseDetailsSections(CourseDetailsSectionsTO courseDetailsSections);

    void initCourseDetailsHints(CourseDetailsHintsTO courseDetailsHints);

    void initCourseDetailsLibraries(CourseDetailsLibrariesTO courseDetailsLibraries);
}