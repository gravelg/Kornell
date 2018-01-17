package kornell.core.to;

import java.util.List;

public interface InstitutionHostNamesTO {
    public static final String TYPE = TOFactory.PREFIX + "institutionHostName+json";

    List<String> getInstitutionHostNames();
    void setInstitutionHostNames(List<String> institutionHostNames);
}
