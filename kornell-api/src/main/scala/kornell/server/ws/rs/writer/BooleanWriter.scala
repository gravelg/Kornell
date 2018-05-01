package kornell.server.ws.rs.writer

import javax.ws.rs.core.{MediaType, MultivaluedMap}
import javax.ws.rs.ext.{MessageBodyWriter, Provider}

@Provider
class BooleanWriter extends MessageBodyWriter[Boolean] {
  override def getSize(b: Boolean,
    aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType): Long = -1L

  override def isWriteable(aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType): Boolean = "application/boolean".equalsIgnoreCase(mediaType.toString)

  override def writeTo(b: Boolean,
    aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType,
    httpHeaders: MultivaluedMap[java.lang.String, java.lang.Object],
    out: java.io.OutputStream): Unit = {
      out.write(b.toString.getBytes)
  }
}