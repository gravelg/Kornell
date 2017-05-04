package kornell.api.client;

import kornell.core.entity.AssetsEntity;

public class AssetsClient extends RESTClient {

	public AssetsClient() {
	}
	
	public void getUploadURL(String entityName, String entityUUID, String filename, Callback<String> callback) {
		GET(entityName, entityUUID, "uploadUrl", filename).go(callback);
	}
	
	public void updateThumbnail(String entityName, String entityUUID, AssetsEntity entity, String entityType, Callback<AssetsEntity> callback) {
		PUT(entityName, entityUUID).withContentType(entityType).withEntityBody(entity).go(callback);
	}
	
}
