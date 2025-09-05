package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.ResponsibleDeliveryEnum
import br.com.mobicare.cielo.pagamentoLink.managers.LinkRepository
import io.reactivex.disposables.CompositeDisposable

class DeliveryPresenter(private val view: DeliveryContract.View, private val repository: LinkRepository)
    : DeliveryContract.Presenter {

    private var paymentLinkDTO: PaymentLinkDTO? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var gaDeliveryType: String
    private var quickFilter : QuickFilter? = null

    override fun setPaymentLinkDTO(dto: PaymentLinkDTO) {
        paymentLinkDTO = dto
    }

    override fun onChoiceButtonClicked(option: String) {
        this.paymentLinkDTO?.responsibleDelivery = ResponsibleDeliveryEnum.CORREIOS
        this.paymentLinkDTO?.costOfFreight = null
    }

    override fun onNextButtonClicked(weight: String?, cep: String?, price: String?) {
        this.paymentLinkDTO?.let { itDTO ->
            weight?.let {
                if (it.isNotEmpty()) {
                    itDTO.weight = it.toInt()
                }
            }
            cep?.let {
                itDTO.zipCode = it.replace("-", "")
            }
            paymentLinkDTO?.productValue?.let {
                itDTO.costOfFreight = it
            }
            generateLink()
        }
    }

    override fun setFilter(quickFilter: QuickFilter?) {
        quickFilter?.let {
            this.quickFilter = quickFilter
        }
    }

    private fun generateLink() {
        this.paymentLinkDTO?.let { itDTO ->
            when (itDTO.responsibleDelivery) {
                ResponsibleDeliveryEnum.CORREIOS -> {
                    gaDeliveryType = ResponsibleDeliveryEnum.CORREIOS.name
                    itDTO.costOfFreight = null
                }

                ResponsibleDeliveryEnum.CUSTOM -> {
                    gaDeliveryType = ResponsibleDeliveryEnum.CUSTOM.name
                    itDTO.zipCode = null
                    itDTO.weight = null
                }
                ResponsibleDeliveryEnum.FREE_SHIPPING -> {
                    gaDeliveryType = ResponsibleDeliveryEnum.FREE_SHIPPING.name
                    itDTO.zipCode = null
                    itDTO.weight = null
                }
            }
            compositeDisposable.add(repository
                    .generateLink(UserPreferences.getInstance().token, itDTO, quickFilter)
                    .configureIoAndMainThread()
                    .doOnSubscribe { this.view.showLoading() }
                    .subscribe({ successReponse ->
                        this.view.hideLoading()
                        this.view.linkGenerated(successReponse, gaDeliveryType)
                    }, { error ->
                        this.view.hideLoading()
                        val convertedError = APIUtils.convertToErro(error)
                        if (convertedError.httpStatus in 400..499) {
                            if (convertedError.httpStatus == 401) {
                                return@subscribe
                            }
                            this.view.showAlert(convertedError)
                        } else {
                            this.view.showError(convertedError)
                        }
                    })
            )
        }
    }
}