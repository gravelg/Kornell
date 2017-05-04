package kornell.api.client;

import java.util.List;
import java.util.logging.Logger;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Institution;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseClassTO;
import kornell.core.to.RoleTO;
import kornell.core.to.TokenTO;
import kornell.core.to.UserHelloTO;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.util.ClientProperties;


public class KornellSession extends KornellClient {
	Logger logger = Logger.getLogger(KornellSession.class.getName());

	private static final String PREFIX = ClientProperties.PREFIX + "UserSession";

	private UserInfoTO currentUser = null;
	private Institution institution = null;
	private CourseClassTO currentCourseClass = null;

	public KornellSession() {
		logger.info("Instantiated new Kornell Session");
	}

	public boolean isPlatformAdmin(String institutionUUID) {
		return isValidRole(RoleType.platformAdmin, institutionUUID, null);
	}

	public boolean isPlatformAdmin() {
		return isValidRole(RoleType.platformAdmin, institution.getUUID(), null);
	}

	public boolean isInstitutionAdmin(String institutionUUID) {
		return isValidRole(RoleType.institutionAdmin, institutionUUID, null) || isPlatformAdmin(institutionUUID);
	}

	public boolean isInstitutionAdmin() {
		return isInstitutionAdmin(institution.getUUID());
	}
	
	public boolean hasCourseClassRole(String courseClassUUID) {
		return isCourseClassAdmin(courseClassUUID) || isCourseClassObserver(courseClassUUID) || isCourseClassTutor(courseClassUUID);
	}
	
	public boolean hasCourseClassRole() {
		return isCourseClassAdmin() || isCourseClassObserver() || isCourseClassTutor();
	}

	public boolean isCourseClassAdmin(String courseClassUUID) {
		return isValidRole(RoleType.courseClassAdmin, null, courseClassUUID) || isInstitutionAdmin();
	}

	public boolean isCourseClassAdmin() {
		if(currentCourseClass == null) return false;
		CourseClass courseClass = currentCourseClass.getCourseClass();
		if(courseClass == null) return false;
		String courseClassUUID = courseClass.getUUID();
		return isCourseClassAdmin(courseClassUUID);
	}

	public boolean isCourseClassObserver(String courseClassUUID) {
		return isValidRole(RoleType.observer, null, courseClassUUID) || isInstitutionAdmin();
	}

	public boolean isCourseClassObserver() {
		if(currentCourseClass == null) return false;
		CourseClass courseClass = currentCourseClass.getCourseClass();
		if(courseClass == null) return false;
		String courseClassUUID = courseClass.getUUID();
		return isCourseClassObserver(courseClassUUID);
	}

	public boolean isCourseClassTutor(String courseClassUUID) {
		return isValidRole(RoleType.tutor, null, courseClassUUID) || isInstitutionAdmin();
	}

	public boolean isCourseClassTutor() {
		if(currentCourseClass == null) return false;
		CourseClass courseClass = currentCourseClass.getCourseClass();
		if(courseClass == null) return false;
		String courseClassUUID = courseClass.getUUID();
		return isCourseClassTutor(courseClassUUID);
	}

	public boolean hasAnyAdminRole() {
		if(currentUser == null) return false;
		List<RoleTO> roleTOs = currentUser.getRoles();
		return (RoleCategory.hasRole(roleTOs, RoleType.courseClassAdmin) || 
				RoleCategory.hasRole(roleTOs, RoleType.observer) || 
				RoleCategory.hasRole(roleTOs, RoleType.tutor) || 
				isInstitutionAdmin());
	}

	private boolean isValidRole(RoleType type, String institutionUUID, String courseClassUUID) {
		if (currentUser == null)
			return false;
		return RoleCategory.isValidRole(currentUser.getRoles(), type, institutionUUID, courseClassUUID);
	}

	public boolean isAuthenticated() {
		return currentUser != null;
	}

	public boolean isAnonymous() {
		return ! isAuthenticated();
	}

	public boolean hasSignedTerms() {
		return StringUtils.isSome(institution.getTerms()) &&
				currentUser != null &&
				currentUser.getPerson().getTermsAcceptedOn() != null;
	}

	public void login(String username, 
			String password, 
			final Callback<UserHelloTO> callback) {
		
		Callback<TokenTO> loginWrapper = new Callback<TokenTO>() {

			@Override
			public void ok(TokenTO to) {
				ClientProperties.set(ClientProperties.X_KNL_TOKEN, to.getToken());
				fetchUser(callback);
			}
			
			@Override
			protected void unauthorized(KornellErrorTO kornellErrorTO) {
				setCurrentUser(null);
				callback.unauthorized(kornellErrorTO);
			}
			
			//user must change his password
			@Override
			protected void forbidden(KornellErrorTO kornellErrorTO) {
				callback.forbidden(kornellErrorTO);
			}
			
		};
		String institutionUUID = institution.getUUID();
		POST_LOGIN(username, password, institutionUUID, "/auth/token").sendRequest(null, loginWrapper);
	}

	public void fetchUser(final Callback<UserHelloTO> callback) {
		final Callback<UserHelloTO> wrapper = new Callback<UserHelloTO>() {
			@Override
			public void ok(UserHelloTO userHello) {
				setCurrentUser(userHello.getUserInfoTO());
				callback.ok(userHello);
			}

			@Override
			protected void unauthorized(KornellErrorTO kornellErrorTO) {
				setCurrentUser(null);
				callback.unauthorized(kornellErrorTO);
			}
		};
		GET("/user/login").sendRequest(null, wrapper);
	}
	
	public void logout(){
		POST("/auth/logout").sendRequest(null, new Callback<String>() {
			@Override
			public void ok(String to) {
				//Nothing to do
			}
			
			@Override
			protected void unauthorized(KornellErrorTO kornellErrorTO) {
				//nothing to do here too, if for some reason the token is not there when the user
				//tries to logout, let's just ignore.
			}
		});
		
		ClientProperties.remove(ClientProperties.X_KNL_TOKEN);
		setCurrentUser(null);
	}

	public String getItem(String key) {
		return ClientProperties.get(prefixed(key));
	}

	public void setItem(String key, String value) {
		ClientProperties.set(prefixed(key), value);
	}

	private String prefixed(String key) {
		return PREFIX + ClientProperties.SEPARATOR + currentUser.getPerson().getUUID()
				+ ClientProperties.SEPARATOR + key;
	}

	public String getInstitutionAssetsURL() {
		return StringUtils.mkurl(getRepositoryAssetsURL(), "institution");
	}

	public String getRepositoryAssetsURL() {
		return institution == null ? "" : StringUtils.mkurl("repository", institution.getAssetsRepositoryUUID(), "knl");
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	public CourseClassTO getCurrentCourseClass() {
		return currentCourseClass;
	}

	public void setCurrentCourseClass(CourseClassTO currentCourseClass) {
		this.currentCourseClass = currentCourseClass;
	}
	
	public UserInfoTO getCurrentUser() {
		if (currentUser == null) {
			logger.warning("WARNING: Requested current user for unauthenticated session. Watch out for NPEs. Check before or use callback to be safer.");
		}
		return currentUser;
	}

	public void setCurrentUser(UserInfoTO userInfo) {
		this.currentUser = userInfo;
		if(userInfo != null && userInfo.getPerson() != null){
	        ClientProperties.set(PREFIX + ClientProperties.SEPARATOR + ClientProperties.CURRENT_SESSION, 
	        		userInfo.getPerson().getUUID());
		}
	}

}
