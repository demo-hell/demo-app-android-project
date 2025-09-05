package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.controle

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_CONTROLE
import br.com.mobicare.cielo.taxaPlanos.TaxaPlanoRepository
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosDetailsResponse

class TaxasPlanosCieloControlePresenter(
        private val repository: TaxaPlanoRepository,
        private val view: TaxasPlanosCieloControleContract.View)
    : TaxasPlanosCieloControleContract.Presenter {

    override fun setView(view: TaxasPlanosCieloControleContract.View) {}

    override fun onDestroy() {
        this.repository.disposable()
    }

    override fun load() {

        UserPreferences.getInstance().token?.let { itToken ->
            repository.loadPlanDetails(TAXA_PLANOS_CONTROLE, object : APICallbackDefault<TaxaPlanosDetailsResponse, String> {
                override fun onStart() {
                    super.onStart()
                    view.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    view.hideLoading()
                    when {
                        error.logout -> view.onLogout()
                        else -> view.onError(error)
                    }
                }

                override fun onSuccess(response: TaxaPlanosDetailsResponse) {
                    view.hideLoading()
                    view.showData(response)
                }
            })
        }

    }


}