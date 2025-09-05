package br.com.mobicare.cielo.pix.api.transfer

import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.model.PixBank
import io.reactivex.Observable

interface PixTransferRepositoryContract {
    fun getTransferDetails(id: String?, code: String?): Observable<TransferDetailsResponse>
    fun transfer(otpCode: String?, request: TransferRequest?): Observable<PixTransferResponse>
    fun transferToBankAccount(otpCode: String?, request: PixManualTransferRequest?): Observable<PixTransferResponse>
    fun getAllBanks(): Observable<List<PixBank>>
    fun cancelTransactionScheduled(otp: String, scheduleCancelRequest: ScheduleCancelRequest): Observable<ScheduleCancelResponse>
    fun getScheduleDetail(schedulingCode: String?): Observable<SchedulingDetailResponse>
}