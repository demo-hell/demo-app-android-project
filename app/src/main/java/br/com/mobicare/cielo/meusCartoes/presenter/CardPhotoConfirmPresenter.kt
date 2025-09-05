package br.com.mobicare.cielo.meusCartoes.presenter

import android.content.Context
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.convertBase64
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.meusCartoes.CreditCardsRepository
import br.com.mobicare.cielo.meusCartoes.domains.entities.ImageDocument
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CardPhotoConfirmContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import kotlin.properties.Delegates


class CardPhotoConfirmPresenter(private val context: Context,
                                private val repository: CreditCardsRepository)
    : CardPhotoConfirmContract.Presenter {


    var uiScheduler: Scheduler by Delegates.notNull()
    var ioScheduler: Scheduler by Delegates.notNull()

    private var subs =  CompositeDisposable()

    private lateinit var view: CardPhotoConfirmContract.View

    private val userToken: String? by lazy {
        UserPreferences.getInstance().token
    }


    //region  CardPhotoConfirmContract.Presenter

    override fun setView(view: CardPhotoConfirmContract.View) {
        this.view = view
    }

    override fun sendDocument(pathFileFront: String, pathFileVerse: String, isResedDoc: Boolean) {

        view.lockScreen()
        view.showLoading()

        val imageDocument = ImageDocument().apply {
            imageFront = imageBase64Convert(pathFileFront)
            imageBack = imageBase64Convert(pathFileVerse)
        }

        sendDocumentCreate(imageDocument)
    }

    private fun sendDocumentUpdate(imageDocument: ImageDocument) {
        MenuPreference.instance.getEC()?.let { ecNumber ->
            subs.add(repository.sendDocumentUpdate(ecNumber, userToken!!, imageDocument)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .doAfterTerminate {
                        view.let {
                            if (it.isAttached()) {
                                it.hideLoading()
                                it.unlockScreen()
                            }
                        }
                    }
                    .subscribe({
                        if(view.isAttached()){
                            view.onSuccessSentCard()
                        }

                    }, {
                        onErrorDefalt(it)
                    }))
        }
    }

    private fun sendDocumentCreate(imageDocument: ImageDocument) {
        MenuPreference.instance.getEC()?.let { ecNumber ->
            subs.add(repository.sendDocumentCreate(ecNumber, userToken!!, imageDocument)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .doAfterTerminate {
                        view.let {
                            if (it.isAttached()) {
                                it.hideLoading()
                                it.unlockScreen()
                            }
                        }

                    }
                    .subscribe({
                        if(view.isAttached()){
                            view.onSuccessSentCard()
                        }

                    }, {
                        onErrorDefalt(it)
                    }))
        }
    }

    private fun onErrorDefalt(error: Throwable) {
        view.let {
            if (it.isAttached()) {
                val errorMessage = APIUtils.convertToErro(error)
                if (errorMessage.logout) {
                    it.logout(errorMessage)
                } else {
                    it.showError(errorMessage)
                }
            }
        }
    }

    private fun imageBase64Convert(pathFileFront: String): String? {
        val imgFile = File(pathFileFront)
        return imgFile.convertBase64(100)
    }

    //endregion


}