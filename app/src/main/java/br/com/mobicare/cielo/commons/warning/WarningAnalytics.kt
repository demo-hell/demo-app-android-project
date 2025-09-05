package br.com.mobicare.cielo.commons.warning

import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label

class WarningAnalytics {

    fun logShowModal(name: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, DYNAMIC_MODAL),
            action = listOf(DYNAMIC_MODAL, modalName(name, MAXIMUM_ID_LENGTH_FOR_SHOW), Action.SHOW),
            label = listOf(modalName(name, MAXIMUM_ID_LENGTH))
        )
    }

    fun logClickMainAction(name: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, DYNAMIC_MODAL),
            action = listOf(DYNAMIC_MODAL, modalName(name, MAXIMUM_ID_LENGTH_FOR_ACTION), Action.CLIQUE),
            label = listOf(Label.BOTAO, MAIN_ACTION)
        )
    }

    fun logCloseModal(name: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, DYNAMIC_MODAL),
            action = listOf(DYNAMIC_MODAL, modalName(name, MAXIMUM_ID_LENGTH_FOR_ACTION), Action.CLIQUE),
            label = listOf(Label.BOTAO, Action.FECHAR)
        )
    }

    private fun modalName(name: String?, length: Int): String {
        return if (name != null) {
            if (name.length > length) name.subSequence(START, length).toString()
            else name
        } else ""
    }

    companion object {
        const val DYNAMIC_MODAL = "modal dinamica"
        const val MAIN_ACTION = "acao principal"
        const val START = 0
        const val MAXIMUM_ID_LENGTH_FOR_SHOW = 70
        const val MAXIMUM_ID_LENGTH_FOR_ACTION = 74
        const val MAXIMUM_ID_LENGTH = 100
    }
}