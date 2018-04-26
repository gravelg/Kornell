package kornell.gui.client.personnel.classroom;

import com.google.gwt.safehtml.shared.UriUtils;

import kornell.core.entity.CourseVersion;
import kornell.core.to.CourseClassTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.util.ClientProperties;

public class WizardTeacher extends LegacyTeacher implements Teacher {

    public WizardTeacher(CourseClassTO courseClassTO) {
        super(courseClassTO);
    }

    public String getClassroomJson(){
        CourseVersion courseVersion = courseClassTO.getCourseVersionTO().getCourseVersion();
        String classroomJson = courseVersion.getClassroomJson();
        String classroomJsonPublished = courseVersion.getClassroomJsonPublished();
        boolean isSandbox = courseClassTO.getCourseClass().isSandbox();
        if(isSandbox && StringUtils.isSome(classroomJson)){
            return ClientProperties.base64Encode(UriUtils.encode(classroomJson));
        } else if(StringUtils.isSome(classroomJsonPublished)) {
            return ClientProperties.base64Encode(UriUtils.encode(classroomJsonPublished));
        }
        return null;
    }

}
