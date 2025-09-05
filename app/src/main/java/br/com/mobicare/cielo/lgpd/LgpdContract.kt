package br.com.mobicare.cielo.lgpd

import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity

interface LgpdContract {
    interface View {
        fun render(status: LgpdPresenter.State)
        fun showMainWindow(password: ByteArray?, isStartByLogin: Boolean)
    }

    interface Presenter {
        fun loadElegibility(entity: LgpdElegibilityEntity, password: ByteArray?, isStartByLogin: Boolean)
        fun onOnwerClicked(isChecked: Boolean)
        fun onAgreeClicked(isChecked: Boolean)
        fun onAgreeButtonClicked()
        fun onBackButtonClicked()
    }
}