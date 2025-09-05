package br.com.mobicare.cielo.superlink.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.superlink.data.model.response.PaymentLinkResponse
import br.com.mobicare.cielo.superlink.domain.model.*

object SuperLinkFactory {

    val userObjWithEc = UserObj().also {
        it.ec = "Superlink Store"
    }

    const val accessToken = "t0k3n"
    const val emptyAccessToken = ""

    val apiExceptionError400 = CieloAPIException.unexpectedError(
        ActionErrorTypeEnum.HTTP_ERROR,
        NewErrorMessage(httpCode = 400)
    )

    val emptyPaymentLink = PaymentLink(
        type = "",
        name = "",
        price = 0.0,
        url = "",
        id = "",
        shipping = Shipping(
            type = "",
            zipCode = "",
            price = 0.0,
            name = "",
            spec = Specification(
                dimension = Dimension(
                    length = 0,
                    height = 0,
                    width = 0
                )
            ),
            pickupAddress = Address(
                type = "",
                streetAddress = "",
                streetAddress2 = null,
                neighborhood = "",
                city = "",
                number = "",
                state = "",
                zipCode = "",
                contactPhone = "",
                note = ""
            ),
            withdrawalDelay = 0
        ),
        quantity = 0,
        createdDate = "",
        softDescriptor = "",
        sku = "",
        recurrence = "",
        expiration = "",
        finalRecurrentExpiration = "",
        maximumInstallment = ""
    )

    val paymentLinkResponseForActiveCheck = PaymentLinkResponse(
        pagination = PaginationPaymentLink(
            pageNumber = 1,
            pageSize = 1,
            totalElements = 1,
            firstPage = true,
            lastPage = true,
            numPages = 1
        ),
        items = List(1) { emptyPaymentLink }
    )

}