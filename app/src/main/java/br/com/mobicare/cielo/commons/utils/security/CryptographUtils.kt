package br.com.mobicare.cielo.commons.utils.security

import android.security.keystore.KeyProperties.DIGEST_SHA256
import br.com.mobicare.cielo.pix.constants.EMPTY
import java.security.MessageDigest

private const val HEX_FORMAT = "%02x"

fun String.toSha256(): String {
    return hashString(this, DIGEST_SHA256)
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold(EMPTY) { str, it -> str + HEX_FORMAT.format(it) }
}