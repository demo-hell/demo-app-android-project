package br.com.mobicare.cielo.taxaPlanos.doSeuJeito

import android.content.Context
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.home.presentation.incomingfast.model.EligibleOffer
import br.com.mobicare.cielo.recebaRapido.api.RecebaRapidoRepository
import br.com.mobicare.cielo.taxaPlanos.ERROR_420
import br.com.mobicare.cielo.taxaPlanos.TaxaPlanoRepository
import br.com.mobicare.cielo.taxaPlanos.domain.OfferIncomingFastDetailResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.mapper.TaxAndBrandsMapper
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.doSeuJeito.taxas.DoSeuJeitoTaxasPlanosContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class DoSeuJeitoTaxasPlanosPresenter(
    private val view: DoSeuJeitoTaxasPlanosContract.View,
    private val repository: TaxaPlanoRepository,
    private val recebaRapidoRepository: RecebaRapidoRepository,
    private val userPreferences: UserPreferences,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val featureTogglePreference: FeatureTogglePreference
) : DoSeuJeitoTaxasPlanosContract.Presenter {

    private val disposable = CompositeDisposable()

    override fun load() {
        loadEligibleIncomingFastOffer()
        loadTaxes()
    }

    override fun loadMachines() {
        repository.loadMachine(userPreferences.token, object :
                APICallbackDefault<TaxaPlanosSolutionResponse, String> {
            override fun onStart() {
                super.onStart()
                view.showMachinesLoading(true)
            }

            override fun onError(error: ErrorMessage) {
                view.showMachinesLoading(false)
                view.showMachineError(error)
            }

            override fun onSuccess(response: TaxaPlanosSolutionResponse) {
                view.showMachinesLoading(false)
                view.showMachine(response)
                if (response.pos.isNullOrEmpty()) view.hideMachinesCard()
            }
        })

    }

    override fun confirmCancellation() {
        val raCancelWhatsappOnly =  featureTogglePreference.getFeatureTogle(FeatureTogglePreference.RA_CANCEL_WHATSAPP_ONLY)
        if (raCancelWhatsappOnly) {
            val whatsappLink = featureTogglePreference.getFeatureToggleObject(FeatureTogglePreference.RA_CANCEL_WHATSAPP_ONLY)?.statusMessage
            view.showWhatsAppCancellationDialog(whatsappLink)
        } else {
            view.showCancellationActivity()
        }
    }


    override fun loadEligibleIncomingFastOffer() {
        val isEnabledCancelIncomingFastFT = repository.isEnabledCancelIncomingFastFT()

        repository.getOfferIncomingFastDetail(object : APICallbackDefault<OfferIncomingFastDetailResponse, String> {
            override fun onError(error: ErrorMessage) {
                view.showIncomingFastLoading(false)
                when (error.httpStatus) {
                    ERROR_420 -> getEligibleRR()
                }
            }

            override fun onSuccess(response: OfferIncomingFastDetailResponse) {
                view.showIncomingFastLoading(false)
                view.showIncomingWay(isEnabledCancelIncomingFastFT)
            }
        })
    }

    private fun getEligibleRR() {
        val isEnabledIncomingFastFT = repository.isEnabledIncomingFastFT()

        repository.getEligibleToOffer(object : APICallbackDefault<EligibleOffer, String> {
            override fun onError(error: ErrorMessage) {
            }

            override fun onSuccess(response: EligibleOffer) {
                if (response.eligible
                        && isEnabledIncomingFastFT) view.showChangeIncomingButton(response.eligible)
            }
        })
    }

    override fun loadTaxes() {
        disposable.add(
                recebaRapidoRepository.getBrands(userPreferences.token)
                        .observeOn(uiScheduler)
                        .subscribeOn(ioScheduler)
                        .doOnSubscribe {
                            view.showTaxesLoading(true)
                        }
                        .doFinally {
                            view.showTaxesLoading(false)
                        }
                        .subscribe({
                            this.view.showTaxes(TaxAndBrandsMapper.convert(it))
                        }, {
                            this.view.showTaxesError(APIUtils.convertToErro(it))
                        }))
        view.showTaxesLoading(true)
    }
}