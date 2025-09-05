package br.com.mobicare.cielo.meusCartoes.presentation.ui

import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached

class CardPhotoConfirmContract {

    interface Presenter: BasePresenter<View> {
        fun sendDocument(path_photo_front: String, path_photo_verse: String, boolean: Boolean)
    }

    interface View : BaseView, IAttached {
        fun onSuccessSentCard()
    }

}