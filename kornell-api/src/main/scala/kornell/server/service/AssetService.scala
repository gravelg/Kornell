package kornell.server.service

import kornell.core.entity.CourseDetailsEntityType
import kornell.core.util.{StringUtils, UUID}
import kornell.server.jdbc.repository.{CertificatesDetailsRepo, ContentRepositoriesRepo, CourseDetailsHintsRepo, CourseDetailsLibrariesRepo, CourseDetailsSectionsRepo, InstitutionRepo}

import scala.collection.JavaConverters.asScalaBufferConverter

object AssetService {

  def copyAssets(institutionUUID: String, entityType: CourseDetailsEntityType, sourceEntityUUID: String, targetEntityUUID: String, thumbUrl: String): Unit = {
    val institution = InstitutionRepo(institutionUUID).get
    val bucketName = ContentRepositoriesRepo.firstRepository(institution.getAssetsRepositoryUUID).get.getBucketName
    val s3 = S3Service.getAmazonS3Client(institution.getUUID)

    //copy thumbnail
    if (StringUtils.isSome(thumbUrl)) {
      val sourceThumbPath = S3Service.getCourseAssetUrl(institutionUUID, sourceEntityUUID, S3Service.THUMB_FILENAME, "")
      val targetThumbPath = S3Service.getCourseAssetUrl(institutionUUID, targetEntityUUID, S3Service.THUMB_FILENAME, "")
      println(sourceThumbPath)
      println(targetThumbPath)
      try {
        s3.copyObject(bucketName, sourceThumbPath, bucketName, targetThumbPath)
      } catch { case _: Exception => }
    }

    //copy certificate bg
    val certificateDetailsOption = CertificatesDetailsRepo.getForEntity(sourceEntityUUID, entityType)
    if (certificateDetailsOption.isDefined) {
      val certificateDetails = certificateDetailsOption.get
      certificateDetails.setUUID(UUID.random)
      certificateDetails.setBgImage(certificateDetails.getBgImage.replace(sourceEntityUUID, targetEntityUUID))
      certificateDetails.setEntityUUID(targetEntityUUID)
      CertificatesDetailsRepo.create(certificateDetails)

      val sourceCertificateBgPath = S3Service.getCourseAssetUrl(institutionUUID, sourceEntityUUID, S3Service.CERTIFICATE_FILENAME, "")
      val targetCertificateBgPath = S3Service.getCourseAssetUrl(institutionUUID, targetEntityUUID, S3Service.CERTIFICATE_FILENAME, "")
      println(sourceCertificateBgPath)
      println(targetCertificateBgPath)
      try {
        s3.copyObject(bucketName, sourceCertificateBgPath, bucketName, targetCertificateBgPath)
      } catch { case _: Exception => }
    }

    //copy sections
    val sections = CourseDetailsSectionsRepo.getForEntity(sourceEntityUUID, entityType)
    sections.getCourseDetailsSections.asScala.foreach(courseDetailsSection => {
      courseDetailsSection.setEntityUUID(targetEntityUUID)
      courseDetailsSection.setUUID(UUID.random)
      CourseDetailsSectionsRepo.create(courseDetailsSection)
    })

    //copy hints
    val hints = CourseDetailsHintsRepo.getForEntity(sourceEntityUUID, entityType)
    hints.getCourseDetailsHints.asScala.foreach(courseDetailsHint => {
      courseDetailsHint.setEntityUUID(targetEntityUUID)
      courseDetailsHint.setUUID(UUID.random)
      CourseDetailsHintsRepo.create(courseDetailsHint)
    })

    //copy library files
    val libraries = CourseDetailsLibrariesRepo.getForEntity(sourceEntityUUID, entityType)
    libraries.getCourseDetailsLibraries.asScala.foreach(courseDetailsLibrary => {
      courseDetailsLibrary.setEntityUUID(targetEntityUUID)
      courseDetailsLibrary.setUUID(UUID.random)
      CourseDetailsLibrariesRepo.create(courseDetailsLibrary)

      val sourceLibraryPath = S3Service.getCourseAssetUrl(institutionUUID, sourceEntityUUID, S3Service.THUMB_FILENAME, "library")
      val targetLibraryPath = S3Service.getCourseAssetUrl(institutionUUID, targetEntityUUID, S3Service.THUMB_FILENAME, "library")
      println(sourceLibraryPath)
      println(targetLibraryPath)
      try {
        s3.copyObject(bucketName, sourceLibraryPath, bucketName, targetLibraryPath)
      } catch { case _: Exception => }
    })
  }
}