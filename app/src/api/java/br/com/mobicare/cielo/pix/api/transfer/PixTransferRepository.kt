package br.com.mobicare.cielo.pix.api.transfer

import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.model.PixBank
import io.reactivex.Observable

class PixTransferRepository(private val dataSource: PixTransferDataSource) :
    PixTransferRepositoryContract {

    override fun getTransferDetails(id: String?, code: String?) =
        dataSource.getTransferDetails(id, code)

    override fun transfer(otpCode: String?, request: TransferRequest?) =
        dataSource.transfer(otpCode, request)

    override fun transferToBankAccount(
        otpCode: String?,
        request: PixManualTransferRequest?
    ): Observable<PixTransferResponse> =
        dataSource.transferToBankAccount(otpCode, request)

    override fun getAllBanks(): Observable<List<PixBank>> =
        dataSource.getAllBanks()

    override fun cancelTransactionScheduled(
        otp: String,
        scheduleCancelRequest: ScheduleCancelRequest
    ): Observable<ScheduleCancelResponse> =
        dataSource.cancelTransactionScheduled(otp, scheduleCancelRequest)

    override fun getScheduleDetail(schedulingCode: String?): Observable<SchedulingDetailResponse> =
        dataSource.getScheduleDetail(schedulingCode)
}