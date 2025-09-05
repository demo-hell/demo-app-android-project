package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.taxaPlanos.TaxaPlanoRepository
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosDetailsResponse
import br.com.mobicare.cielo.taxaPlanos.model.CieloPlanType
import java.net.HttpURLConnection

class TaxasPlanosCieloPresenter(
    private val repository: TaxaPlanoRepository,
    private val view: TaxasPlanosCieloContract.View
) : TaxasPlanosCieloContract.Presenter {

    override fun setView(view: TaxasPlanosCieloContract.View) {
    }

    override fun onDestroy() {
        this.repository.disposable()
    }

    override fun load(planName: String) {

        repository.loadPlanDetails(
            planName,
            object : APICallbackDefault<TaxaPlanosDetailsResponse, String> {
                override fun onStart() {
                    super.onStart()
                    view.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    view.hideLoading()
                    when (error.httpStatus) {
                        HttpURLConnection.HTTP_UNAUTHORIZED -> {}
                        else -> view.onError(error)
                    }
                }

                override fun onSuccess(response: TaxaPlanosDetailsResponse) {
                    view.hideLoading()

                    when (planName) {
                        CieloPlanType.FREE.planName -> {
                            view.showFreeCieloData(response)
                        }
                        else -> {
                            view.showControlCieloData(response)
                        }
                    }
                }

            })
    }

}