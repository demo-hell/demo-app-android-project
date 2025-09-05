package br.com.mobicare.cielo.simulator.analytics

import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

class SimulatorAnalytics {

    private var initialized = false
    private var serviceAvailable = true
    private var responsibility = ""
    private var brand = ""
    private var saleValue: Double = 0.0

    fun setServiceAvailable(serviceAvailable: Boolean) {
        this.serviceAvailable = serviceAvailable
    }

    fun setResponsibility(responsibility: String) {
        this.responsibility = responsibility
    }

    fun logScreenView(screen: String, className: Class<Any>) =
        Analytics.trackScreenView(screen, className)

    fun logToggleResponsibility(label: String) {
        responsibility = label
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SIMULADOR_VENDAS),
            action = listOf(Action.FORMULARIO, Action.SELECAO),
            label = listOf(Label.BOTAO, RESPONSABILIDADE, label)
        )
    }

    fun logSelectBrand(brand: String) {
        this.brand = brand
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SIMULADOR_VENDAS),
            action = listOf(Action.FORMULARIO, Action.SELECAO),
            label = listOf(Label.FILTRO, BANDEIRA_CLIENTE, brand)
        )
    }

    fun logClickButtonCalculate(saleValue: Double) {
        this.saleValue = saleValue
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SIMULADOR_VENDAS),
            action = listOf(Action.FORMULARIO, Action.CLIQUE),
            label = listOf(Label.BOTAO, CALCULAR)
        )
    }

    fun logServiceAvailable() {
        if (!initialized){
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, SIMULADOR_VENDAS),
                action = listOf(Action.CALLBACK, ELEGIBILIDADE),
                label = listOf(
                    if (serviceAvailable) Label.SUCESSO else Label.ERRO,
                    if (serviceAvailable) "" else NAO_ELEGIVEL
                )
            )
            initialized = true
        }
    }

    fun logCallbackCalculate(error: ErrorMessage? = null) {
        if (saleValue > 0.0) Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SIMULADOR_VENDAS),
            action = listOf(
                Action.FORMULARIO,
                Action.CALLBACK,
                CALCULAR,
                responsibility,
                brand,
                saleValue.toInt().toString()
            ),
            label = listOf(
                if (error != null) Label.ERRO else Label.SUCESSO,
                error?.errorMessage ?: "",
                error?.httpStatus?.toString() ?: ""
            )
        )
    }

    fun logShowResult() =
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SIMULADOR_VENDAS),
            action = listOf(Action.EXIBICAO),
            label = listOf(RESULTADO_SIMULACAO)
        )

    companion object {
        const val SCREENVIEW_FORMULARIO = "/simulador-de-vendas/formulario"
        const val SCREENVIEW_RESULTADO = "/simulador-de-vendas/resultado"

        const val SIMULADOR_VENDAS = "simulador de vendas"
        const val RESPONSABILIDADE = "responsabilidade"
        const val LOJA = "loja"
        const val CLIENTE = "cliente"
        const val BANDEIRA_CLIENTE = "bandeira cliente"
        const val CALCULAR = "calcular"
        const val ELEGIBILIDADE = "elegibilidade"
        const val NAO_ELEGIVEL = "nao elegivel"
        const val RESULTADO_SIMULACAO = "resultado da simulacao"
    }

}