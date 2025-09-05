package br.com.mobicare.cielo.pix.ui.transfer.amount

interface PixEnterTransferAmountContract {

    interface Result {
        fun onAmount(amount: Double)
    }

    interface View {
        fun onBalanceView(isShow: Boolean)
    }

    interface Presenter {
        fun onSaveShowBalanceValue()
        fun isShowBalanceValue(): Boolean
    }

}