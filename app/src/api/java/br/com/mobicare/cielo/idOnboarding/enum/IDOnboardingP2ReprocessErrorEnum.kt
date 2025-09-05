package br.com.mobicare.cielo.idOnboarding.enum

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class IDOnboardingP2ReprocessErrorEnum(
    val code: String,
    @StringRes val title: Int,
    @DrawableRes val img: Int
) {
    MISSING_DOCUMENT(
        code = "BRSCAN_DOCUMENTO_AUSENTE",
        title = R.string.id_onboarding_pictures_restart_warning_title,
        img = R.drawable.ic_07
    ),
    IRREGULAR_DOCUMENT(
        code = "BRSCAN_DOCUMENTO_IRREGULAR",
        title = R.string.id_onboarding_pictures_restart_warning_title,
        img = R.drawable.ic_07
    ),
    SELFIE_INVALID(
        code = "BRSCAN_SELFIE_INVALIDA",
        title = R.string.id_onboarding_pictures_restart_warning_title,
        img = R.drawable.ic_07
    ),
    WITHOUT_APPARENT_RISK(
        code = "BRSCAN_SEM_RISCO_APARENTE",
        title = R.string.id_onboarding_pictures_restart_warning_title,
        img = R.drawable.ic_07
    ),
    CENTRALIZE_FACE_CAPTURE(
        code = "UNICO_CENTRALIZE_ROSTO_CAPTURA",
        title = R.string.id_onboarding_pictures_selfie_guide_selfie_centralize_face,
        img = R.drawable.img_posicao_selfie
    ),
    CLOSE_CAMERA_FACE(
        code = "UNICO_APROXIME_ROSTO_CAMERA",
        title = R.string.id_onboarding_pictures_selfie_guide_selfie_close_face,
        img = R.drawable.img_posicao_selfie
    ),
    CAMERA_FACE_AWAY(
        code = "UNICO_AFASTE_ROSTO_CAMERA",
        title = R.string.id_onboarding_pictures_selfie_guide_selfie_face_away,
        img = R.drawable.img_selfie
    ),
    NOT_SATISFACTORY_LIGHTING(
        code = "UNICO_ILUMINACAO_NAO_SATISFATORIA",
        title = R.string.id_onboarding_pictures_selfie_guide_selfie_not_satisfactory_lighting,
        img = R.drawable.img_lampada
    ),
    PICTURE_OUTSIDE_FOCUS(
        code = "UNICO_IMAGEM_FORA_FOCO",
        title = R.string.id_onboarding_pictures_selfie_guide_selfie_outside_focus,
        img = R.drawable.img_selfie
    ),
    TILTED_FACE(
        code = "UNICO_ROSTO_INCLINADO",
        title = R.string.id_onboarding_pictures_selfie_guide_selfie_centralize_face,
        img = R.drawable.img_selfie
    ),
    FACE_SIDE(
        code = "UNICO_ROSTO_LADO",
        title = R.string.id_onboarding_pictures_selfie_guide_selfie_centralize_face,
        img = R.drawable.img_selfie
    ),
    REMOVE_GLASSES(
        code = "UNICO_RETIRE_OCULOS",
        title = R.string.id_onboarding_pictures_selfie_guide_selfie_remove_glasses,
        img = R.drawable.img_selfie_correta
    )
}
