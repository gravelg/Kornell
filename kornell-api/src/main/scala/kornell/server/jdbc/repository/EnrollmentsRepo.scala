package kornell.server.jdbc.repository

import kornell.core.entity.Enrollment
import scala.collection.JavaConverters._
import kornell.core.entity.EnrollmentState
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.core.entity.Enrollments
import kornell.core.to.EnrollmentTO
import kornell.core.to.EnrollmentsTO
import kornell.server.repository.TOs
import kornell.core.error.exception.EntityConflictException
import scala.collection.mutable.Buffer
import kornell.core.to.SimplePersonTO
import kornell.core.to.SimplePeopleTO
import kornell.server.repository.Entities
import kornell.core.entity.Person
import kornell.core.util.UUID
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import java.util.concurrent.TimeUnit.MINUTES
import kornell.core.entity.CourseVersion
import kornell.core.error.exception.ServerErrorException
import kornell.core.entity.InstitutionType
import kornell.core.to.DashboardLeaderboardTO
import kornell.core.to.DashboardLeaderboardItemTO
import kornell.core.entity.EnrollmentSource
import kornell.server.jdbc.PreparedStmt

object EnrollmentsRepo {

  def byCourseClass(courseClassUUID: String) =
    byCourseClassPaged(courseClassUUID, "", Int.MaxValue, 1, "e.state", false)

  def byCourseClassPaged(courseClassUUID: String, searchTerm: String, pageSize: Int, pageNumber: Int, orderBy: String, asc: Boolean) = {
    val resultOffset = (pageNumber.max(1) - 1) * pageSize
    val filteredSearchTerm = '%' + Option(searchTerm).getOrElse("") + '%'
    val orderColumn = if(orderBy != null && !orderBy.contains(";")) orderBy else "e.state"
    val orderMod =  (if(asc) " asc" else " desc")
    val order = orderColumn + orderMod + (if(orderColumn.contains("e.progress")) (", e.assessmentScore " + orderMod) else "")

    val enrollmentsTO = TOs.newEnrollmentsTO(
      new PreparedStmt(s"""
			  select 
      		e.*, 
      		p.uuid as personUUID,
      		p.fullName,
      		if(pw.username is not null, pw.username, p.email) as username
				from Enrollment e 
				join Person p on e.personUUID = p.uuid
				left join Password pw on p.uuid = pw.personUUID
				where e.classUUID = '${courseClassUUID}' and 
       			e.state <> '${EnrollmentState.deleted.toString}' and 
                (p.fullName like '${filteredSearchTerm}'
                or pw.username like '${filteredSearchTerm}'
                or p.email like '${filteredSearchTerm}')
				order by ${order}, e.state desc, p.fullName limit ${resultOffset}, ${pageSize}
			""", List[String]()).map[EnrollmentTO](toEnrollmentTO)
		)			
    enrollmentsTO.setCount(countByCourseClass(courseClassUUID))
    enrollmentsTO.setCountCancelled(countByCourseClassAndState(courseClassUUID, EnrollmentState.cancelled))
    enrollmentsTO.setPageSize(pageSize)
    enrollmentsTO.setPageNumber(pageNumber.max(1))
    enrollmentsTO.setSearchCount({
      if (searchTerm == "")
        0
      else
        sql"""
          select count(*), 
          if(pw.username is not null, pw.username, p.email) as username
          from Enrollment e 
          join Person p on e.personUUID = p.uuid
          left join Password pw on p.uuid = pw.personUUID
          where e.classUUID = ${courseClassUUID} and 
      	  e.state <> ${EnrollmentState.deleted.toString} and 
          (p.fullName like ${filteredSearchTerm}
          or pw.username like ${filteredSearchTerm}
          or p.email like ${filteredSearchTerm})
        """.first[String].get.toInt
    })
    enrollmentsTO
  }
  
  def countByCourseClass(courseClassUUID: String) = 
    sql"""select count(*) 
      from Enrollment e 
      where e.classUUID = ${courseClassUUID} 
      and e.state <> ${EnrollmentState.deleted.toString}
    """.first[String].get.toInt
  
