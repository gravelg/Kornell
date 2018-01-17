package kornell.core.to;

import java.util.List;

import kornell.core.event.EntityChanged;

public interface EntityChangedEventsTO extends Page {
    public static final String TYPE = TOFactory.PREFIX + "EntityChangedEvents+json";

    List<EntityChanged> getEntitiesChanged();
    void setEntitiesChanged(List<EntityChanged> entitiesChanged);

}
