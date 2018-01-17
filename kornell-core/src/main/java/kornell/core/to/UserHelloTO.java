package kornell.core.to;

import kornell.core.entity.Institution;

public interface UserHelloTO {
    public static String TYPE = TOFactory.PREFIX + "userhello+json";

    UserInfoTO getUserInfoTO();
    void setUserInfoTO(UserInfoTO userInfoTO);

    Institution getInstitution();
    void setInstitution(Institution institution);

    CourseClassesTO getCourseClassesTO();
    void setCourseClassesTO(CourseClassesTO courseClassesTO);

}
