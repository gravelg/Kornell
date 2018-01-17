package kornell.core.to;

import java.util.List;

public interface InstitutionEmailWhitelistTO {
    public static final String TYPE = TOFactory.PREFIX + "institutionEmailWhitelist+json";

    List<String> getDomains();
    void setDomains(List<String> domains);
}
