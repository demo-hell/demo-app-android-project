package br.com.mobicare.cielo.idOnboarding.enum

enum class IDOCheckpointP2(val code: Int) {
    NONE(0),
    DOCUMENT_PHOTO_UPLOADED(100),
    SELF_PHOTO_UPLOADED(200),
    ALLOWME_SENT(300),
    POLICY_2_REQUESTED(400),
    POLICY_2_RESPONSE(500);

    companion object {
        fun fromCode(code: Int?) = values().firstOrNull{ it.code == code } ?: NONE
    }
}