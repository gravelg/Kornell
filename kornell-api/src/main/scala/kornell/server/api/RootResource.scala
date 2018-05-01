package kornell.server.api

import javax.ws.rs.{GET, Path, Produces}
import kornell.server.util.Settings._

@Path("")
class RootResource {
  val buildDescription: String = BUILD_NUM.getOpt.orElse("development_build").get
  val buildDate: String = BUILT_ON.getOpt.orElse("now").get

  @Produces(Array("text/plain"))
  @GET
  def get: String =
    s"""|Welcome to Kornell API
    |
    |build number: $buildDescription
    |
    |build date: $buildDate
    |"""
      .stripMargin
}
