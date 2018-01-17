package kornell.server.ws.rs.writer

import javax.ws.rs.ext.Provider
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.MediaType

@Provider
class BooleanWriter extends MessageBodyWriter[Boolean] {
  override def getSize(b: Boolean,
    aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType) = -1L

  override def isWriteable(aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType) = "application/boolean".equalsIgnoreCase(mediaType.toString())

  override def writeTo(b: Boolean,
    aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType,
    httpHeaders: MultivaluedMap[java.lang.String, java.lang.Object],
    out: java.io.OutputStream) {
    out.write(b.toString.getBytes)
  }
}