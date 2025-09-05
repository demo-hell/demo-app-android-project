package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToLocalDateTimeOrNull
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixFeeType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferOrigin
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixEnable
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixTransferDetailMapperTest {
    private val response = PixTransactionsFactory.TransferDetail.response

    private val expectedResult = PixTransferDetail(
        agentMode = "AGPSS",
        agentWithdrawalIspb = "string",
        amount = 10.0,
        changeAmount = 3.0,
        errorCode = "string",
        errorMessage = "string",
        errorType = "string",
        finalAmount = 9.0,
        idAccount = "4923",
        idAccountType = "string",
        idAdjustment = "185328",
        idEndToEnd = "E01027058202302081005sirsWeaVmgI",
        merchantNumber = "123456",
        originChannel = "App Cielo Gestão",
        payerAnswer = "Jogo de PS4 do Homem Aranha",
        pixType = PixQrCodeOperationType.TRANSFER,
        purchaseAmount = 7.0,
        tariffAmount = 1.0,
        transactionCode = "fe785da1-dd5c-43ab-8402-96441a040968",
        transactionCodeOriginal = null,
        transactionDate = "2023-02-08T07:05:31.901".parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
        transactionStatus = PixTransactionStatus.EXECUTED,
        transactionType = PixTransactionType.TRANSFER_DEBIT,
        transferType = PixTransferType.MANUAL,
        transactionReversalDeadline = "2023-02-08T07:05:31.901".parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
        expiredReversal = false,
        idTx = "string",
        transferOrigin = PixTransferOrigin.P2M,
        debitParty = PixTransferDetail.TransferParty(
            bankAccountNumber = "42583000012",
            bankAccountType = "CC",
            bankBranchNumber = "0001",
            bankName = "CIELO IP S.A.",
            ispb = "1027058",
            key = "string",
            name = "MASSA DADOS AFIL. - 341-85597",
            nationalRegistration = "66.691.597/0001-76"
        ),
        creditParty = PixTransferDetail.TransferParty(
            bankAccountNumber = "92556400010",
            bankAccountType = "CC",
            bankBranchNumber = "0001",
            bankName = "CIELO IP S.A.",
            ispb = "1027058",
            key = "string",
            name = "Cielo",
            nationalRegistration = "01.027.058/0001-91"
        ),
        credit = PixTransferDetail.Credit(
            originChannel = "string",
            creditTransactionDate = "2024-01-22T18:57:35.624".parseToLocalDateTimeOrNull(LONG_TIME_WITH_MILLIS_NO_UTC),
            creditAmount = 10.0,
            creditFinalAmount = 9.0,
            creditIdEndToEnd = "string",
            creditTransactionCode = "string"
        ),
        fee = PixTransferDetail.Fee(
            feeIdEndToEnd = "string",
            feeTax = 0.0,
            feePaymentDate = "2024-01-22T18:57:35.624".parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
            feeTransactionStatus = PixTransactionStatus.EXECUTED,
            feeTransactionCode = "string",
            feeType = PixFeeType.PERCENTAGE
        ),
        settlement = PixTransferDetail.Settlement(
            settlementIdEndToEnd = "string",
            settlementDate = "2024-01-22T18:57:35.624".parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
            settlementTransactionStatus = PixTransactionStatus.EXECUTED,
            settlementTransactionCode = "string",
            settlementFinalAmount = 9.0
        ),
        type = PixType.TRANSFER_DEBIT,
        enable = PixEnable(
            refund = false,
            cancelSchedule = false,
            requestAnalysis = false,
        )
    )

    @Test
    fun `it should map response to entity correctly`() {
        val result = response.toEntity()

        assertThat(result).isEqualTo(expectedResult)
    }
}
