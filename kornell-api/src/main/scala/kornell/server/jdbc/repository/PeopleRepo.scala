package kornell.server.jdbc.repository

import java.sql.ResultSet
import java.util.concurrent.TimeUnit.MINUTES

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import kornell.core.entity.{AuditedEntityType, Person, RegistrationType}
import kornell.core.to.{PeopleTO, PersonTO}
import kornell.core.util.StringUtils.isSome
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.Entities
import kornell.server.repository.Entities.randUUID
import kornell.server.repository.TOs.newPeopleTO

object PeopleRepo {

  implicit def toString(rs: ResultSet): String = rs.getString(1)

  type InstitutionKey = (String, String)

  val usernameLoader: CacheLoader[InstitutionKey, Option[Person]] = new CacheLoader[InstitutionKey, Option[Person]]() {
    override def load(instKey: InstitutionKey): Option[Person] = lookupByUsername(instKey._1, instKey._2)
  }

  val cpfLoader: CacheLoader[InstitutionKey, Option[Person]] = new CacheLoader[InstitutionKey, Option[Person]]() {
    override def load(instKey: InstitutionKey): Option[Person] = lookupByCPF(instKey._1, instKey._2)
  }

  val emailLoader: CacheLoader[InstitutionKey, Option[Person]] = new CacheLoader[InstitutionKey, Option[Person]]() {
    override def load(instKey: InstitutionKey): Option[Person] = lookupByEmail(instKey._1, instKey._2)
  }

  val uuidLoader: CacheLoader[String, Option[Person]] = new CacheLoader[String, Option[Person]]() {
    override def load(uuid: String): Option[Person] = lookupByUUID(uuid)
  }

  val timezoneLoader: CacheLoader[String, Option[String]] = new CacheLoader[String, Option[String]]() {
    override def load(personUUID: String): Option[String] = lookupTimezoneByUUID(personUUID)
  }

  val DEFAULT_CACHE_SIZE = 1000

