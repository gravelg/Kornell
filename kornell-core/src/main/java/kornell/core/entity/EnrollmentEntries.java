package kornell.core.entity;

import java.util.Map;

public interface EnrollmentEntries {
    public static String TYPE = EntityFactory.PREFIX + "enrollmententries+json";

    Map<String,ActomEntries> getActomEntriesMap();
    void setActomEntriesMap(Map<String,ActomEntries> actomEntriesMap);
}
