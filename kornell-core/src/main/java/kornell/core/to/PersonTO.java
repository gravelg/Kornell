package kornell.core.to;

import kornell.core.entity.Person;

public interface PersonTO {
    public static String TYPE = TOFactory.PREFIX + "person+json";

    Person getPerson();
    void setPerson(Person person);

    String getUsername();
    void setUsername(String username);
}
