package br.com.mobicare.cielo.meusrecebimentosnew.models

import java.io.Serializable

data class DayType(val day: String,
                   val type: Int) : Serializable
{
    enum class Type(val type: Int) {
        DAY(0),
        DAY7(1),
        DAY15(2),
        DAY30(3),
        OTHERDAY(4),
    }
}