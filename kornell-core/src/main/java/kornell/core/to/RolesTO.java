package kornell.core.to;

import java.util.List;

public interface RolesTO {
    public static final String TYPE = TOFactory.PREFIX + "roles+json";

    List<RoleTO> getRoleTOs();
    void setRoleTOs(List<RoleTO> roleTOs);

}