  def countByCourseClassAndState(courseClassUUID: String, enrollmentState: EnrollmentState) = 
    sql"""select count(*) 
      from Enrollment e 
      where e.classUUID = ${courseClassUUID} 
      and e.state = ${enrollmentState.toString}
    """.first[String].get.toInt
    
  def byPerson(personUUID: String) =
    sql"""
      SELECT 
      e.*
  	  FROM Enrollment e join Person p on e.personUUID = p.uuid 
      WHERE e.personUUID = ${personUUID} and e.state <> ${EnrollmentState.deleted.toString}
	  """.map[Enrollment](toEnrollment)

  def byCourseClassAndPerson(courseClassUUID: String, personUUID: String, getDeleted: Boolean): Option[Enrollment] =
    sql"""
  	  SELECT e.*, p.* 
      FROM Enrollment e join Person p on e.personUUID = p.uuid
      WHERE e.classUUID = ${courseClassUUID} and 
  	    (e.state <> ${EnrollmentState.deleted.toString} or ${getDeleted} = true)  
  	    AND e.personUUID = ${personUUID}
    """.first[Enrollment]

  def byCourseClassAndUsername(courseClassUUID: String, username: String): Option[String] =
    sql"""
  	  SELECT e.uuid
      FROM Enrollment e join Person p on e.personUUID = p.uuid
  	  join Password pw on pw.personUUID = p.uuid
      WHERE e.classUUID = ${courseClassUUID} and e.state <> ${EnrollmentState.deleted.toString} 
      AND pw.username = ${username}
    """.first[String]

  def byCourseVersionAndPerson(courseVersionUUID: String, personUUID: String): Option[Enrollment] =
    sql"""
    	SELECT e.*, p.* 
      FROM Enrollment e join Person p on e.personUUID = p.uuid
      WHERE e.courseVersionUUID = ${courseVersionUUID} and e.state <> ${EnrollmentState.deleted.toString} 
	    AND e.personUUID = ${personUUID}
	  """.first[Enrollment]

  def byStateAndPerson(state: EnrollmentState, personUUID: String) =
    sql"""
  	  SELECT e.*, p.* 
      FROM Enrollment e join Person p on e.personUUID = p.uuid
      WHERE e.personUUID = ${personUUID}
  	  AND e.state = ${state.toString()}
      ORDER BY e.state desc, p.fullName, p.email
	  """.map[EnrollmentTO](toEnrollmentTO)

  def create(
        courseClassUUID:String,
        personUUID:String,
        enrollmentState:EnrollmentState, 
        courseVersionUUID:String,
        parentEnrollmentUUID:String,
        enrollmentSource: EnrollmentSource):Enrollment = 
     create(Entities.newEnrollment(null, null, courseClassUUID, personUUID, null, "", EnrollmentState.notEnrolled, null, null, null, null, null, courseVersionUUID,parentEnrollmentUUID, null, null, null, null, enrollmentSource))

      
  def create(enrollment: Enrollment):Enrollment = {
    if (enrollment.getUUID == null)
      enrollment.setUUID(randomUUID)
    if (enrollment.getCourseClassUUID != null && enrollment.getCourseVersionUUID != null)
      throw new EntityConflictException("doubleEnrollmentCriteria")
    sql""" 
    	insert into Enrollment(uuid,classUUID,personUUID,enrolledOn,state,courseVersionUUID,parentEnrollmentUUID,enrollmentSource)
    	values(
    		${enrollment.getUUID},
    		${enrollment.getCourseClassUUID},
    		${enrollment.getPersonUUID}, 
    		now(),
    		${enrollment.getState.toString},
    		${enrollment.getCourseVersionUUID},
        ${enrollment.getParentEnrollmentUUID},
        ${enrollment.getEnrollmentSource.toString}
    	)""".executeUpdate
    	if (enrollment.getCourseClassUUID != null)
    		ChatThreadsRepo.addParticipantsToCourseClassThread(CourseClassesRepo(enrollment.getCourseClassUUID).get)
    enrollment
  }

