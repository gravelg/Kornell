package kornell.server.repository.jdbc

import kornell.core.entity.Person
import kornell.server.repository.Entities
import kornell.server.repository.jdbc.SQLInterpolation._

import kornell.server.repository.Entities._
class People {
  def createTestPerson(fullName: String): PersonRepository = {
    val uuid = randomUUID
    sql"insert into Person(uuid, fullName) values ($uuid,$fullName)".executeUpdate
    PersonRepository(uuid)
  }

  def createPerson(email: String, fullName:String, 
      company: String="", title: String="", sex: String="", 
      birthDate: String="1800-01-01", confirmation: String = "") = {
    
    val uuid = randomUUID
    sql"""
    	insert into Person(uuid, fullName, email,
    		company, title, sex, birthDate, confirmation
    	) values ($uuid, $fullName, $email, 
    		$company, $title, $sex, $birthDate, $confirmation)
    """.executeUpdate
    PersonRepository(uuid)
  }
}

object People {
  def apply() = new People()
}
