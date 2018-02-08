package kornell.gui.client.presentation.classroom;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import kornell.core.entity.ContentSpec;
import kornell.core.util.StringUtils;

public class ClassroomPlace extends Place {
    private String enrollmentUUID;
    private ContentSpec contentSpec;

    public ClassroomPlace(String enrollmentUUID) {
        this(enrollmentUUID, ContentSpec.SCORM12);
    }

    public ClassroomPlace(String enrollmentUUID, ContentSpec contentSpec) {
        this.enrollmentUUID = enrollmentUUID;
        this.setContentSpec(contentSpec);
    }

    public String getEnrollmentUUID() {
        return enrollmentUUID;
    }

    public void serEnrollmentUUID(String enrollmentUUID) {
        this.enrollmentUUID = enrollmentUUID;
    }

    @Prefix("classroom")
    public static class Tokenizer implements PlaceTokenizer<ClassroomPlace> {
        public ClassroomPlace getPlace(String str) {
            String[] tokens = StringUtils.parseStrings(str);
            return new ClassroomPlace(tokens[0], ContentSpec.SCORM12);
        }

        public String getToken(ClassroomPlace place) {
            return place.getEnrollmentUUID();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }

    public ContentSpec getContentSpec() {
        return contentSpec;
    }

    public void setContentSpec(ContentSpec contentSpec) {
        this.contentSpec = contentSpec;
    }
}