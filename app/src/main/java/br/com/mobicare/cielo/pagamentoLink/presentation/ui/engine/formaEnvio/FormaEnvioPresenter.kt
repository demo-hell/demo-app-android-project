package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.ResponsibleDeliveryEnum
import br.com.mobicare.cielo.pagamentoLink.managers.LinkRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class FormaEnvioPresenter(private val view: FormaEnvioContract.View,
                          private val uiScheduler: Scheduler,
                          private val ioScheduler: Scheduler,
                          private val repository: LinkRepository) : FormaEnvioContract.Presenter {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var paymentLinkDTO: PaymentLinkDTO? = null
    private var develiryType: String = ""
    private var quickFilter: QuickFilter? = null

    override fun setPaymentLinkDTO(dto: PaymentLinkDTO) {
        this.paymentLinkDTO = dto
        this.view.enableNextButton(false)
    }

    override fun setFilter(quickFilter: QuickFilter?) {
        quickFilter?.let {
            this.quickFilter = quickFilter
        }
    }

    companion object {
        const val LOGGY_DELIVERY_TYPE = "1"
        const val CORREIOS_DELIVERY_TYPE = "2"
        const val CUSTOM_DELIVERY_TYPE = "3"
        const val FREE_DELIVERY_TYPE = "4"
    }

    override fun onChoiceButtonClicked(option: String) {
        this.develiryType = option
        when(option) {
            LOGGY_DELIVERY_TYPE -> {
                view.gaSendTypeDelivery("loggi")
                onLoggyChoiceOption()
            }
            CORREIOS_DELIVERY_TYPE -> {
                view.gaSendTypeDelivery("correios")
                onCorreiosChoiceOption()
            }
            CUSTOM_DELIVERY_TYPE -> {
                view.gaSendTypeDelivery("lojista")
                onFreightChoiceOption()
            }
            FREE_DELIVERY_TYPE -> {
                view.gaSendTypeDelivery("free_shipping")
                onFreeChoiceOption()
            }
        }
    }

    override fun onNextButtonClicked() {
        this.paymentLinkDTO?.let { itDTO ->
            when (this.develiryType) {
                LOGGY_DELIVERY_TYPE -> {
                    if (UserPreferences.getInstance().isToShowOnboarding(UserPreferences.ONBOARDING.SUPERLINK)) {
                        this.view.goToNextStep(FormaEnvioFragmentDirections.actionFormaEnvioFragmentToOnboardingLoggiStep01(), itDTO, quickFilter)
                    }
                    else {
                        this.view.goToNextStep(FormaEnvioFragmentDirections.actionFormaEnvioFragmentToCollectAddressFragment(), itDTO, quickFilter)
                    }
                }
                CORREIOS_DELIVERY_TYPE -> this.view.goToNextStep(FormaEnvioFragmentDirections.actionFormaEnvioFragmentToCorreiosDeliveryFragment(), itDTO, quickFilter)
                CUSTOM_DELIVERY_TYPE -> this.view.goToNextStep(FormaEnvioFragmentDirections.actionFormaEnvioFragmentToFreightConfigurationFragment(), itDTO, quickFilter)
                FREE_DELIVERY_TYPE -> generateLinkWithObjectDelivery(itDTO)
            }
        }
    }

    override fun onInstructionButtonClicked() {
        this.paymentLinkDTO?.let { itDTO ->
            this.view.goToNextStep(FormaEnvioFragmentDirections.actionFormaEnvioFragmentToOnboardingLoggiStep01(), itDTO, quickFilter)
        }
    }

    private fun onLoggyChoiceOption() {
        this.paymentLinkDTO?.responsibleDelivery = ResponsibleDeliveryEnum.LOGGI
        this.view.showLoggyState()
    }

    private fun onCorreiosChoiceOption() {
        this.paymentLinkDTO?.responsibleDelivery = ResponsibleDeliveryEnum.CORREIOS
        this.paymentLinkDTO?.costOfFreight = null
        this.view.showCorreiosState()
    }

    private fun onFreightChoiceOption() {
        this.paymentLinkDTO?.responsibleDelivery = ResponsibleDeliveryEnum.CUSTOM
        this.paymentLinkDTO?.zipCode = null
        this.paymentLinkDTO?.weight = null
        this.view.showFreightState()
    }

    private fun onFreeChoiceOption() {
        this.paymentLinkDTO?.responsibleDelivery = ResponsibleDeliveryEnum.FREE_SHIPPING
        this.view.showFreeState()
    }


    override fun generateLinkWithObjectDelivery(paymentLinkDto: PaymentLinkDTO?) {
        paymentLinkDto?.run {
            compositeDisposable.add(repository
                    .generateLink(UserPreferences.getInstance().token, this, quickFilter)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .doOnSubscribe { view.showLoading() }
                    .subscribe({ successReponse ->
                        view.linkSuccessfulCreated(successReponse)
                    }, { error ->
                        view.hideLoading()
                        val convertedError = APIUtils.convertToErro(error)
                        view.errorOnLinkCreation(convertedError)
                    }))

        }
    }

    override fun onResume() {
        if (compositeDisposable.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

}
