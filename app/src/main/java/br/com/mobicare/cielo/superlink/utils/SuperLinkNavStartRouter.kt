package br.com.mobicare.cielo.superlink.utils

import br.com.mobicare.cielo.R

class SuperLinkNavStartRouter {

    private var flowStartOrigin: FlowStartArg? = null

    val isFlowOriginFromPosVirtual get() = flowStartOrigin == FlowStartArg.POS_VIRTUAL

    val startDestinationResId get() =
        if (isFlowOriginFromPosVirtual)
            R.id.tipoVendaPagamentoPorLinkFragment
        else
            R.id.linkPaymentFragment

    fun setFlowStartOrigin(value: String) {
        flowStartOrigin = FlowStartArg.find(value)
    }

    enum class FlowStartArg {
        HOME, POS_VIRTUAL;

        companion object {
            const val KEY = "FlowArg.KEY"
            fun find(value: String) = values().firstOrNull { it.name == value }
        }
    }

}