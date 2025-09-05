package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address

import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meuCadastroNovo.domain.Address
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse

interface InstalacaoMaquinaChooseAddressContract {
    interface View : BaseView, IAttached {
        fun goToNextScreen(addressChosen: MachineInstallAddressObj)
        fun showAddress(address: Address, addressType: String)
        fun merchantResponse(mStablishment: MCMerchantResponse)
    }

    interface Presenter {
        fun onNextButtonClicked()

        fun loadMerchant()
        fun getAddressTypes():  List<String>
        fun addressChosen(addressChosen: String)
        fun addressChosen(addressChosen: MachineInstallAddressObj)

        fun onCleared()
    }
}