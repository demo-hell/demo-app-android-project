package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView

interface TransferenceBottomSheetContract {

    interface View {

        fun initView()
        fun configureResetErrorSubscribers(textInputEditDt: TypefaceEditTextView,
                                           textInputEditCvv: TypefaceEditTextView)
        fun changeDialogShowLoading(progressVisibility: Int, buttonTransferVisibility: Int)
        fun showEmptyCvv()
        fun showWrongExpirationDate()
    }
}