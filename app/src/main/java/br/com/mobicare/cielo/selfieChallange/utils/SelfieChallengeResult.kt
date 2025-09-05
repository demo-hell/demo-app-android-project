package br.com.mobicare.cielo.selfieChallange.utils

import br.com.mobicare.cielo.commons.constants.Intent.FILE_TYPE_JPG
import java.io.Serializable

data class SelfieChallengeResult(
    var photo64: String? = null,
    var imageFileType: String = FILE_TYPE_JPG.uppercase(),
    var faceIdToken: String?
): Serializable
