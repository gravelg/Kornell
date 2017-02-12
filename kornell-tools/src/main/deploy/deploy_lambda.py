import boto3, os, datetime, tempfile, zipfile
from botocore.client import Config

# Required env variables for this script:
# VERSION_BUCKET: S3 bucket that contains the application artifacts
# GWT_VERSION_KEY: Path inside VERSION_BUCKET to the GWT assets zip
# GWT_BUCKET: Destination bucket where to copy the extracted GWT assets
# ENV_NAME: Elastic Beanstalk environment name for API deploy
# API_VERSION_KEY: Path inside VERSION_BUCKET to the API artifact zip
# API_APPLICATION_NAME: Elastic Beanstalk application name
def lambda_handler(event, context):
    print "Starting S3 upload"
    tmp_dir = tempfile.mkdtemp()
    zip_path = tmp_dir + '/archive.zip'
    s3_resource = boto3.resource('s3', config=Config(signature_version='s3v4'))
    s3_resource.meta.client.download_file(os.environ['VERSION_BUCKET'], os.environ['GWT_VERSION_KEY'], zip_path)
    zip_ref = zipfile.ZipFile(zip_path, 'r')
    zip_ref.extractall(tmp_dir)
    zip_ref.close()
    os.remove(zip_path)

    extensions = {".jpg": "image/jpeg", ".jpeg": "image/jpeg", ".gif": "image/gif", ".png": "image/png", ".html": "text/html",
        ".txt": "text/plain", ".css": "text/css", ".js": "application/javascript", ".ico": "image/vnd.microsoft.icon",
        ".otf": "application/font-sfnt", ".eot": "application/vnd.ms-fontobject", ".svg": "image/svg+xml", ".ttf": "application/font-sfnt",
        ".woff": "application/font-woff"}

    for path, subdirs, files in os.walk(tmp_dir):
        for name in files:
            if ".nocache." in name:
                max_age = "0"
            else:
                max_age = "2592000"

            upload_path = os.path.join(path, name).replace(tmp_dir + "/", '')
            content_type = extensions.get(os.path.splitext(os.path.join(path, name))[1].lower())
            if content_type is None:
                print "Content Type not found for " + upload_path
                content_type = "application/octet-stream"
            print "Uploading " + os.path.join(path, name) + " to " + upload_path
            s3_resource.meta.client.upload_file(os.path.join(path, name), os.environ['GWT_BUCKET'], upload_path,
                ExtraArgs={'CacheControl': "max-age=" + max_age, 'ContentType': content_type})

    print "S3 upload complete"
    print "Starting API release"
    version_label = 'kornell-api-' + datetime.datetime.utcnow().strftime('%Y%m%d%H%M')
    source_bundle = {"S3Bucket": os.environ['VERSION_BUCKET'], "S3Key": os.environ['API_VERSION_KEY']}
    ebc_client = boto3.client('elasticbeanstalk')
    response = ebc_client.create_application_version(
        ApplicationName=os.environ['API_APPLICATION_NAME'],
        VersionLabel=version_label,
        SourceBundle=source_bundle)

    response = ebc_client.update_environment(
        ApplicationName=os.environ['API_APPLICATION_NAME'],
        EnvironmentName=os.environ['ENV_NAME'],
        VersionLabel=version_label)
    print "API release complete"
