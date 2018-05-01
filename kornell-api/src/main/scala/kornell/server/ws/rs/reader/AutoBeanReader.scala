package kornell.server.ws.rs.reader

import java.io.InputStream
import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.nio.charset.CodingErrorAction

import com.google.web.bindery.autobean.shared.{AutoBeanCodex, AutoBeanFactory}
import javax.ws.rs.core.{MediaType, MultivaluedMap}
import javax.ws.rs.ext.MessageBodyReader

import scala.io.{Codec, Source}

trait AutoBeanReader extends MessageBodyReader[Any] {
  def getAutoBeanFactory: AutoBeanFactory
  def getTypePrefix: String

  override def isReadable(
    arg0: Class[_],
    arg1: Type,
    arg2: Array[Annotation],
    mediaType: MediaType): Boolean =
    mediaType.toString.startsWith(getTypePrefix)

  override def readFrom(
    clazz: Class[Any],
    arg1: Type,
    arg2: Array[Annotation],
    arg3: MediaType,
    arg4: MultivaluedMap[String, String],
    in: InputStream): Any = {

    val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.IGNORE)

    val src = Source.fromInputStream(in)(codec)
    if (src.nonEmpty) {
      val lines = src.getLines()
      val text = lines.mkString("")
      val bean = AutoBeanCodex.decode(getAutoBeanFactory, clazz, text)
      bean.as
    } else null

  }

}