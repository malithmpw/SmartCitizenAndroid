package cmb.reporter.app.smartcitizen.security

import java.math.BigInteger
import java.security.MessageDigest

object EncryptUtil {
    fun encryptPassword(rawPassword: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(rawPassword.toByteArray())).toString(16).padStart(32, '0')
    }
}