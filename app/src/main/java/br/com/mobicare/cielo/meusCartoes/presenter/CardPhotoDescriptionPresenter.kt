package br.com.mobicare.cielo.meusCartoes.presenter

import android.content.Context
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CardPhotoDescriptionContract

class CardPhotoDescriptionPresenter(
        private val context: Context) :
        CardPhotoDescriptionContract.Presenter {

    private lateinit var view: CardPhotoDescriptionContract.View
    private var photoFront: String? = null
    private var photoVerse: String? = null
    private var lastTakePhoto = TAKE_FIRST

    //region CardPhotoDescriptionContract.Presenter

    override fun takePicture() {
        if (lastTakePhoto == TAKE_FIRST || lastTakePhoto == TAKE_FRONT) {
            lastTakePhoto = TAKE_FRONT
            view.takePictureOfFront(TAKE_FRONT)
        } else if (lastTakePhoto == TAKE_VERSE) {
            view.takePictureOfVerse(TAKE_VERSE)
        } else {
            view.takePictureOfFront(lastTakePhoto)
        }
    }

    override fun takePicture(requestCode: Int, resultCode: Int, pathFile: String?) {
        if (resultCode == -1) {
            if (requestCode == TAKE_FRONT) {
                photoFront = pathFile
                lastTakePhoto = TAKE_VERSE
                view.takePictureOfVerse(TAKE_VERSE)
            } else {
                photoVerse = pathFile
                view.showSendDocument(photoFront, photoVerse)
            }
        }
    }

    override fun setView(view: CardPhotoDescriptionContract.View) {
        this.view = view
    }

    //endregion


}