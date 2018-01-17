package kornell.core.to;

import java.util.List;

import kornell.core.entity.InstitutionRegistrationPrefix;
import kornell.core.entity.Person;

public interface UserInfoTO {
    public static String TYPE = TOFactory.PREFIX + "userinfo+json";

    Person getPerson();
    void setPerson(Person p);

    InstitutionRegistrationPrefix getInstitutionRegistrationPrefix();
    void setInstitutionRegistrationPrefix(InstitutionRegistrationPrefix institutionRegistrationPrefix);

    String getUsername();
    void setUsername(String username);

    String getLastPlaceVisited();
    void setLastPlaceVisited(String lastPlaceVisited);

    List<RoleTO> getRoles();
    void setRoles(List<RoleTO> roles);

}
