package kornell.server.ws.rs.writer

import java.io.{OutputStream, OutputStreamWriter}

import com.google.web.bindery.autobean.shared.{AutoBeanCodex, AutoBeanUtils}
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.{MediaType, MultivaluedMap, Response}
import javax.ws.rs.ext.{MessageBodyWriter, Provider}
import kornell.server.util.{Err, Failed, Passed}

@Provider
class AutoBeanWriter extends MessageBodyWriter[Any] {
  override def getSize(t: Any,
    aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType): Long = -1L

  override def isWriteable(aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType): Boolean = mediaType.toString.contains("vnd.kornell")

  override def writeTo(t: Any,
    aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType,
    httpHeaders: MultivaluedMap[java.lang.String, java.lang.Object],
    out: java.io.OutputStream): Unit = {
    t match {
      case Some(thing) => outputPayload(thing, out)
      case Passed(block) => outputPayload(block, out)
      case Failed(err) => spitErr(err)
      case _ => outputPayload(t, out)
    }
  }

  def spitErr(e: Err): Nothing = {
    println(e)
    val response = Response.status(Response.Status.BAD_REQUEST).build()
    throw new WebApplicationException(response)
  }

  private def outputPayload(content: Any, out: OutputStream): Unit = {
    val bean = AutoBeanUtils.getAutoBean(content)
    val payload = AutoBeanCodex.encode(bean).getPayload
    val writer = new OutputStreamWriter(out, "UTF-8")
    writer.write(payload)
    writer.flush()
    writer.close()
  }

}