#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/bash-utils.sh

REGION=${REGION:-"us-east-1"}
TEMP_DIR=${TEMP_DIR:-"$DIR/kornell-api-eb/"}
API_ARTIFACT=${GWT_ARTIFACT:-"$DIR/../kornell-api/target/kornell-api-eb.zip"}
APPLICATION_NAME=${APPLICATION_NAME:-"kornell-api" }
VERSION_ID=${VERSION_ID:-"$(date -u +'%Y%m%d%H%M')"}
VERSION_BUCKET=${VERSION_BUCKET:-"us-east-1.craftware-dist"}
VERSION_BRANCH=${VERSION_BRANCH:-"master"}
VERSION_LABEL=${VERSION_LABEL:-"kornell-api-$VERSION_ID"}
VERSION_KEY=${VERSION_KEY:-"kornell/$VERSION_BRANCH/latest/kornell-api-eb.zip"}
VERSION_URL="s3://$VERSION_BUCKET/$VERSION_KEY"

demmand "REGION"
demmand "API_ARTIFACT"
demmand "APPLICATION_NAME"
demmand "ENV_NAME"

log "Uploading [$API_ARTIFACT] to [$VERSION_URL]"
aws s3 cp $API_ARTIFACT $VERSION_URL

log "Creating application [$APPLICATION_NAME] version [$VERSION_LABEL]"
aws elasticbeanstalk create-application-version \
	--application-name $APPLICATION_NAME \
	--version-label $VERSION_LABEL \
	--source-bundle S3Bucket=$VERSION_BUCKET,S3Key=$VERSION_KEY \
	--region $REGION

log "Updating environment [$ENV_NAME]"
aws elasticbeanstalk update-environment \
	--application-name $APPLICATION_NAME \
	--environment-name $ENV_NAME \
	--version-label $VERSION_LABEL \
	--region $REGION

log "API module deployment finished successfully"
