package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.categories

import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.HelpCategory
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

class CentralAjudaCategoriesPresenter(private val respository: CentralAjudaLogadoRepository)
    : CentralAjudaCategoriesContract.Presenter {

    private lateinit var mView: CentralAjudaCategoriesContract.View

    override fun setView(view: CentralAjudaCategoriesContract.View) {
        this.mView = view
    }

    override fun onCleared() {
        this.respository.disposable()
    }

    override fun loadFaqCategories(imageType: String, accessToken: String) {

        respository.faqCategories(imageType, accessToken, object : APICallbackDefault<List<HelpCategory>, String> {
            override fun onStart() {
                mView.showLoading()
            }

            override fun onError(error: ErrorMessage) {
                if (error.logout) {
                    mView.logout(error)
                } else {
                    mView.showError(error)
                }
            }

            override fun onSuccess(response: List<HelpCategory>) {
                mView.showFaqCategories(response)
                mView.hideLoading()
            }

        })


    }

}