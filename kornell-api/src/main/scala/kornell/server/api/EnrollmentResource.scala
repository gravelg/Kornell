package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Assessment
import kornell.core.entity.Enrollment
import kornell.core.lom.Contents
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.util.Err
import kornell.server.util.AccessDeniedErr
import kornell.server.repository.ContentRepository
import java.util.HashMap
import kornell.core.entity.ActomEntries
import kornell.core.entity.EnrollmentEntries
import kornell.server.repository.Entities
import kornell.core.to.EnrollmentLaunchTO
import kornell.server.repository.TOs
import kornell.server.scorm12.SCORM12
import kornell.core.entity.EnrollmentsEntries
import kornell.server.jdbc.PreparedStmt
import kornell.server.ep.EnrollmentSEP
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.core.to.DashboardLeaderboardTO
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.core.entity.ContentSpec
import kornell.server.jdbc.repository.CourseRepo

@Produces(Array(Enrollment.TYPE))
class EnrollmentResource(uuid: String) {
  lazy val enrollment = get
  lazy val enrollmentRepo = EnrollmentRepo(uuid)

  def get = enrollmentRepo.get

  @GET
  def first = enrollmentRepo.first

  @PUT
  @Produces(Array("text/plain"))
  @Consumes(Array(Enrollment.TYPE))
  def update(enrollment: Enrollment) = {
    EnrollmentRepo(enrollment.getUUID).update(enrollment)
  }.requiring(PersonRepo(getAuthenticatedPersonUUID).hasPowerOver(enrollment.getPersonUUID), AccessDeniedErr())
   .get

  @Path("actoms/{actomKey}")
  def actom(@PathParam("actomKey") actomKey: String) = ActomResource(uuid, actomKey)

  @GET
  @Path("contents")
  @Produces(Array(Contents.TYPE))
  def contents(): Option[Contents] = AuthRepo().withPerson { person =>
    first map { e =>
      ContentRepository.findKNLVisitedContent(e, person)
    }
  }
    
 def findRootOf(uuid:String): String ={ 
   val parent = sql"""
   select parentEnrollmentUUID from Enrollment where uuid = $uuid
   """.first[String] match {
     case Some(p) => p
     case None => uuid
   }
   parent 
 }
 
 def findFamilyOf(parentUUID:String):List[String] ={
   val selfie = List(parentUUID)
   val children = sql"""
   select uuid from Enrollment where parentEnrollmentUUID = $parentUUID
  """.map[String]
   val result = selfie ++ children
   result
 }
 
 
  
 def findEnrollmentsFamilyUUIDs():List[String] = {
   val selfie = Set(uuid)
   val family = findFamilyOf(findRootOf(uuid)).toSet
   val result = (selfie ++ family).toList
   result
 }

  @GET
  @Path("launch")
  @Produces(Array(EnrollmentLaunchTO.TYPE))
  def launch() = AuthRepo().withPerson { person =>
    
    val courseClassUUID = Option(enrollment.getCourseClassUUID).getOrElse {
      val parent = EnrollmentRepo(enrollment.getParentEnrollmentUUID).first
      parent.map{_.getCourseClassUUID()}.getOrElse(null)
    }
    val courseClass = if(courseClassUUID != null){
      CourseClassesRepo(courseClassUUID).get
    } else null
    val courseVersion = CourseVersionRepo(courseClass.getCourseVersionUUID).get
    
    val eLaunch: EnrollmentLaunchTO = TOs.newEnrollmentLaunchTO
    
    val eContents = contents.get
    eLaunch.setContents(eContents)
    
    val enrollments:List[String] = findEnrollmentsFamilyUUIDs
    val eEntries = getEntries(enrollments)
    val mEntries = eEntries.getEnrollmentEntriesMap.asScala    
    
    for {
      (enrollmentUUID, enrollmentEntries) <- mEntries.par
      (actomKey,actomEntries) <- enrollmentEntries.getActomEntriesMap.asScala
    } {
      val entriesMap = actomEntries.getEntries
      val launchedMap = SCORM12.initialize(entriesMap, person, enrollment, courseClass)
      entriesMap.putAll(launchedMap)
      actomEntries.setEntries(entriesMap)
    }
    
    //if the enrollment is on a class and it's a SCORM12 version
    if(courseClass != null &&
        ContentSpec.SCORM12.equals(CourseRepo(courseVersion.getCourseUUID).get.getContentSpec)) {
      //initialize the enrollmentEntries map if no attribute exists
      val enrollmentEntries = Option(eEntries.getEnrollmentEntriesMap.get(enrollment.getUUID)) match {
        case Some(ee) => ee
        case None => {
          val ee = Entities.newEnrollmentEntries
          eEntries.getEnrollmentEntriesMap.put(enrollment.getUUID, ee)
          ee
        }
      }
      
      //initialize for each actom
      eContents.getChildren.asScala.foreach { topic =>  
        if(topic.getTopic != null){
          topic.getTopic.getChildren.asScala.foreach { externalPage =>  
            val key = externalPage.getExternalPage.getKey
            val actomEntries = Option(enrollmentEntries.getActomEntriesMap.get(key)) match {
              case Some(ae) => ae
              case None => {
                val entriesMap = new HashMap[String,String]()
                val launchedMap = SCORM12.initialize(entriesMap, person, enrollment, courseClass)
                entriesMap.putAll(launchedMap)
                val ae = Entities.newActomEntries(enrollment.getUUID, key, entriesMap)
                enrollmentEntries.getActomEntriesMap.put(key, ae)
                ae
              }
            }
          }  
        }
      }
    }

    eLaunch.setEnrollmentEntries(eEntries)
    EnrollmentSEP.onProgress(uuid)
    eLaunch
  }

  def getEntries(es:List[String]):EnrollmentsEntries = {
    val esEntries: EnrollmentsEntries = Entities.newEnrollmentsEntries()
    val esEntriesMap = esEntries.getEnrollmentEntriesMap
    
    val sql = s"""
      select * from ActomEntries
      where enrollmentUUID IN (${es.map{s => s"'${s}'"}.mkString(",")})
      order by enrollmentUUID, actomKey      
    """
    
    new PreparedStmt(sql,List()).foreach { rs =>  
      val enrollmentUUID = rs.getString("enrollmentUUID")
      val actomKey = rs.getString("actomKey")
      val entryKey = rs.getString("entryKey")
      val entryValue = rs.getString("entryValue")

      val enrollmentEntries = Option(esEntriesMap.get(enrollmentUUID)) match {
        case Some(e) => e
        case None => {
          val e = Entities.newEnrollmentEntries
          esEntriesMap.put(enrollmentUUID, e)
          e
        }
      }

      val aeMap = enrollmentEntries.getActomEntriesMap() 
      
      val actomEntries = Option(aeMap.get(actomKey)) match {
        case Some(a) => a
        case None => {
          val a: ActomEntries = Entities.newActomEntries(enrollmentUUID, actomKey, new HashMap[String, String]())
          aeMap.put(actomKey, a)
          a
        }
      }

      actomEntries.getEntries.put(entryKey, entryValue)
    }
    esEntries   
  }

  @GET
  @Path("approved")
  @Produces(Array("application/octet-stream"))
  def approved = {
    val e = first.get
    if (Assessment.PASSED == e.getAssessment) {
      if (e.getAssessmentScore != null)
        e.getAssessmentScore.toString
      else
        ""
    } else {
      ""
    }
  }
    
  @GET
  @Produces(Array(EnrollmentsEntries.TYPE))
  def getEntries():EnrollmentsEntries = getEntries(List(uuid))


}