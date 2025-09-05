package br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter

import android.view.View
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
import io.reactivex.disposables.CompositeDisposable

class DadosContaPresenter(
        private val mView: MeuCadastroContract.DadosContaView,
        private val mRepository: MeuCadastroContract.MeuCadastroRepository
) {

    val compositeDisposable = CompositeDisposable()

    /**
     * método para carregar lista de soluções da api
     * */
    fun loadDadosAccount(view: View) {
        val token: String? = UserPreferences.getInstance().token
        token?.let {
            mRepository.loadBrands(it, compositeDisposable, { lstSolution ->
                mView.showBrands(lstSolution, view)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                when (errorMessage.httpStatus) {
                    401 -> mView.logout()
                    else -> mView.error()
                }
            })
        }
    }

    fun onCleared() {
        compositeDisposable.clear()
    }
}