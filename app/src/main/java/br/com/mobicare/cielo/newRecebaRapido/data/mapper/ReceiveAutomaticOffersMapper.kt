package br.com.mobicare.cielo.newRecebaRapido.data.mapper

import br.com.mobicare.cielo.newRecebaRapido.data.model.*
import br.com.mobicare.cielo.newRecebaRapido.domain.model.*

object ReceiveAutomaticOffersMapper {

    fun mapListOffer(contend: List<OfferResponse?>?): List<Offer> {
        val listOffers = mutableListOf<Offer>()
        contend?.forEach {
            val offerItem = Offer(
                id = it?.id,
                periodicity = it?.periodicity,
                validityPeriodDate = it?.validityPeriodDate,
                validityPeriodMonths = it?.validityPeriodMonths,
                items = mapOfferItem(it?.items),
                factors = mapFactors(it?.factors),
                summary = mapSummary(it?.summary)
            )
            listOffers.add(offerItem)
        }
        return listOffers
    }

    private fun mapOfferItem(contend: List<OfferItemResponse?>?): List<OfferItem> {
        val offerItemList = mutableListOf<OfferItem>()
        contend?.forEach {

            val offerItem = OfferItem(
                brandCode = it?.brandCode,
                brandDescription = it?.brandDescription,
                imgCardBrand = it?.imgCardBrand,
                items = mapCreditOfferItem(it?.items),
            )
            offerItemList.add(offerItem)
        }
        return offerItemList
    }

    private fun mapFactors(contend: List<FactorResponse?>?): List<Factor> {
        val factorList = mutableListOf<Factor>()
        contend?.forEach {
            val factorItem = Factor(
                rate = it?.rate,
                type = it?.type,
                number = it?.number
            )
            factorList.add(factorItem)
        }
        return factorList
    }

    private fun mapSummary(contend: List<SummaryResponse?>?): List<Summary> {
        val summaryList = mutableListOf<Summary>()
        contend?.forEach {
            val summaryItem = Summary(
                mdr = it?.mdr,
                settlementDay = it?.settlementDay,
                type = it?.type,
                typeDescription = it?.typeDescription,
                settlementDayDescription = it?.settlementDayDescription
            )
            summaryList.add(summaryItem)
        }
        return summaryList
    }

    private fun mapCreditOfferItem(contend: List<CreditOfferItemResponse?>?): List<CreditOfferItem> {
        val list = mutableListOf<CreditOfferItem>()
        contend?.forEach {
            val item = CreditOfferItem(
                recebaRapidoMdr = it?.recebaRapidoMdr,
                mdr = it?.mdr,
                summarizedMdr = it?.summarizedMdr,
                type = it?.type,
                productCode = it?.productCode,
                description = it?.description,
                installments = mapInstallmentsOfferItem(it?.installments)
            )
            list.add(item)
        }
        return list
    }

    private fun mapInstallmentsOfferItem(contend: List<InstallmentOfferItemResponse?>?): List<InstallmentOfferItem> {
        val list = mutableListOf<InstallmentOfferItem>()
        contend?.forEach {
            val item = InstallmentOfferItem(
                recebaRapidoMdr = it?.recebaRapidoMdr,
                mdr = it?.mdr,
                summarizedMdr = it?.summarizedMdr,
                number = it?.number
            )
            list.add(item)
        }
        return list
    }

}