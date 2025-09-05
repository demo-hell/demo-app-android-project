package br.com.mobicare.cielo.meusCartoes.presentation.ui

import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached

class CardPhotoDescriptionContract {

    interface Presenter: BasePresenter<View> {

        val TAKE_FIRST: Int
            get() = 0

        val TAKE_FRONT: Int
            get() = 1234

        val TAKE_VERSE: Int
            get() = 12345

        fun takePicture(requestCode: Int = TAKE_FIRST, resultCode: Int = 0, pathFile: String? = null)
        fun takePicture()

    }

    interface View : BaseView, IAttached {
        fun takePictureOfFront(codeFront: Int)
        fun takePictureOfVerse(codeVerse: Int)
        fun showSendDocument(photoFront: String?, photoVerse: String?)
    }

}