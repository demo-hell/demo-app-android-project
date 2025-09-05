package br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_401
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
import io.reactivex.rxkotlin.addTo

class DadosEstabelecimentoPresenter(
    val mView: MeuCadastroContract.DadosEstabelecimentoView,
    val mRepository: MeuCadastroContract.MeuCadastroRepository
) {

    val compositeDisposableHandler = CompositeDisposableHandler()

    fun onResume() {
        compositeDisposableHandler.start()
    }

    fun onCleared() {
        compositeDisposableHandler.destroy()
    }

    /**
     * método para carregar lista de soluções da api
     * */
    fun loadDadosAccount() {
        val token: String? = UserPreferences.getInstance().token
        token?.let {
            mRepository.loadMerchant(it)
                .configureIoAndMainThread()
                .doOnSubscribe {
                    mView.showProgress()
                }
                .subscribe({ est ->
                    mView.hideProgress()
                    mView.showEstabelecimento(est)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    when (errorMessage.httpStatus) {
                        HTTP_STATUS_401 -> mView.logout()
                        else -> mView.error()
                    }
                })
                .addTo(compositeDisposableHandler.compositeDisposable)
        }
    }
}

