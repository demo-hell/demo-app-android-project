package br.com.mobicare.cielo.newRecebaRapido.util

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticContractRequest
import br.com.mobicare.cielo.newRecebaRapido.domain.model.CreditOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.InstallmentOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.Offer
import br.com.mobicare.cielo.newRecebaRapido.domain.model.OfferItem

object ReceiveAutomaticFactory {

    val resultError = CieloDataResult.APIError(
        CieloAPIException.networkError(EMPTY)
    )

    val resultWhenClientHasRRContracted: CieloDataResult<List<Offer>> =
        CieloDataResult.APIError(
            CieloAPIException.networkError(
                message = EMPTY,
                newErrorMessage = NewErrorMessage(
                    flagErrorCode = ConstantsReceiveAutomatic.HIRED_OFFER_EXISTS_ERROR
                )
            )
        )

    val resultWhenClientHasNoOffer =
        CieloDataResult.APIError(
            CieloAPIException.networkError(
                message = EMPTY,
                newErrorMessage = NewErrorMessage(
                    flagErrorCode = ConstantsReceiveAutomatic.OFFER_NOT_FOUND
                )
            )
        )

    val resultWhenClientHasNoOffers =
        CieloDataResult.APIError(
            CieloAPIException.networkError(
                message = EMPTY,
                newErrorMessage = NewErrorMessage(
                    flagErrorCode = ConstantsReceiveAutomatic.OFFERS_NOT_FOUND
                )
            )
        )

    val listOffer = listOf(
        Offer(
            id = "1",
            periodicity = "Daily",
            items = listOf(),
            factors = listOf(),
            summary = listOf()
        ),
        Offer(
            id = "2",
            periodicity = "Daily",
            items = listOf(
                OfferItem(
                    imgCardBrand = "https://digitalhml.hdevelo.com.br/merchant/offers/static/assets/img/brands/brand_1.png",
                    brandCode = 1,
                    brandDescription = "VISA",
                    items = listOf(
                        CreditOfferItem(
                            summarizedMdr = 5.06,
                            type = "CREDIT"
                        ),
                        CreditOfferItem(
                            type = "INSTALLMENT",
                            installments = listOf(
                                InstallmentOfferItem(
                                    recebaRapidoMdr = 2.44,
                                    mdr = 4.49,
                                    summarizedMdr = 6.93,
                                    number = 2
                                ),
                                InstallmentOfferItem(
                                    recebaRapidoMdr = 3.66,
                                    mdr = 4.49,
                                    summarizedMdr = 8.15,
                                    number = 3
                                )
                            )
                        )
                    )
                ),
                OfferItem(
                    imgCardBrand = "https://digitalhml.hdevelo.com.br/merchant/offers/static/assets/img/brands/brand_2.png",
                    brandCode = 2,
                    brandDescription = "MASTERCARD",
                    items = listOf(
                        CreditOfferItem(
                            summarizedMdr = 5.06,
                            type = "CREDIT"
                        ),
                        CreditOfferItem(
                            type = "INSTALLMENT",
                            installments = listOf(
                                InstallmentOfferItem(
                                    recebaRapidoMdr = 2.44,
                                    mdr = 4.49,
                                    summarizedMdr = 6.93,
                                    number = 2
                                )
                            )
                        )
                    )
                )

            ),
            factors = listOf(),
            summary = listOf()
        )
    )

    val offersToSummary = listOf(
        Offer(
            id = "1",
            periodicity = "Daily",
            items = listOf(
                OfferItem(
                    imgCardBrand = "https://digitalhml.hdevelo.com.br/merchant/offers/static/assets/img/brands/brand_1.png",
                    brandCode = 1,
                    brandDescription = "VISA",
                    items = listOf(
                        CreditOfferItem(
                            summarizedMdr = 5.06,
                            type = "CREDIT"
                        ),
                        CreditOfferItem(
                            type = "INSTALLMENT",
                            installments = listOf(
                                InstallmentOfferItem(
                                    recebaRapidoMdr = 2.44,
                                    mdr = 4.49,
                                    summarizedMdr = 6.93,
                                    number = 2
                                ),
                                InstallmentOfferItem(
                                    recebaRapidoMdr = 3.66,
                                    mdr = 4.49,
                                    summarizedMdr = 8.15,
                                    number = 3
                                )
                            )
                        )
                    )
                ),
                OfferItem(
                    imgCardBrand = "https://digitalhml.hdevelo.com.br/merchant/offers/static/assets/img/brands/brand_2.png",
                    brandCode = 2,
                    brandDescription = "MASTERCARD",
                    items = listOf(
                        CreditOfferItem(
                            summarizedMdr = 5.06,
                            type = "CREDIT"
                        ),
                        CreditOfferItem(
                            type = "INSTALLMENT",
                            installments = listOf(
                                InstallmentOfferItem(
                                    recebaRapidoMdr = 2.44,
                                    mdr = 4.49,
                                    summarizedMdr = 6.93,
                                    number = 2
                                )
                            )
                        )
                    )
                )

            ),
            factors = listOf(

            ),
            summary = listOf(

            )
        )
    )



    val requestUseCase = ReceiveAutomaticContractRequest(
        settlementTerm= 1,
    dayOfTheWeek = "MONDAY",
     customFastRepayPeriodicity= "DAILY",
   customFastRepayContractType = "BOTH"
    )

}