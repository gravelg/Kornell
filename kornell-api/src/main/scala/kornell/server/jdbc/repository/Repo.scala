package kornell.server.jdbc.repository

/**
 * Default interface for a repository of entities.
 * Not to be confused with a repository of content [ContentRepository]
 *
 */
trait Repo[E] {
  def first(uuid: String): Option[E]
}
