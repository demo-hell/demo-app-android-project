package br.com.mobicare.cielo.arv.presentation.anticipation.adapter

import br.com.mobicare.cielo.arv.domain.model.CardBrand

interface ArvSelectableItem {
     val code: Int?
     val discountAmount: Double?
     val grossAmount: Double?
     val name: String?
     val netAmount: Double?
     var isSelected: Boolean
     val cardBrands: List<CardBrand>?

     fun copy(): ArvSelectableItem
}