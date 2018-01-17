package kornell.core.entity;

import java.util.List;

public interface Roles {
    public static String TYPE = EntityFactory.PREFIX + "roles+json";

    List<Role> getRoles();
    void setRoles(List<Role> r);

}