  def find(personUUID: String, courseClassUUID: String): Option[Enrollment] = sql"""
	  select * from Enrollment 
	  where personUUID=${personUUID}
	   and classUUID=${courseClassUUID}"""
    .first[Enrollment]

  def simplePersonList(courseClassUUID: String): SimplePeopleTO = {
    TOs.newSimplePeopleTO(sql"""select p.uuid as uuid, p.fullName as fullName, pw.username as username
    from Enrollment enr 
    join Person p on enr.personUUID = p.uuid
    left join Password pw on p.uuid = pw.personUUID
    where enr.state <> ${EnrollmentState.cancelled.toString} and enr.state <> ${EnrollmentState.deleted.toString} and
    enr.classUUID = ${courseClassUUID}""".map[SimplePersonTO](toSimplePersonTO))
  }
    
  def getEspinafreEmailList(): List[Person] = {
    sql"""select p.* from Enrollment e 
        join CourseVersion cv on cv.uuid = e.courseVersionUUID
    	join Person p on e.personUUID = p.uuid
        where cv.label = 'espinafre'
    	and p.receiveEmailCommunication = 1
    	and e.end_date = concat(curdate(), ' 23:59:59')
    	and e.progress < 100
    """.map[Person](toPerson)
  }
  
  def getLeaderboardForDashboard(dashboardEnrollmentUUID: String) = {
    val courseClass = CourseClassesRepo.byEnrollment(dashboardEnrollmentUUID)
    if(!courseClass.isDefined) throw new ServerErrorException("errorGeneratingReport")
    val institution = InstitutionRepo(courseClass.get.getInstitutionUUID).get
    if(!InstitutionType.DASHBOARD.equals(institution.getInstitutionType)) throw new ServerErrorException("errorGeneratingReport")
    TOs.newDashboardLeaderboardTO(
      sql"""
        select 
        	p.uuid,
        	p.fullName, 
          (select ae.entryValue from ActomEntries ae where ae.enrollmentUUID = e.uuid and ae.entryKey = "knl.leaderboardScore") as attribute
        from Person p
        	join Enrollment e on e.personUUID = p.uuid
        where
            e.uuid in (select uuid from Enrollment where classUUID in (select classUUID from Enrollment where uuid = ${dashboardEnrollmentUUID}))
        order by CONVERT(SUBSTRING_INDEX(attribute,'-',-1),UNSIGNED INTEGER) desc, p.fullName;
  	    """.map[DashboardLeaderboardItemTO](toDashboardLeaderboardItemTO)
  	 )
  }
  
  def getLeaderboardPosition(dashboardEnrollmentUUID: String) = {
    val personUUID = sql" SELECT personUUID FROM Enrollment e WHERE uuid = ${dashboardEnrollmentUUID}".first[String].get
    var personAttribute = 0
    val leaderboardItems = getLeaderboardForDashboard(dashboardEnrollmentUUID).getDashboardLeaderboardItems
    for (i <- 0 until leaderboardItems.size; if personAttribute == 0) {
      val item = leaderboardItems.get(i)
      if(item.getPersonUUID.equals(personUUID))
        personAttribute = item.getAttribute.toInt
    }
    (leaderboardItems.asScala.filter(_.getAttribute.toInt > personAttribute).length + 1).toString
  }

  val cacheBuilder = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, MINUTES)
    .maximumSize(1000)

  val uuidLoader = new CacheLoader[String, Option[Enrollment]]() {
    override def load(uuid: String): Option[Enrollment] = sql" SELECT * FROM Enrollment e WHERE uuid = ${uuid}".first[Enrollment]
  }
    
  val uuidCache = cacheBuilder.build(uuidLoader)
  
  def getByUUID(uuid: String) = Option(uuid) flatMap uuidCache.get
  
  def updateCache(e: Enrollment) = {
    val oe = Some(e)
    uuidCache.put(e.getUUID, oe)
  }
  
  def invalidateCache(enrollmentUUID: String) = {
    uuidCache.invalidate(enrollmentUUID)
  }

  def clearCache() = uuidCache.invalidateAll()

}
