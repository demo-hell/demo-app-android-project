package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner
import retrofit2.Response

class DadosProprietarioPresenter(
    private val userPreferences: UserPreferences,
    private val view: AlertaCadastralContract.View,
    private val repository: MeuCadastroNovoRepository) : AlertaCadastralContract.Presenter {

    override fun submitOwnerData(otpCode: String, owner: Owner) {
        userPreferences.token?.let { itToken ->
            repository.putOwner(itToken, otpCode, owner, object :
                APICallbackDefault<Response<Void>, String> {
                override fun onStart() {
                    view.showLoading()
                    view.removeAlertMessage()
                }

                override fun onSuccess(response: Response<Void>) {
                    view.removeAlertMessage()
                }

                override fun onError(error: ErrorMessage) {
                    view.showError(error)
                    view.addAlertMessage()
                }

                override fun onFinish() {
                    view.hideLoading()
                }
            })
        }
    }

    override fun getUserName(): String = userPreferences.userName
}