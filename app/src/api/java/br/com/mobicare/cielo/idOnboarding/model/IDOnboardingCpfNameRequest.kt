package br.com.mobicare.cielo.idOnboarding.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class IDOnboardingCpfNameRequest(
    val cpf: String?,
    val name: String?
) : Parcelable
