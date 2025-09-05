package br.com.mobicare.cielo.suporteTecnico.domain.entities

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Problem(
    val id: String?,
    val name: String,
    val idProblem: String?,
    val solutions: ArrayList<ProblemSolution>?
    ): Parcelable
