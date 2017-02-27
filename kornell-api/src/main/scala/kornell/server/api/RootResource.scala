package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.GET
import java.util.Properties
import kornell.server.jdbc.SQL._
import kornell.server.util.Settings._

@Path("")
class RootResource { 
  val buildDescription = BUILD_NUM.getOpt.orElse("development_build").get
  val buildDate = BUILT_ON.getOpt.orElse("now").get
  
  @Produces(Array("text/plain"))
  @GET
  def get = 
    s"""|Welcome to Kornell API  
	  |
	  |build number: $buildDescription
	  |
    |build date: $buildDate
	  |"""
    .stripMargin
}