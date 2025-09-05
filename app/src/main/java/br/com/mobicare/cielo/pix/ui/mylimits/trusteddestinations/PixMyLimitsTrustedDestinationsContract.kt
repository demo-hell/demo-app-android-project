package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.PixTrustedDestinationResponse

interface PixMyLimitsTrustedDestinationsContract {

    interface View : BaseView {
        fun onSuccessTrustedDestinations(trustedDestinations: List<PixTrustedDestinationResponse>)
        fun onNoTrustedDestinations()
        fun onShowDetails(trustedDestination: PixTrustedDestinationResponse)
    }

    interface Presenter {
        fun getTrustedDestinations()
        fun onResume()
        fun onPause()
    }
}