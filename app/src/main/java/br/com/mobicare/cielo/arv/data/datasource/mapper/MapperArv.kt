package br.com.mobicare.cielo.arv.data.datasource.mapper

import br.com.mobicare.cielo.arv.data.model.response.AcquirerResponse
import br.com.mobicare.cielo.arv.data.model.response.ArvAnticipationResponse
import br.com.mobicare.cielo.arv.data.model.response.CardBrandResponse
import br.com.mobicare.cielo.arv.domain.model.Acquirer
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.CardBrand

object MapperArv {

    fun mapToArv(
        arvAnticipationResponse: ArvAnticipationResponse?,
        negotiationType: String? = null,
        initialDate: String? = null,
        finalDate: String? = null
    ): ArvAnticipation? {
        arvAnticipationResponse?.let {
            return ArvAnticipation(
                acquirers = mapArvAcquirerList(it.acquirers),
                discountAmount = it.discountAmount,
                finalDate = it.finalDate ?: finalDate,
                grossAmount = it.grossAmount,
                id = it.id,
                initialDate = it.initialDate ?: initialDate,
                negotiationType = it.negotiationType ?: negotiationType,
                netAmount = it.netAmount,
                nominalFee = it.nominalFee,
                standardFee = it.standardFee,
                token = it.token,
                effectiveFee = it.effectiveFee,
                simulationType = it.simulationType,
                eligibleTimeToReceiveToday = it.eligibleTimeToReceiveToday
            )
        }
        return null
    }

    private fun mapArvAcquirerList(contend: List<AcquirerResponse?>?): List<Acquirer> {
        val acquirerItems = mutableListOf<Acquirer>()
        contend?.forEach { contends ->
            val acquirerItem = Acquirer(
                code = contends?.code,
                cardBrands = mapCardBrands(contends?.cardBrands),
                discountAmount = contends?.discountAmount,
                grossAmount = contends?.grossAmount,
                name = contends?.name,
                netAmount = contends?.netAmount
            )
            acquirerItems.add(acquirerItem)
        }
        return acquirerItems
    }

    private fun mapCardBrands(contend: List<CardBrandResponse?>?): List<CardBrand> {
        val cardBrandList = mutableListOf<CardBrand>()
        contend?.forEach { contents ->
            val cardBrandItem = CardBrand(
                code = contents?.code,
                discountAmount = contents?.discountAmount,
                grossAmount = contents?.grossAmount,
                name = contents?.name,
                netAmount = contents?.netAmount
            )
            cardBrandList.add(cardBrandItem)
        }
        return cardBrandList
    }

}

