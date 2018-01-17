package kornell.core.entity;

import java.util.List;

public interface People {
    public static final String TYPE = EntityFactory.PREFIX + "people+json";
    List<Person> getPeople();
    void setPeople(List<Person> people);

}
