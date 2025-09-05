package br.com.mobicare.cielo.pix.api.transfer

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.PixManualTransferRequest
import br.com.mobicare.cielo.pix.domain.ScheduleCancelRequest
import br.com.mobicare.cielo.pix.domain.TransferRequest

class PixTransferDataSource(private val api: PixAPI) {

    private val authorization = Utils.authorization()

    fun getTransferDetails(id: String?, code: String?) =
        api.getTransferDetails(authorization, id, code)

    fun transfer(otpCode: String?, request: TransferRequest?) =
        api.transfer(authorization, otpCode, request)

    fun transferToBankAccount(otpCode: String?, request: PixManualTransferRequest?) =
        api.transferToBankAccount(authorization, otpCode, request)

    fun getAllBanks() =
        api.getAllBanks(authorization)

    fun cancelTransactionScheduled(otp: String, scheduleCancelRequest: ScheduleCancelRequest) =
        api.cancelTransactionScheduled(authorization, otp, scheduleCancelRequest)

    fun getScheduleDetail(schedulingCode: String?) =
        api.getScheduleDetail(authorization, schedulingCode)
}