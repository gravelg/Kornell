package kornell.core.to;

import java.util.List;

public interface PeopleTO {
    public static final String TYPE = TOFactory.PREFIX + "people+json";
    List<PersonTO> getPeopleTO();
    void setPeopleTO(List<PersonTO> people);

}
