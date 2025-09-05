package br.com.mobicare.cielo.suporteTecnico.domain.entities

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ProblemSolution(
    val name: String,
    @SerializedName("id")
    val idProblem: String = EMPTY,
    val idSolution: String = EMPTY,
) : Parcelable