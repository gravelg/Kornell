package kornell.scorm.client.scorm12;

import com.google.web.bindery.event.shared.EventBus;

public abstract class Action {
    public abstract void execute(EventBus bus);
}
