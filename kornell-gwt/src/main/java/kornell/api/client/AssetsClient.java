package kornell.api.client;

import kornell.core.entity.ThumbnailEntity;

public class AssetsClient extends RESTClient {

	public AssetsClient() {
	}
	
	public void getUploadURL(String entityName, String entityUUID, String filename, Callback<String> callback) {
		GET(entityName, entityUUID, "uploadUrl", filename).go(callback);
	}
	
	public void updateThumbnail(String entityName, String entityUUID, ThumbnailEntity entity, String entityType, Callback<ThumbnailEntity> callback) {
		PUT(entityName, entityUUID).withContentType(entityType).withEntityBody(entity).go(callback);
	}
	
	public void move(String assetType, String entityType, String entityUUID, String direction, Integer index, Callback<String> callback) {
		POST(assetType, entityType, entityUUID, "move" + direction, index.toString()).go(callback);
	}
	
}
