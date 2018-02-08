package kornell.api.client;

import kornell.core.entity.Institution;

public class InstitutionsClient extends RESTClient {

    public void findInstitutionByName(String name, Callback<Institution> cb) {
        GET("/institutions/?name=" + name).sendRequest(null, cb);
    }

    public void findInstitutionByHostName(String hostName, Callback<Institution> cb) {
        GET("/institutions/?hostName=" + hostName).sendRequest(null, cb);
    }

}
