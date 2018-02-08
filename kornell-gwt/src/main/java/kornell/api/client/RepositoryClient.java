package kornell.api.client;

import kornell.core.entity.ContentRepository;

public class RepositoryClient extends RESTClient {

    public void getRepository(String repositoryUUID, Callback<ContentRepository> cb) {
        GET("/contentRepositories/" + repositoryUUID).sendRequest(null, cb);
    }

    public void updateRepository(String repositoryUUID, ContentRepository repo, Callback<ContentRepository> cb) {
        PUT("contentRepositories", repositoryUUID).withContentType(ContentRepository.TYPE).withEntityBody(repo).go(cb);
    }
}
