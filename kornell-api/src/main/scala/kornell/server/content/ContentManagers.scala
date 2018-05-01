package kornell.server.content

import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.core.entity.RepositoryType

object ContentManagers {

  def forRepository(repoUUID: String): SyncContentManager =
    ContentRepositoriesRepo
      .getByRepositoryUUID(repoUUID)
      .map {
        case x if x.getRepositoryType == RepositoryType.S3 => new S3ContentManager(x)
        case x if x.getRepositoryType == RepositoryType.FS => new FSContentManager(x)
        case _ => throw new IllegalStateException("Unknown repository type")
      }.getOrElse(throw new IllegalArgumentException(s"Could not find repository [$repoUUID]"))
}
