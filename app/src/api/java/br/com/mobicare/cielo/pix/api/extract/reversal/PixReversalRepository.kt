package br.com.mobicare.cielo.pix.api.extract.reversal

import br.com.mobicare.cielo.pix.domain.*
import io.reactivex.Observable

class PixReversalRepository(private val dataSource: PixReversalDataSource) :
    PixReversalRepositoryContract {

    override fun receipts(
        idEndToEndOriginal: String?
    ): Observable<ReversalReceiptsResponse> =
        dataSource.receipts(idEndToEndOriginal)

    override fun getReversalDetails(transactionCode: String?) =
        dataSource.getReversalDetails(transactionCode)

    override fun getReversalDetailsFull(transactionCode: String?) =
        dataSource.getReversalDetailsFull(transactionCode)

    override fun reverse(
        otpCode: String?,
        request: ReversalRequest?
    ): Observable<PixReversalResponse> =
        dataSource.reverse(otpCode, request)
}