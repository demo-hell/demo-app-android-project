package br.com.mobicare.cielo.pix.api.extract.reversal

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.ReversalRequest

class PixReversalDataSource(private val api: PixAPI) {

    private val authorization = Utils.authorization()

    fun receipts(idEndToEndOriginal: String?) =
        api.getReceipts(authorization, idEndToEndOriginal)

    fun getReversalDetails(transactionCode: String?) =
        api.getReversalDetails(authorization, transactionCode)

    fun getReversalDetailsFull(transactionCode: String?) =
        api.getReversalDetailsFull(authorization, transactionCode)

    fun reverse(otpCode: String?, request: ReversalRequest?) =
        api.reverse(authorization, otpCode, request)
}