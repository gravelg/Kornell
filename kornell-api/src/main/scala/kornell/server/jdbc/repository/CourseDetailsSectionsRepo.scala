package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.server.jdbc.SQL._
import kornell.core.util.UUID
import kornell.core.entity.CourseDetailsSection
import kornell.core.entity.CourseDetailsEntityType
import kornell.server.repository.TOs

object CourseDetailsSectionsRepo {

  def create(courseDetailsSection: CourseDetailsSection): CourseDetailsSection = {
    if (courseDetailsSection.getUUID == null){
      courseDetailsSection.setUUID(UUID.random)
    }    
    sql"""
    | insert into CourseDetailsSection (uuid,text,entityType,entityUUID,`index`,title) 
    | values(
    | ${courseDetailsSection.getUUID},
    | ${courseDetailsSection.getText},
    | ${courseDetailsSection.getEntityType.toString}, 
    | ${courseDetailsSection.getEntityUUID},
    | ${courseDetailsSection.getIndex},
    | ${courseDetailsSection.getTitle})""".executeUpdate
    
    courseDetailsSection
  }  
  
  def getForEntity(entityUUID: String, entityType: CourseDetailsEntityType) = {
    TOs.newCourseDetailsSectionsTO(sql"""
      select * from CourseDetailsSection where entityUUID = ${entityUUID} and entityType = ${entityType.toString}
    """.map[CourseDetailsSection](toCourseDetailsSection))
  }
  
}