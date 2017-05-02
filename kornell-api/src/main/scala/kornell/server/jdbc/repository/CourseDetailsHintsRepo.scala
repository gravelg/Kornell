package kornell.server.jdbc.repository

import scala.collection.JavaConverters._
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.core.util.UUID

import kornell.core.entity.CourseDetailsHint
import kornell.core.entity.CourseDetailsEntityType
import kornell.server.repository.TOs

object CourseDetailsHintsRepo {

  def create(courseDetailsHint: CourseDetailsHint): CourseDetailsHint = {
    if (courseDetailsHint.getUUID == null){
      courseDetailsHint.setUUID(UUID.random)
    }    
    sql"""
    | insert into CourseDetailsHint (uuid,text,entityType,entityUUID,`index`,fontAwesomeClassName) 
    | values(
    | ${courseDetailsHint.getUUID},
    | ${courseDetailsHint.getText},
    | ${courseDetailsHint.getEntityType.toString}, 
    | ${courseDetailsHint.getEntityUUID},
    | ${courseDetailsHint.getIndex},
    | ${courseDetailsHint.getFontAwesomeClassName})""".executeUpdate
    
    courseDetailsHint
  }
  
  def listForEntity(entityUUIDs: List[String], entityType: CourseDetailsEntityType): Map[String, List[CourseDetailsHint]] = {
    sql"""
      select * from CourseDetailsHint where entityUUID in (${entityUUIDs mkString ","}) and entityType = ${entityType.toString}
    """.map[CourseDetailsHint].groupBy { x => x.getEntityUUID }
  }
  
  def getForEntity(entityUUID: String, entityType: CourseDetailsEntityType) = {
    TOs.newCourseDetailsHintsTO(sql"""
      select * from CourseDetailsHint where entityUUID = ${entityUUID} and entityType = ${entityType.toString}
    """.map[CourseDetailsHint](toCourseDetailsHint))
  }
}