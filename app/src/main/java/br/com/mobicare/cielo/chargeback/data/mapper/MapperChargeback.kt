package br.com.mobicare.cielo.chargeback.data.mapper

import br.com.mobicare.cielo.chargeback.data.model.response.CardBrandResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackDetailsResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackDisputeStatusResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackFilterResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackProcessResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebacksResponse
import br.com.mobicare.cielo.chargeback.data.model.response.LifecycleResponse
import br.com.mobicare.cielo.chargeback.data.model.response.TransactionDetailsResponse
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDetails
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterCardBrand
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterDisputeStatus
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterProcess
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilters
import br.com.mobicare.cielo.chargeback.domain.model.Chargebacks
import br.com.mobicare.cielo.chargeback.domain.model.Lifecycle
import br.com.mobicare.cielo.chargeback.domain.model.TransactionDetails
import br.com.mobicare.cielo.commons.utils.parseToLocalDate

object MapperChargeback {

    fun mapToChargebacks(chargebackResponse: ChargebacksResponse?): Chargebacks? {
        chargebackResponse?.let {
            return Chargebacks(
                content = mapChargebackList(chargebackResponse.content),
                totalElements = chargebackResponse.totalElements,
                firstPage = chargebackResponse.firstPage,
                lastPage = chargebackResponse.lastPage,
                totalPages = chargebackResponse.totalPages,
                pageSize = chargebackResponse.pageSize,
                pageNumber = chargebackResponse.pageNumber,
                numberOfElements = chargebackResponse.numberOfElements,
                empty = chargebackResponse.empty
            )
        }
        return null
    }

    private fun mapChargebackList(content: List<ChargebackResponse?>): List<Chargeback> {
        val chargebackItems = mutableListOf<Chargeback>()
        content.let { contents ->
            contents.forEach {
                val chargebackItem = Chargeback(
                    actionTakenCode = it?.actionTakenCode,
                    chargebackId = it?.chargebackId,
                    daysToDeadLine = it?.daysToDeadLine,
                    caseId = it?.caseId,
                    merchantId = it?.merchantId,
                    transactionAmount = it?.transactionAmount,
                    process = it?.process,
                    disputeStatus = it?.disputeStatus,
                    chargebackDetails = mapChargebackDetail(it?.chargebackDetails),
                    lifecycle = mapChargebackLifecycle(it?.lifecycle),
                    transactionDetails = mapChargebackTransactionDetails(it?.transactionDetails),
                    treatmentDeadline = it?.treatmentDeadline
                )
                chargebackItems.add(chargebackItem)
            }
        }
        return chargebackItems
    }

    private fun mapChargebackDetail(chargebackDetails: ChargebackDetailsResponse?): ChargebackDetails? {
        chargebackDetails?.let { chargebackDetailsResponse ->
            return ChargebackDetails(
                descriptionReason = chargebackDetailsResponse.descriptionReason,
                reasonCode = chargebackDetailsResponse.reasonCode,
                reasonDescription = chargebackDetailsResponse.reasonDescription,
                reasonType  = chargebackDetailsResponse.reasonType,
                receptionDate = chargebackDetailsResponse.receptionDate?.parseToLocalDate(),
                replyDate = chargebackDetailsResponse.replyDate?.parseToLocalDate(),
                chargebackFraudMessage = chargebackDetailsResponse.chargebackMessage,
                descriptionReasonType = chargebackDetailsResponse.descriptionReasonType,
                fastDisputeResolution = chargebackDetailsResponse.fastDisputeResolution,
                refundFileInformation = chargebackDetailsResponse.refundFileInformation
            )
        }
        return null
    }

    private fun mapChargebackLifecycle(lifecycle: LifecycleResponse?): Lifecycle? {
        lifecycle?.let { lifecycleResponse ->
            return Lifecycle(
                action = lifecycleResponse.action,
                actionDate = lifecycleResponse.actionDate?.parseToLocalDate()
            )
        }
        return null
    }

    private fun mapChargebackTransactionDetails(transactionDetails: TransactionDetailsResponse?): TransactionDetails? {
        transactionDetails?.let { transactionDetailsResponse ->
            return TransactionDetails(
                authorizationCode = transactionDetailsResponse.authorizationCode,
                cardBrandCode = transactionDetailsResponse.cardBrandCode,
                cardBrandName = transactionDetailsResponse.cardBrandName,
                issuerSenderCode = transactionDetailsResponse.issuerSenderCode,
                issuerSenderDescription = transactionDetailsResponse.issuerSenderDescription,
                merchantName = transactionDetailsResponse.merchantName,
                nsu = transactionDetailsResponse.nsu,
                productType = transactionDetailsResponse.productType,
                productTypeCode = transactionDetailsResponse.productTypeCode,
                referenceNumber = transactionDetailsResponse.referenceNumber,
                roNumber = transactionDetailsResponse.roNumber,
                terminal = transactionDetailsResponse.terminal,
                transactionDate = transactionDetailsResponse.transactionDate?.parseToLocalDate(),
                truncatedCardNumber = transactionDetailsResponse.truncatedCardNumber,
                tid = transactionDetailsResponse.tid,
                currency = transactionDetailsResponse.currency
            )
        }
        return null
    }


    fun mapToChargebackFilter(filterResponse: ChargebackFilterResponse?): ChargebackFilters? {
        filterResponse?.let {
            return ChargebackFilters(
                brands = mapCardBrandsFilterResponse(filterResponse.brandsResponse),
                process = mapChargebackProcessFilterResponse(filterResponse.processResponse),
                disputeStatus = mapChargebackDisputeStatusFilterResponse(filterResponse.disputeStatus)
            )
        }
        return null
    }

    private fun mapCardBrandsFilterResponse(listOfBrandsResponse: List<CardBrandResponse>?):
            List<ChargebackFilterCardBrand>? {

        var resultList: MutableList<ChargebackFilterCardBrand>? = null
        listOfBrandsResponse?.let { list ->
            resultList =  mutableListOf()
            list.forEach { cardBrandResponse ->
                resultList?.add(
                    ChargebackFilterCardBrand(
                        brandCode = cardBrandResponse.brandCode,
                        brandName = cardBrandResponse.brandName
                    )
                )
            }
        }
        return resultList?.toList()
    }


    private fun mapChargebackProcessFilterResponse(listOfProcessResponse: List<ChargebackProcessResponse>?):
            List<ChargebackFilterProcess>? {

        var resultList: MutableList<ChargebackFilterProcess>? = null
        listOfProcessResponse?.let { list ->
            resultList = mutableListOf()
            list.forEach { processResponse ->
                resultList?.add(
                    ChargebackFilterProcess(
                        chargebackProcessCode = processResponse.chargebackProcessCode,
                        chargebackProcessName = processResponse.chargebackProcessName
                    )
                )
            }
        }
        return resultList?.toList()
    }

    private fun mapChargebackDisputeStatusFilterResponse(listOfDisputeStatusResponse: List<ChargebackDisputeStatusResponse>?):
            List<ChargebackFilterDisputeStatus>? {

        return listOfDisputeStatusResponse?.map { disputeStatusResponse ->
            ChargebackFilterDisputeStatus(
                chargebackDisputeStatusCode = disputeStatusResponse.chargebackDisputeStatusCode,
                chargebackDisputeStatusName = disputeStatusResponse.chargebackDisputeStatusName
            )
        }
    }

}

