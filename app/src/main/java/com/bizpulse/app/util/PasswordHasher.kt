package com.bizpulse.app.util
import java.security.MessageDigest
object PasswordHasher {
    private const val SALT = "bizpulse_salt_v1"
    fun hash(password: String): String = MessageDigest.getInstance("SHA-256")
        .digest((password + SALT).toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
    fun verify(password: String, storedHash: String): Boolean = MessageDigest.isEqual(hash(password).toByteArray(), storedHash.toByteArray())
}
