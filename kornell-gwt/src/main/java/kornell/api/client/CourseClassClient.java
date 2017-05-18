package kornell.api.client;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Roles;
import kornell.core.to.CourseClassTO;
import kornell.core.to.LibraryFilesTO;
import kornell.core.to.RolesTO;

public class CourseClassClient extends RESTClient {

	private String courseClassUUID;

	public CourseClassClient(String courseClassUUID) {
		this.courseClassUUID = courseClassUUID;
	}
	
    public void getTO(Callback<CourseClassTO> callback) {
        GET("courseClasses",courseClassUUID,"to").go(callback);
    }

	public void update(CourseClass courseClass, Callback<CourseClass> cb) {
		PUT("courseClasses",courseClass.getUUID()).withContentType(CourseClass.TYPE).withEntityBody(courseClass).go(cb);
	}

	public void delete(Callback<CourseClass> cb) {
		DELETE("courseClasses",courseClassUUID).go(cb);
	}

	public void copy(Callback<CourseClass> cb) {
		POST("courseClasses",courseClassUUID,"copy").go(cb);
	}

	public void getAdmins(String bindMode, Callback<RolesTO> cb) {
		GET("courseClasses",courseClassUUID,"admins"+"?bind="+bindMode).withContentType(CourseClass.TYPE).go(cb);
	}

	public void updateAdmins(Roles roles, Callback<Roles> cb) {
		PUT("courseClasses",courseClassUUID,"admins").withContentType(Roles.TYPE).withEntityBody(roles).go(cb);
	}
	
	public void getTutors(String bindMode, Callback<RolesTO> cb) {
        GET("courseClasses",courseClassUUID,"tutors"+"?bind="+bindMode).withContentType(CourseClass.TYPE).go(cb);
    }

    public void updateTutors(Roles roles, Callback<Roles> cb) {
        PUT("courseClasses",courseClassUUID,"tutors").withContentType(Roles.TYPE).withEntityBody(roles).go(cb);
    }
    
    public void getObservers(String bindMode, Callback<RolesTO> cb) {
        GET("courseClasses",courseClassUUID,"observers"+"?bind="+bindMode).withContentType(CourseClass.TYPE).go(cb);
    }

    public void updateObservers(Roles roles, Callback<Roles> cb) {
        PUT("courseClasses",courseClassUUID,"observers").withContentType(Roles.TYPE).withEntityBody(roles).go(cb);
    }

	public void libraryFiles(Callback<LibraryFilesTO> callback) {
		GET("courseClasses",courseClassUUID,"libraryFiles").go(callback);
	}
	
}