  val cacheBuilder: CacheBuilder[AnyRef, AnyRef] = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, MINUTES)
    .maximumSize(1000)

  val usernameCache: LoadingCache[(String, String), Option[Person]] = cacheBuilder.build(usernameLoader)
  val cpfCache: LoadingCache[(String, String), Option[Person]] = cacheBuilder.build(cpfLoader)
  val emailCache: LoadingCache[(String, String), Option[Person]] = cacheBuilder.build(emailLoader)
  val uuidCache: LoadingCache[String, Option[Person]]  = cacheBuilder.build(uuidLoader)
  val timezoneCache: LoadingCache[String, Option[String]] = cacheBuilder.build(timezoneLoader)

  def clearCache(): Unit = {
    usernameCache.invalidateAll()
    cpfCache.invalidateAll()
    emailCache.invalidateAll()
    uuidCache.invalidateAll()
    timezoneCache.invalidateAll()
  }

  def getByUsername(institutionUUID: String, username: String): Option[Person] = Option(institutionUUID, username) flatMap usernameCache.get
  def getByEmail(institutionUUID: String, email: String): Option[Person] = Option(institutionUUID, email) flatMap emailCache.get
  def getByCPF(institutionUUID: String, cpf: String): Option[Person] = Option(institutionUUID, cpf) flatMap cpfCache.get
  def getByUUID(uuid: String): Option[Person] = Option(uuid) flatMap uuidCache.get
  def getTimezoneByUUID(personUUID: String): Option[String] = timezoneCache.get(personUUID)

  def lookupByUsername(institutionUUID: String, username: String): Option[Person] = sql"""
    select p.* from Person p
    join Password pwd
    on p.uuid = pwd.personUUID
    where pwd.username = $username
    and p.institutionUUID = $institutionUUID
  """.first[Person]

  def lookupByCPF(institutionUUID: String, cpf: String): Option[Person] = sql"""
    select p.* from Person p
    where p.cpf = $cpf
    and p.institutionUUID = $institutionUUID
  """.first[Person]

  def lookupByEmail(institutionUUID: String, email: String): Option[Person] = sql"""
    select p.* from Person p
    where p.email = $email
    and p.institutionUUID = $institutionUUID
  """.first[Person]

  def lookupByUUID(uuid: String): Option[Person] = sql"""
    select p.* from Person p
    where p.uuid = $uuid
  """.first[Person]

  def lookupTimezoneByUUID(personUUID: String): Option[String] =
    sql"""select i.timeZone from Person p left join Institution i on p.institutionUUID = i.uuid where p.uuid = ${personUUID}""".first[String]

  def get(institutionUUID: String, any: String): Option[Person] = get(institutionUUID, any, any, any)

  def get(institutionUUID: String, cpf: String, email: String): Option[Person] =
    getByUsername(institutionUUID, {
      if (cpf == null)
        cpf
      else
        email
    })
      .orElse(getByCPF(institutionUUID, cpf))
      .orElse(getByEmail(institutionUUID, email))

  def get(institutionUUID: String, username: String, cpf: String, email: String): Option[Person] =
    getByUsername(institutionUUID, username)
      .orElse(getByCPF(institutionUUID, cpf))
      .orElse(getByEmail(institutionUUID, email))

  def findBySearchTerm(institutionUUID: String, search: String): PeopleTO = {
    newPeopleTO(
      sql"""
        | select p.*,
        | if(pw.username is not null, pw.username, p.email) as username
        | from Person p
        | left join Password pw on p.uuid = pw.personUUID
        | where (pw.username like ${"%" + search + "%"}
        | or p.fullName like ${"%" + search + "%"}
        | or p.email like ${"%" + search + "%"}
        | or p.cpf like ${"%" + search + "%"})
        | and p.institutionUUID = ${institutionUUID}
        | order by p.email, p.cpf
        | limit 8
      """.map[PersonTO](toPersonTO))
  }

  def createPerson(institutionUUID: String = null, email: String = null, fullName: String = null, cpf: String = null): Person =
    create(Entities.newPerson(institutionUUID = institutionUUID, fullName = fullName, email = email, cpf = cpf, registrationType = RegistrationType.email))

  def createPersonCPF(institutionUUID: String, cpf: String, fullName: String): Person =
    create(Entities.newPerson(institutionUUID = institutionUUID, fullName = fullName, cpf = cpf, registrationType = RegistrationType.cpf))

  def createPersonUsername(institutionUUID: String, username: String, fullName: String, institutionRegistrationPrefixUUID: String): Person = {
    val p = create(Entities.newPerson(institutionUUID = institutionUUID, fullName = fullName, registrationType = RegistrationType.username, institutionRegistrationPrefixUUID = institutionRegistrationPrefixUUID))
    if (isSome(username)) usernameCache.put((p.getInstitutionUUID, username), Some(p))
    p
  }

  def create(person: Person): Person = {
    if (person.getUUID == null)
      person.setUUID(randUUID)
    sql"""
      insert into Person(uuid, fullName, email, cpf, institutionUUID, registrationType, institutionRegistrationPrefixUUID)
        values (${person.getUUID},
               ${person.getFullName},
               ${person.getEmail},
               ${person.getCPF},
               ${person.getInstitutionUUID},
               ${person.getRegistrationType.toString},
               ${person.getInstitutionRegistrationPrefixUUID})
    """.executeUpdate

    //log entity creation
    EventsRepo.logEntityChange(person.getInstitutionUUID, AuditedEntityType.person, person.getUUID, null, person)

    updateCaches(person)
    person
  }

  def updateCaches(p: Person): Unit = {
    val op = Some(p)
    uuidCache.put(p.getUUID, op)
    if (isSome(p.getCPF)) cpfCache.put((p.getInstitutionUUID, p.getCPF), op)
    if (isSome(p.getEmail)) emailCache.put((p.getInstitutionUUID, p.getEmail), op)
    if (timezoneCache.get(p.getUUID).isDefined) {
      timezoneCache.put(p.getUUID, lookupTimezoneByUUID(p.getUUID))
    }
  }

  def invalidateCache(p: Person): Unit = {
    if (isSome(p.getCPF)) cpfCache.invalidate((p.getInstitutionUUID, p.getCPF))
    if (isSome(p.getEmail)) emailCache.invalidate((p.getInstitutionUUID, p.getEmail))
    uuidCache.invalidate(p.getUUID)
  }

}
