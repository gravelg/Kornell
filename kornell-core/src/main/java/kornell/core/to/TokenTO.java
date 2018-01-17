package kornell.core.to;

import java.util.Date;

import kornell.core.entity.AuthClientType;

public interface TokenTO {
    public static String TYPE = TOFactory.PREFIX + "token+json";

    public String getToken();
    public void setToken(String token);

    public Date getExpiry();
    public void setExpiry(Date expiry);

    public String getPersonUUID();
    public void setPersonUUID(String personUUID);

    public AuthClientType getClientType();
    public void setClientType(AuthClientType authClientType);
}
