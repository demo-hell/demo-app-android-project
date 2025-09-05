package br.com.mobicare.cielo.interactbannersoffers.termoAceite.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import java.io.Serializable

private const val GENERIC_TITLE = "Solicitação em processamento!"
private const val GENERIC_SUBTITLE = "Sua solicitação para garantir a oferta está em processamento, o prazo de confirmação é de até 48 horas."

@Keep
data class TermoAceiteObj(
    val bannerId: Int,
    val name: String,
    @DrawableRes val banner: Int,
    val title: String,
    val subtitle: String,
    val url: String,
    val customMessageSuccess: CustomMessageSuccess
): Serializable

@Keep
data class CustomMessageSuccess(
    val title: String = GENERIC_TITLE,
    val subtitle: String = GENERIC_SUBTITLE
): Serializable