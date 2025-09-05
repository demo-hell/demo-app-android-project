package br.com.mobicare.cielo.posVirtual.presentation.home.utils

import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtualProduct

sealed class PosVirtualProductClickAction {
    abstract class Enabled : PosVirtualProductClickAction()
    data class TapOnPhone(val hasCardReader: Boolean) : Enabled()
    object SuperLink : Enabled()
    data class Pix(val logicalNumber: String?) : Enabled()
    data class RequestDetails(val product: PosVirtualProduct) : PosVirtualProductClickAction()
    object UnavailableOption : PosVirtualProductClickAction()
}
