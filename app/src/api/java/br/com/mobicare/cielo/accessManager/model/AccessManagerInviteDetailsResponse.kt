package br.com.mobicare.cielo.accessManager.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AccessManagerInviteDetailsResponse (
        val userExists: Boolean,
        val legalEntity: String,
        val companyName: String,
        val cpf: String,
        val email: String,
        val role: String,
        val foreign: Boolean,
        val unauthenticatedAnswerMandatory: Boolean
): Parcelable
