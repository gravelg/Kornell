package kornell.server.util

import org.apache.commons.codec.digest.DigestUtils


object SHA256 {
	def apply(plain: String): String = DigestUtils.sha256Hex(plain)
}