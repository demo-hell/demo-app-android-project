package br.com.mobicare.cielo.selfieChallange.utils

enum class SelfieOperation(val id: String) {
    NONE(""),
    CHANGE_PASSWORD("change_password"),
    NEW_DEVICE("new_device"),
    UPDATE_DATA("update_data"),
    CRD_FACE_ID("face_id_enrollment_crd")
}