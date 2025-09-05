package br.com.mobicare.cielo.pix.ui.transfer.amount

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class PixEnterTransferAmountPresenter(
    private var view: PixEnterTransferAmountContract.View,
    private var userPreferences: UserPreferences
) :
    PixEnterTransferAmountContract.Presenter {

    override fun onSaveShowBalanceValue() {
        val isShow = isShowBalanceValue().not()
        userPreferences.saveShowBalanceValue(isShow)
        view.onBalanceView(isShow)
    }

    override fun isShowBalanceValue(): Boolean = userPreferences.isShowBalanceValue
}