package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

import kornell.core.to.CourseClassesTO;

public class CourseClassesFetchedEvent extends GwtEvent<CourseClassesFetchedEventHandler> {
    public static final Type<CourseClassesFetchedEventHandler> TYPE = new Type<CourseClassesFetchedEventHandler>();

    private CourseClassesTO courseClassesTO;

    public CourseClassesFetchedEvent(CourseClassesTO courseClassesTO) {
        this.setCourseClassesTO(courseClassesTO);
    }

    @Override
    protected void dispatch(CourseClassesFetchedEventHandler handler) {
        handler.onCourseClassesFetched(this);
    }

    @Override
    public Type<CourseClassesFetchedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public CourseClassesTO getCourseClassesTO() {
        return courseClassesTO;
    }

    public void setCourseClassesTO(CourseClassesTO courseClassesTO) {
        this.courseClassesTO = courseClassesTO;
    }

}