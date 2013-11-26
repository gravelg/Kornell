package kornell.server.repository

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.to.TOFactory
import kornell.core.entity.Institution
import kornell.core.entity.Registration
import kornell.core.to.RegistrationsTO
import scala.collection.JavaConverters._
import java.util.Date
import java.math.BigDecimal
import java.sql.ResultSet
import kornell.core.to.CourseTO
import kornell.server.repository.s3.S3
import kornell.core.util.StringUtils
import kornell.core.entity.EnrollmentState

//TODO: Consider turning to Object
object TOs {
  val tos = AutoBeanFactorySource.create(classOf[TOFactory])

  def newUserInfoTO = tos.newUserInfoTO.as
  def newRegistrationsTO: RegistrationsTO = tos.newRegistrationsTO.as
  def newRegistrationsTO(registrationsWithInstitutions: Map[Registration, Institution]): RegistrationsTO = {
    val registrations = newRegistrationsTO
    registrations.setRegistrationsWithInstitutions(registrationsWithInstitutions asJava)
    registrations
  }
 

  def newCoursesTO(l: List[CourseTO]) = {
    val to = tos.newCoursesTO.as
    to.setCourses(l asJava)
    to
  } 
  
  def newRegistrationRequestTO = tos.newRegistrationRequestTO.as
}