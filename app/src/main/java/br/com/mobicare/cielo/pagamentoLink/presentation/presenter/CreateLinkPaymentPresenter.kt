package br.com.mobicare.cielo.pagamentoLink.presentation.presenter

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domains.LinkRequest
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.TypeSaleEnum
import br.com.mobicare.cielo.pagamentoLink.managers.LinkRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class CreateLinkPaymentPresenter(private val createLinkLinkView: LinkContract.CreateLinkView,
                                 private val uiScheduler: Scheduler,
                                 private val ioScheduler: Scheduler,
                                 private val createLinkRepository: LinkRepository) :
        LinkContract.CreateLinkPresenter {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var paymentLinkDTO: PaymentLinkDTO? = null
    private var quickFilter: QuickFilter? = null

    override fun generateLink(token: String, linkToCreate: LinkRequest) {
        this.paymentLinkDTO?.let { itDTO ->
            itDTO.productName = linkToCreate.title
            itDTO.productValue = linkToCreate.value

            if (itDTO.typeSale == TypeSaleEnum.SEND_PRODUCT) {
                this.createLinkLinkView.goToShippingMethod(itDTO)
                return
            }

            compositeDisposable.add(createLinkRepository
                    .generateLink(token, itDTO, quickFilter)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .doOnSubscribe { createLinkLinkView.showLoading() }
                    .subscribe({ successReponse ->
                        createLinkLinkView.hideLoading()
                        createLinkLinkView.linkSuccessfulCreated(successReponse)
                    }, { error ->
                        createLinkLinkView.hideLoading()
                        val convertedError = APIUtils.convertToErro(error)
                        if (convertedError.httpStatus != 401) {
                            if (convertedError.httpStatus == 420) {
                                createLinkLinkView.showIneligibleUser(convertedError)
                            } else {
                                createLinkLinkView.errorOnLinkCreation(convertedError)
                            }
                        }
                    })
            )
        }
    }

    override fun setFilter(quickFilter: QuickFilter?) {
        super.setFilter(quickFilter)
        quickFilter?.let {
            this.quickFilter = quickFilter
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

    override fun setPaymentLinkDTO(dto: PaymentLinkDTO) {
        this.paymentLinkDTO = dto
        if (dto.typeSale == TypeSaleEnum.SEND_PRODUCT) {
            this.createLinkLinkView.setLabelButton(R.string.coil_button_next)
        } else if (dto.typeSale == TypeSaleEnum.RECURRENT_SALE) {
            createLinkLinkView.showPeriodicityCharge()
        }
    }

    override fun generateLinkWithObjectDelivery(paymentLinkDto: PaymentLinkDTO?) {
        paymentLinkDto?.let { dto ->
            compositeDisposable
                .add(
                    createLinkRepository
                        .generateLink(UserPreferences.getInstance().token, dto, quickFilter)
                        .subscribeOn(ioScheduler)
                        .observeOn(uiScheduler)
                        .doOnSubscribe { createLinkLinkView.showLoading() }
                        .subscribe({ successResponse ->
                            createLinkLinkView.linkSuccessfulCreated(successResponse)
                        }, { error ->
                            createLinkLinkView.hideLoading()
                            val convertedError = APIUtils.convertToErro(error)
                            createLinkLinkView.errorOnLinkCreation(convertedError)
                        })
                )
        }
    }

}