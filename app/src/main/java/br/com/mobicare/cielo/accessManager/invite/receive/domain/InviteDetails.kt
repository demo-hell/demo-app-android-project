package br.com.mobicare.cielo.accessManager.invite.receive.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InviteDetails(
        val userExists: Boolean,
        val legalEntity: String,
        val companyName: String,
        val cpf: String? = null,
        val email: String,
        val role: String,
        val foreign: Boolean,
        val unauthenticatedAnswerMandatory: Boolean,
        var foreignName: String? = null
): Parcelable
