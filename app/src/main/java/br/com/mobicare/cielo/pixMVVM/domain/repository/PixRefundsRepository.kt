package br.com.mobicare.cielo.pixMVVM.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixRefundCreateRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundCreated
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts

interface PixRefundsRepository {
    suspend fun getReceipts(idEndToEndOriginal: String?): CieloDataResult<PixRefundReceipts>
    suspend fun getDetail(transactionCode: String?): CieloDataResult<PixRefundDetail>
    suspend fun getDetailFull(transactionCode: String?): CieloDataResult<PixRefundDetailFull>
    suspend fun refund(otpCode: String?, request: PixRefundCreateRequest?): CieloDataResult<PixRefundCreated>
}