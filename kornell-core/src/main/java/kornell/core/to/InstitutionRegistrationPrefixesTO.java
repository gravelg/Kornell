package kornell.core.to;

import java.util.List;

import kornell.core.entity.InstitutionRegistrationPrefix;

public interface InstitutionRegistrationPrefixesTO {
    public static final String TYPE = TOFactory.PREFIX + "institutionRegistrationPrefixes+json";

    List<InstitutionRegistrationPrefix> getInstitutionRegistrationPrefixes();
    void setInstitutionRegistrationPrefixes(List<InstitutionRegistrationPrefix> institutionRegistrationPrefixes);

}
