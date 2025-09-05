package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixEnable
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetail
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixRefundDetailMapperTest {
    private val response = PixRefundsFactory.RefundDetail.response

    private val expectedResult =
        PixRefundDetail(
            idAccount = "0",
            idEndToEndOriginal = "E01027058202302081005sirsWeaVmgI",
            idEndToEndReturn = "E01027058202302081005sirsWeaVmgR",
            transactionDate = "2024-01-18T21:06:23.744".parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
            transactionType = PixExtractType.REVERSAL_CREDIT,
            errorType = "0",
            transactionStatus = PixTransactionStatus.EXECUTED,
            creditParty =
                PixRefundDetail.RefundParty(
                    ispb = "0",
                    bankName = "string",
                    nationalRegistration = "string",
                    name = "Receiver",
                    bankBranchNumber = "string",
                    bankAccountNumber = "string",
                    bankAccountType = "CC: Conta corrente",
                ),
            debitParty =
                PixRefundDetail.RefundParty(
                    ispb = "0",
                    bankName = "string",
                    nationalRegistration = "string",
                    name = "Sender",
                    bankBranchNumber = "string",
                    bankAccountNumber = "string",
                    bankAccountType = "CC: Conta corrente",
                ),
            amount = 10.0,
            tariffAmount = 0.0,
            finalAmount = 0.0,
            reversalCode = "0",
            reversalReason = "string",
            idAdjustment = "0",
            transactionCode = "string",
            transactionCodeOriginal = "string",
            payerAnswer = "Me enviou valor por engano.",
            enable =
                PixEnable(
                    refund = false,
                    cancelSchedule = false,
                    requestAnalysis = false,
                ),
        )

    @Test
    fun `it should map PixRefundDetailResponse to entity correctly`() {
        val result = response.toEntity()

        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `it should map RefundParty response to entity correctly`() {
        val debitResult = response.debitParty?.toEntity()

        assertThat(debitResult).isEqualTo(expectedResult.debitParty)
    }
}
