package br.com.mobicare.cielo.changeEc.domain

import android.os.Parcelable
import br.com.mobicare.cielo.commons.constants.Merchant.COMERCIAL_GROUP_EN
import br.com.mobicare.cielo.commons.constants.Merchant.COMERCIAL_GROUP_PT
import br.com.mobicare.cielo.commons.constants.Merchant.POINT_OF_SALE_EN
import br.com.mobicare.cielo.commons.constants.Merchant.POINT_OF_SALE_PT
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Merchant(
        val hierarchyLevel: String,
        val hierarchyLevelDescription: String,
        val hierarchyNode: String,
        val id: String,  // EC
        val tradingName: String,
        var hierarchies: Array<Hierarchy>? = emptyArray()) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Merchant

        if (hierarchyLevel != other.hierarchyLevel) return false
        if (hierarchyLevelDescription != other.hierarchyLevelDescription) return false
        if (hierarchyNode != other.hierarchyNode) return false
        if (id != other.id) return false
        if (tradingName != other.tradingName) return false
        if (hierarchies != null) {
            if (other.hierarchies == null) return false
            if (!hierarchies.contentEquals(other.hierarchies)) return false
        } else if (other.hierarchies != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hierarchyLevel.hashCode()
        result = 31 * result + hierarchyLevelDescription.hashCode()
        result = 31 * result + hierarchyNode.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + tradingName.hashCode()
        result = 31 * result + (hierarchies?.contentHashCode() ?: 0)
        return result
    }

    fun hierarchyLevelEnglish(): String =
        when (hierarchyLevel) {
            COMERCIAL_GROUP_PT -> COMERCIAL_GROUP_EN
            POINT_OF_SALE_PT -> POINT_OF_SALE_EN
            else -> hierarchyLevel
        }

}