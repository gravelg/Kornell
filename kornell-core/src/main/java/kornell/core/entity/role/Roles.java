package kornell.core.entity.role;

import java.util.List;

import kornell.core.entity.EntityFactory;

public interface Roles {
    public static String TYPE = EntityFactory.PREFIX + "roles+json";

    List<Role> getRoles();
    void setRoles(List<Role> r);

}
