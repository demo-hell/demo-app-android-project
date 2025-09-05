package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.quantidade

import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.machine.domain.MachineItemOfferResponse

interface InstalacaoMaquinaAdicionalQuantidadeContract {

    interface View : IAttached {
        fun loadImage(url: String)
        fun setAmount(amount: Int)
        fun setTitle(title: String)
        fun setRentalAmount(value: Double)
        fun setNotification(text: String)
        fun setEnablePlusButton(isEnable: Boolean)
        fun setEnableMinusButton(isEnable: Boolean)
        fun goToNextScreen(title: String?, value: Double?, amount: Int)
        fun isEnabledNextButton(isEnabled: Boolean)
    }

    interface Presenter {
        fun setData(data: MachineItemOfferResponse)
        fun minusButtonClicked()
        fun plusButtonClicked()
        fun onNextButtonClicked()
    }

}