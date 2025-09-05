package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.deliveryLoggi

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.ProductDetailDTO
import br.com.mobicare.cielo.pagamentoLink.managers.LinkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DeliveryLoggiConfigurationPresenter(
        private val view: DeliveryLoggiConfigurationContract.View,
        private val repository: LinkRepository) : DeliveryLoggiConfigurationContract.Presenter {

    private var paymentLinkDTO: PaymentLinkDTO? = null
    private var quickFilter : QuickFilter? = null

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun setPaymentLinkDTO(dto: PaymentLinkDTO) {
        this.paymentLinkDTO = dto
    }

    override fun setFilter(quickFilter: QuickFilter?) {
        quickFilter?.let {
            this.quickFilter = quickFilter
        }
    }

    override fun onNextButtonClicked(dimension: ProductDetailDTO, weight: String) {
        this.paymentLinkDTO?.let { itDTO ->
            itDTO.weight = weight.toInt()
            itDTO.productDetail = dimension
            this.generateLink(itDTO)
        }
    }

    private fun generateLink(dto: PaymentLinkDTO) {
            compositeDisposable.add(repository
                    .generateLink(UserPreferences.getInstance().token, dto, quickFilter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { this.view.showLoading() }
                    .subscribe( { successReponse ->
                        this.view.hideLoading()
                        this.view.linkGenerated(successReponse)
                    }, { error ->
                        this.view.hideLoading()
                        val convertedError = APIUtils.convertToErro(error)
                        if (convertedError.httpStatus in 400..499) {
                            if (convertedError.httpStatus == 401) {
                                return@subscribe
                            }
                            this.view.showAlert(convertedError)
                        }
                        else {
                            this.view.showError(convertedError)
                        }
                    })
            )
    }

}