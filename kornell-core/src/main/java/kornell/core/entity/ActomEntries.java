package kornell.core.entity;

import java.util.Map;

public interface ActomEntries {
    public static String TYPE = EntityFactory.PREFIX + "actomentries+json";

    String getEnrollmentUUID();
    void setEnrollmentUUID(String enrollmentUUID);

    String getActomKey();
    void setActomKey(String actomKey);

    Map<String,String> getEntries();
    void setEntries(Map<String,String> values);


    //TODO: Consider timestamping individual entries
    String getLastModifiedAt();
    void setLastModifiedAt(String lastModifiedAt);

}
