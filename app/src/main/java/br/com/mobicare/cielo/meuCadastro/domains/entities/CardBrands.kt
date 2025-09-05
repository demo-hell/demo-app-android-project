package br.com.mobicare.cielo.meuCadastro.domains.entities

import android.os.Parcel
import android.os.Parcelable
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeiraModelView
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.TaxasModelView
import java.io.Serializable

/**
 * Created by gustavon on 15/01/18.
 */
open class CardBrands() : Parcelable, Serializable {
    var name: String = ""
    var imageURL: String = ""
    var products: List<Products> = listOf()

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardBrands> {
        override fun createFromParcel(parcel: Parcel): CardBrands {
            return CardBrands(parcel)
        }

        override fun newArray(size: Int): Array<CardBrands?> {
            return arrayOfNulls(size)
        }
    }


}