package kornell.api.client;

import kornell.core.entity.LearningEntity;

public class AssetsClient extends RESTClient {

	public AssetsClient() {
	}
	
	public void getUploadURL(String entityName, String entityUUID, String filename, String path, Callback<String> callback) {
		GET(entityName, entityUUID, "uploadUrl", "?filename=" + filename + "&path=" + path).go(callback);
	}
	
	public void updateThumbnail(String entityName, String entityUUID, LearningEntity entity, String entityType, Callback<LearningEntity> callback) {
		PUT(entityName, entityUUID).withContentType(entityType).withEntityBody(entity).go(callback);
	}
	
	public void move(String assetType, String entityType, String entityUUID, String direction, Integer index, Callback<String> callback) {
		POST(assetType, entityType, entityUUID, "move" + direction, index.toString()).go(callback);
	}
	
}
