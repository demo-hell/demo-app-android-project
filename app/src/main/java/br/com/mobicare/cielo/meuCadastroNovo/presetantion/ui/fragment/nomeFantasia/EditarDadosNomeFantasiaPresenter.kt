package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.nomeFantasia

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.ReceitaFederalResponse

class EditarDadosNomeFantasiaPresenter(
        private val mRepository: MeuCadastroNovoRepository,
        private val mView: EditarDadosNomeFantasiaContract.View)
    : EditarDadosNomeFantasiaContract.Presenter {

    private var isSalvando = false

    override fun resubmit() {
        if (isSalvando)
            saveNameReceitaFederal()
        else
            loadReceitaFederal()
    }

    override fun onCleared() {
        mRepository.disposable()
    }

    override fun loadReceitaFederal() {

        val token = UserPreferences.getInstance().token ?: ""

        mRepository.loadReceitaFederal(token, object : APICallbackDefault<ReceitaFederalResponse, String> {
            override fun onStart() {
                mView.showLoading()
                isSalvando = false
            }

            override fun onError(error: ErrorMessage) {
                if (error.logout) {
                    mView.logout(error)
                } else {
                    mView.showError(error)
                }
            }

            override fun onSuccess(response: ReceitaFederalResponse) {
                mView.showReceitaFederal(response.tradingName)
                mView.hideLoading()
            }

        })

    }


    override fun saveNameReceitaFederal() {

        val token = UserPreferences.getInstance().token ?: ""

        mRepository.saveReceitaFederal(token, object : APICallbackDefault<ReceitaFederalResponse, String> {
            override fun onStart() {
                mView.showLoading()
                isSalvando = true
            }

            override fun onError(error: ErrorMessage) {
                if (error.logout) {
                    mView.logout(error)
                } else {
                    mView.showError(error)
                }
            }

            override fun onSuccess(response: ReceitaFederalResponse) {
                mView.showUpdateSuccess()
                mView.hideLoading()
            }

        })

    }


}