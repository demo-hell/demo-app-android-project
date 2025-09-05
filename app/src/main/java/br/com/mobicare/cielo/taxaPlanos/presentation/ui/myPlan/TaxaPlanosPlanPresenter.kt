package br.com.mobicare.cielo.taxaPlanos.presentation.ui.myPlan

import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.home.presentation.incomingfast.model.EligibleOffer
import br.com.mobicare.cielo.taxaPlanos.ERROR_420
import br.com.mobicare.cielo.taxaPlanos.TaxaPlanoRepository
import br.com.mobicare.cielo.taxaPlanos.domain.OfferIncomingFastDetailResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosOverviewResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse

class TaxaPlanosPlanPresenter(
    private val mRepository: TaxaPlanoRepository,
    private val mView: TaxaPlanosPlanContract.View,
    userPreferences: UserPreferences,
    private val featureTogglePreference: FeatureTogglePreference,
) : TaxaPlanosPlanContract.Presenter {

    private var token: String = userPreferences.token

    override fun onClieared() {
        mRepository.disposable()
    }

    override fun confirmCancellation() {
        val raCancelWhatsappOnly =
            featureTogglePreference.getFeatureTogle(FeatureTogglePreference.RA_CANCEL_WHATSAPP_ONLY)
        if (raCancelWhatsappOnly) {
            val whatsappLink =
                featureTogglePreference.getFeatureToggleObject(FeatureTogglePreference.RA_CANCEL_WHATSAPP_ONLY)?.statusMessage
            mView.showWhatsAppCancellationDialog(whatsappLink)
        } else {
            mView.showCancellationActivity()
        }
    }

    override fun loadOverview(type: String) {
        mRepository.loadOverview(
            token,
            type,
            object : APICallbackDefault<TaxaPlanosOverviewResponse, String> {
                override fun onStart() {
                    super.onStart()
                    mView.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    mView.hideLoading()
                    mView.showOverviewError(error)

                }

                override fun onSuccess(response: TaxaPlanosOverviewResponse) {
                    mView.hideLoading()
                    mView.showOverview(response)
                }

            })
    }

    override fun loadMarchine() {
        mRepository.loadMachine(
            token,
            object : APICallbackDefault<TaxaPlanosSolutionResponse, String> {
                override fun onStart() {
                    super.onStart()
                    mView.showLoadMachines()
                }

                override fun onError(error: ErrorMessage) {
                    mView.hideLoadMachines()
                    mView.showMachineError(error)
                }

                override fun onSuccess(response: TaxaPlanosSolutionResponse) {
                    mView.showMachine(response)
                    mView.hideLoadMachines()
                    if (response.pos.isNullOrEmpty()) mView.hideEmptyMachines()
                }
            })
    }

    override fun getEligibleToOffer() {
        val isEnabledCancelIncomingFastFT = mRepository.isEnabledCancelIncomingFastFT()

        mRepository.getOfferIncomingFastDetail(object :
            APICallbackDefault<OfferIncomingFastDetailResponse, String> {
            override fun onError(error: ErrorMessage) {
                when (error.httpStatus) {
                    ERROR_420 -> getEligibleRR()
                }
            }

            override fun onSuccess(response: OfferIncomingFastDetailResponse) {
                mView.showIncomingWay(isEnabledCancelIncomingFastFT)
            }
        })
    }

    private fun getEligibleRR() {
        val isEnabledIncomingFastFT = mRepository.isEnabledIncomingFastFT()
        mRepository.getEligibleToOffer(object : APICallbackDefault<EligibleOffer, String> {
            override fun onError(error: ErrorMessage) {
            }

            override fun onSuccess(response: EligibleOffer) {
                if (response.eligible && isEnabledIncomingFastFT) mView.showChangeIncomingButton()
            }
        })
    }
}