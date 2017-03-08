package kornell.server.report

import java.io.InputStream
import java.sql.ResultSet
import java.util.HashMap
import scala.collection.JavaConverters.seqAsJavaListConverter
import kornell.core.to.report.InstitutionBillingEnrollmentReportTO
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.TOs
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.core.to.report.InstitutionBillingMonthlyReportTO
import kornell.core.entity.BillingType
import kornell.core.entity.Institution
import kornell.core.entity.CourseClass
import kornell.core.to.report.CourseClassAuditTO
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.core.entity.EnrollmentState
import kornell.core.entity.EnrollmentSource
import javax.servlet.http.HttpServletResponse
import kornell.server.jdbc.repository.CourseClassRepo
import java.text.SimpleDateFormat

object ReportCourseClassAuditGenerator {

  def generateCourseClassAuditReport(courseClassUUID: String, resp: HttpServletResponse) = {
	  if(courseClassUUID != null){
	    val courseClass = CourseClassRepo(courseClassUUID).get
	    val fileName = courseClass.getName + " - Audit - " + new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())+ ".xls"
	    resp.addHeader("Content-disposition", "attachment; filename=" + fileName)
	    resp.setContentType("application/vnd.ms-excel")
	    
      val parameters: HashMap[String, Object] = new HashMap()
      parameters.put("courseClassName", courseClass.getName)
      parameters.put("institutionBaseURL", InstitutionRepo(courseClass.getInstitutionUUID).get.getBaseURL)
  
      generateCourseClassAuditReportWithParameters(courseClass.getUUID, parameters)
	  }
    
    
  }

  private def generateCourseClassAuditReportWithParameters(courseClassUUID: String, parameters: HashMap[String, Object]) = {

    implicit def toCourseClassAuditTO(rs: ResultSet): CourseClassAuditTO =
      TOs.newCourseClassAuditTO(
        rs.getTimestamp("eventFiredAt"),
        rs.getString("eventType"),
        rs.getString("adminFullName"),
        rs.getString("adminUsername"),
        rs.getString("participantFullName"),
        rs.getString("participantUsername"),
        rs.getString("fromCourseClassName"),
        rs.getString("toCourseClassName"),
        rs.getString("fromState"),
        rs.getString("toState"),
        rs.getString("adminUUID"),
        rs.getString("participantUUID"),
        rs.getString("enrollmentUUID"),
        rs.getString("fromCourseClassUUID"),
        rs.getString("toCourseClassUUID"))

    val courseClassAuditTO = sql"""
		    select 
				esc.eventFiredAt as eventFiredAt,
				case    
					when esc.toState = ${EnrollmentState.deleted.toString} then 'deletion'  
					else 'stateChanged'  
				end as eventType,
			    admin.fullName as adminFullName,
			    adminPwd.username as adminUsername,
			    COALESCE(participant.fullname, 'a') as participantFullName,
			    COALESCE(participantPwd.username, participant.email) as participantUsername,
			    '-' as fromCourseClassName,
			    '-' as toCourseClassName,
				case    
					when esc.fromState = ${EnrollmentState.notEnrolled.toString} then '-'  
					when esc.fromState = ${EnrollmentState.cancelled.toString} then 'Cancelada'  
					when esc.fromState = ${EnrollmentState.requested.toString} then 'Requisitada'  
					when esc.fromState = ${EnrollmentState.denied.toString} then 'Negada'  
					when esc.fromState = ${EnrollmentState.enrolled.toString} then 'Matriculado'  
					when esc.fromState = ${EnrollmentState.deleted.toString} then 'Excluída'  
					else '?'   
				end as fromState,
				case    
					when esc.toState = ${EnrollmentState.notEnrolled.toString} then '-'  
					when esc.toState = ${EnrollmentState.cancelled.toString} then 'Cancelada'  
					when esc.toState = ${EnrollmentState.requested.toString} then 'Requisitada'  
					when esc.toState = ${EnrollmentState.denied.toString} then 'Negada'  
					when esc.toState = ${EnrollmentState.enrolled.toString} then 'Matriculado'  
					when esc.toState = ${EnrollmentState.deleted.toString} then 'Excluída'  
					else '?'   
				end as toState,
			    admin.uuid as adminUUID,
			    participant.uuid as participantUUID,
			    e.uuid as enrollmentUUID,
			    cc.uuid as fromCourseClassUUID,
			    '-' as toCourseClassUUID
			from EnrollmentStateChanged esc
			join Person admin on admin.uuid = esc.person_uuid
			join Password adminPwd on adminPwd.person_uuid = admin.uuid
			join Enrollment e on e.uuid = esc.enrollment_uuid
			join CourseClass cc on cc.uuid = e.class_uuid and cc.uuid = ${courseClassUUID}
			join Person participant on participant.uuid = e.person_uuid
			left join Password participantPwd on participantPwd.person_uuid = participant.uuid
		union
			select 
				et.eventFiredAt as eventFiredAt,
			    'transferred' as eventType,
			    admin.fullName as adminFullName,
			    adminPwd.username as adminUsername,
			    COALESCE(participant.fullname, 'a') as participantFullName,
			    COALESCE(participantPwd.username, participant.email) as participantUsername,
			    ccFrom.name as fromCourseClassName,
			    ccTo.name as toCourseClassName,
			    '-' as fromState,
			    '-' as toState,
			    admin.uuid as adminUUID,
			    participant.uuid as participantUUID,
			    e.uuid as enrollmentUUID,
			    ccFrom.uuid as fromCourseClassUUID,
			    ccTo.uuid as toCourseClassUUID
			from EnrollmentTransferred et
			join Person admin on admin.uuid = et.personUUID
			join Password adminPwd on adminPwd.person_uuid = admin.uuid 
			join Enrollment e on e.uuid = et.enrollmentUUID
			join CourseClass ccFrom on ccFrom.uuid = et.fromCourseClassUUID and (et.fromCourseClassUUID = ${courseClassUUID} or et.toCourseClassUUID = ${courseClassUUID})
			join CourseClass ccTo on ccTo.uuid = et.toCourseClassUUID and (et.toCourseClassUUID = ${courseClassUUID} or et.fromCourseClassUUID = ${courseClassUUID})
			join Person participant on participant.uuid = e.person_uuid
			left join Password participantPwd on participantPwd.person_uuid = participant.uuid
    where e.enrollmentSource = ${EnrollmentSource.WEBSITE.toString}
		order by eventFiredAt desc
	    """.map[CourseClassAuditTO](toCourseClassAuditTO)

    val cl = Thread.currentThread.getContextClassLoader
    val jasperStream = cl.getResourceAsStream("reports/courseClassAuditXLS.jasper")
    getReportBytesFromStream(courseClassAuditTO, parameters, jasperStream, "xls")
  }

}