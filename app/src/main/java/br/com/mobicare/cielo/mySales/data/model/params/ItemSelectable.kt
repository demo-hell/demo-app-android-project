package br.com.mobicare.cielo.mySales.data.model.params

data class ItemSelectable <T> (
    val data: T,
    var isSelected:Boolean = false
)
