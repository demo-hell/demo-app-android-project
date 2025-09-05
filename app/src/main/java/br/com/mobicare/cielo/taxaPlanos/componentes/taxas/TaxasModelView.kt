package br.com.mobicare.cielo.taxaPlanos.componentes.taxas

import android.os.Parcelable
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrands
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BandeiraModelView(
    val nomeBandeira: String,
    val iconeBandeira: String,
    val taxas: ArrayList<TaxasModelView>
) : Parcelable

@Parcelize
data class TaxasModelView(
    val name: String,
    val values: List<Pair<String, String>>
) : Parcelable