package br.com.mobicare.cielo.pix.ui.extract.home

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.pix.domain.PixExtractReceipt
import br.com.mobicare.cielo.pix.domain.PixExtractResponse

interface PixExtractContract {

    interface View: BaseView {
        fun onSuccessGetPixBalance(card: Card, isShow: Boolean)
        fun onShowLoadingBalance()
        fun onHideLoadingBalance()
        fun onErrorGetPixBalance(errorMessage: ErrorMessage?)
        fun onUserData(userName: String?, document: String, ec: String)
        fun onUserDataHideDocument(userName: String?, ec: String)
        fun onUserDataHideEC(userName: String?, document: String)
        fun onUserDataHideDocumentAndEC(userName: String?)
    }

    interface Presenter {
        fun onGetCard()
        fun onGetUserData()
        fun onSaveShowBalanceValue(isShow: Boolean)
        fun onResume()
        fun onPause()
    }
}