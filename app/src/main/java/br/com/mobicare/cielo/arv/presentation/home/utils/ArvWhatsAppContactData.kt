package br.com.mobicare.cielo.arv.presentation.home.utils

import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import com.google.gson.Gson

data class ArvWhatsAppContactData(
    val phoneNumber: String = PHONE_NUMBER,
    val message: String = MESSAGE,
) {
    companion object {
        private const val PHONE_NUMBER = "551130035525"
        private val MESSAGE =
            """
            Antecipação de Recebíveis. Quero falar com um atendente para me explicar melhor as taxas e me ajudar na contratação.
            """.trimIndent()

        fun fromJson(json: String): ArvWhatsAppContactData =
            try {
                Gson().fromJson(json, ArvWhatsAppContactData::class.java)
            } catch (e: Exception) {
                e.message.logFirebaseCrashlytics()
                ArvWhatsAppContactData()
            }
    }
}
