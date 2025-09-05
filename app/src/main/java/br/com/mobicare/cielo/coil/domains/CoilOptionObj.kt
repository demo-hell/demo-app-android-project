package br.com.mobicare.cielo.coil.domains

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CoilOptionObj : Parcelable {
    var quantity : Int = 0
    var code: String = ""
    var title: String = ""
    var description: String = ""
    var descriptionComplement: String = ""
    var allowedQuantity: Boolean = false
    var type: String = ""
    var tagService: String = ""
}