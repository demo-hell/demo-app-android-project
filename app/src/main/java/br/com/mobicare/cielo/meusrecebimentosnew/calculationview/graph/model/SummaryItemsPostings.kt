package br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.model

import android.os.Parcelable
import br.com.mobicare.cielo.meusrecebimentosnew.models.Link
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SummaryItemsPostings(
        val code: Int,
        val type: String,
        val items: List<NetAmount>,
        val links: List<Link>
) : Parcelable