package kornell.core.event;

import kornell.core.entity.EntityState;


public interface CourseClassStateChanged extends Event {
	public static final String TYPE = EventFactory.PREFIX+"CourseClassStateChanged+json";
	
	String getFromPersonUUID();
	void setFromPersonUUID(String fromPersonUUID);
	
	String getCourseClassUUID();
	void setCourseClassUUID(String courseClassUUID);
	
	EntityState getFromState();
	void setFromState(EntityState fromState);
	
	EntityState getToState();
	void setToState(EntityState toState);
}
