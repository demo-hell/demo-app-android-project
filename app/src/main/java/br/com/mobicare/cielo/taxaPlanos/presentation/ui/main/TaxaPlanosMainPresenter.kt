package br.com.mobicare.cielo.taxaPlanos.presentation.ui.main

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.taxaPlanos.TaxaPlanoRepository
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosStatusPlanResponse

class TaxaPlanosMainPresenter(
        private val repository: TaxaPlanoRepository,
        private val view: TaxaPlanosMainContract.View,
        private val userPreferences: UserPreferences,
) : TaxaPlanosMainContract.Presenter {

    private var isHideLoadingStatus = false
    private var isHideLoadingMachine = false

    private var errorMessage: ErrorMessage? = null
    private var responseStatus: TaxaPlanosStatusPlanResponse? = null
    private var responseMachine: TaxaPlanosSolutionResponse? = null

    override fun loadData() {
        isHideLoadingStatus = false
        isHideLoadingMachine = false
        view.showLoading()

        loadStatusPlan()
        loadMachines()
    }

    private fun loadStatusPlan() {
        repository.loadStatusPlan(userPreferences.token,
                object : APICallbackDefault<TaxaPlanosStatusPlanResponse, String> {

                    override fun onError(error: ErrorMessage) {
                        isHideLoadingStatus = true
                        errorMessage = error
                        showData()
                    }

                    override fun onSuccess(response: TaxaPlanosStatusPlanResponse) {
                        responseStatus = response
                        isHideLoadingStatus = true
                        showData()
                    }
                })
    }

    private fun loadMachines() {
        repository.loadMachine(userPreferences.token, object :
                APICallbackDefault<TaxaPlanosSolutionResponse, String> {

            override fun onError(error: ErrorMessage) {
                isHideLoadingMachine = true
                showData()
            }

            override fun onSuccess(response: TaxaPlanosSolutionResponse) {
                responseMachine = response
                isHideLoadingMachine = true
                showData()
            }
        })

    }

    private fun showData() {
        if (isHideLoadingStatus && isHideLoadingMachine) {
            view.hideLoading()
            if (errorMessage != null) {
                showError(errorMessage)
                errorMessage = null
            } else {
                responseStatus?.let {
                    view.showResult(it, responseMachine)
                }
                    ?: showError()
            }
        }
    }

    private fun showError(error: ErrorMessage? = null) {
        when (error?.logout) {
            true -> view.onLogout()
            else -> view.onError(error)
        }
    }

    override fun onPause() {
        view.hideLoading()
        repository.disposable()
    }

    override fun onResume() {
        repository.onResume()
    }
}