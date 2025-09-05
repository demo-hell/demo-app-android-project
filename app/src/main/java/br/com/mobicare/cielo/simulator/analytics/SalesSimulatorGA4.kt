package br.com.mobicare.cielo.simulator.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import br.com.mobicare.cielo.simulator.simulation.domain.model.PaymentType
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation
import com.google.firebase.analytics.FirebaseAnalytics

class SalesSimulatorGA4 {

    fun logScreenView(screenPath: String) = Analytics.GoogleAnalytics4Tracking.trackScreenView(screenPath)

    fun logException(screenPath: String, error: NewErrorMessage?) {
        val description = error?.let {
            it.message.ifEmpty { it.httpCode.toString() }
        } ?: Text.EMPTY

        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.Exception.EXCEPTION_EVENT, eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenPath,
                GoogleAnalytics4Events.Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                GoogleAnalytics4Events.Exception.STATUS_CODE to error?.httpCode.toStringOrEmpty()
            )
        )
    }

    fun logDisplayContent(
        screenName: String,
        contentComponent: String? = null,
        description: String,
        contentType: String
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT, eventsMap = listOfNotNull(
                ScreenView.SCREEN_NAME to screenName,
                contentComponent?.let { Navigation.CONTENT_COMPONENT to contentComponent },
                GoogleAnalytics4Events.Exception.DESCRIPTION to description,
                Navigation.CONTENT_TYPE to contentType
            ).toMap()
        )
    }

    fun logResultDisplayContent(paymentType: PaymentType?, simulation: Simulation) {
        logDisplayContent(
            screenName = SCREEN_VIEW_SIMULATOR_RESULT.format(paymentType.toScreenName()),
            contentType = GoogleAnalytics4Events.Exception.DESCRIPTION,
            description = "${paymentType.toScreenName()}_${simulation.toDescription()}",
        )
    }

    private fun Simulation?.toDescription() = this?.let {
        when (it.flexibleTerm) {
            true -> WITH_RA
            else -> WITHOUT_RA
        }
    }

    fun logClick(
        screenName: String,
        contentName: String,
        contentType: String = BUTTON,
        contentComponent: String? = null,
        itemName: String? = null
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.Click.CLICK_EVENT, eventsMap = listOfNotNull(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to contentType,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase(),
                contentComponent?.let { Navigation.CONTENT_COMPONENT to contentComponent },
                itemName?.let { FirebaseAnalytics.Param.ITEM_NAME to itemName.normalizeToLowerSnakeCase() }).toMap()
        )
    }

    fun logResultRedoButtonClick(paymentType: PaymentType?) {
        logClick(
            screenName = SCREEN_VIEW_SIMULATOR_RESULT.format(paymentType.toScreenName()),
            contentName = REDO_SIMULATION
        )
    }

    private fun PaymentType?.toScreenName() = this?.let {
        when (it.productDescription?.toLowerCasePTBR()) {
            API_CASH_SALE_TYPE -> CASH_SALE_TYPE
            API_DEBIT_SALE_TYPE -> DEBIT_SALE_TYPE
            API_INSTALLMENT_SALE_TYPE -> INSTALLMENT_SALE_TYPE
            else -> productDescription.toLowerCasePTBR().normalizeToLowerSnakeCase()
        }
    }

    companion object {
        private const val SALES = "vendas"
        private const val SIMULATOR = "simulador"
        private const val SALE_TYPE = "tipo_de_venda"
        private const val RESULT = "resultado"
        private const val REDO_SIMULATION = "refazer_simulacao"

        private const val API_CASH_SALE_TYPE = "crédito à vista"
        private const val API_DEBIT_SALE_TYPE = "débito"
        private const val API_INSTALLMENT_SALE_TYPE = "parcelado loja"
        private const val CASH_SALE_TYPE = "credito"
        private const val DEBIT_SALE_TYPE = "debito"
        private const val INSTALLMENT_SALE_TYPE = "credito_parcelado"
        private const val WITH_RA = "com_ra"
        private const val WITHOUT_RA = "sem_ra"

        internal const val SIMULATE = "simular"


        const val SCREEN_VIEW_SIMULATOR = "/$SALES/$SIMULATOR"
        const val SCREEN_VIEW_SIMULATOR_RESULT = "/$SALES/$SIMULATOR/${RESULT}_%s"
        const val SCREEN_VIEW_SIMULATOR_SALE_TYPE = "/$SALES/$SIMULATOR/$SALE_TYPE"
    }

}