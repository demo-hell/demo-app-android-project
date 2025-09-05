package br.com.mobicare.cielo.meuCadastroDomicilio.presetation

import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity.FlagTransferEngineActivity

interface DomicioBancarioContract{

    interface View {
        fun errorServer(viewDB: android.view.View?, listenerDB: FlagTransferEngineActivity)
        fun showProgress()
        fun hideProgress(viewDB: android.view.View?, listenerDB: FlagTransferEngineActivity)
        fun transfBrandsSucess()
        fun logout()
        fun sucessProgress()
    }

}