package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CourseClassesFetchedEventHandler extends EventHandler {

    void onCourseClassesFetched(CourseClassesFetchedEvent event);
}
