#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/../bash/bash-utils.sh

DATE=$(date -u +"%Y%m%d%H%M")
DOMAIN_PREFIX="$INSTITUTION-$DATE"
DOMAIN_NAME=${DOMAIN_NAME:-"$DOMAIN_PREFIX.eduvem.com"}
STACK_NAME=${STACK_NAME:-"cfn-cloudfront-$PURPOSE-$DOMAIN_PREFIX"}


demmand "DOMAIN_NAME"
demmand "GWT_BUCKET"
demmand "LOGS_BUCKET"
demmand "USERCONTENT_BUCKET"
demmand "API_ENDPOINT"
demmand "CERT_ID"
demmand "INSTITUTION"

ARG_S3_DOMAIN_NAME="ParameterKey=knldomainname,ParameterValue='$DOMAIN_NAME'"
ARG_S3_GWT_BUCKET="ParameterKey=knlgwtbucketname,ParameterValue='$GWT_BUCKET'"
ARG_S3_LOGS_BUCKET="ParameterKey=knllogsbucketname,ParameterValue='$LOGS_BUCKET'"
ARG_S3_USERCONTENT_BUCKET="ParameterKey=knlusercontentbucketname,ParameterValue='$USERCONTENT_BUCKET'"
ARG_API_ENDPOINT="ParameterKey=knlapiendpoint,ParameterValue='$API_ENDPOINT'"
ARG_CERT_ID="ParameterKey=knlcertificateid,ParameterValue='$CERT_ID'"

CFN_CREATE_ARGS="--parameters $ARG_S3_DOMAIN_NAME $ARG_S3_GWT_BUCKET $ARG_S3_LOGS_BUCKET $ARG_API_ENDPOINT $ARG_CERT_ID $ARG_S3_USERCONTENT_BUCKET"

#echo $CFN_CREATE_ARGS
source $DIR/cfn-create-stack.sh
