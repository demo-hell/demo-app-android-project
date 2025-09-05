package br.com.mobicare.cielo.pix.api.extract.reversal

import br.com.mobicare.cielo.pix.domain.*
import io.reactivex.Observable

interface PixReversalRepositoryContract {
    fun receipts(idEndToEndOriginal: String?): Observable<ReversalReceiptsResponse>
    fun getReversalDetails(transactionCode: String?): Observable<ReversalDetailsResponse>
    fun getReversalDetailsFull(transactionCode: String?): Observable<ReversalDetailsFullResponse>
    fun reverse(otpCode: String?, request: ReversalRequest?): Observable<PixReversalResponse>
}