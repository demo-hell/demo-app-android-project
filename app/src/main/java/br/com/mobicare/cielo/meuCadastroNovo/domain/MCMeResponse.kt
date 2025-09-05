package br.com.mobicare.cielo.meuCadastroNovo.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MCMeResponse(
    val activeMerchant: ActiveMerchant,
    val birthDate: String,
    val email: String,
    val identity: Identity,
    val impersonating: Boolean,
    val impersonationEnabled: Boolean,
    val lastLoginDate: String,
    val login: String,
    val merchant: Merchant,
    val roles: List<String>,
    val username: String
): Parcelable

@Parcelize
data class Merchant(
        val cnpj: Cnpj,
        val hierarchyLevel: String,
        val id: String,
        val individual: Boolean,
        val name: String,
        val receivableType: String,
        val tradingName: String
): Parcelable

@Parcelize
data class Cnpj(
        val number: String,
        val rootNumber: String
): Parcelable

@Parcelize
data class Identity(
        val cpf: String?,
        val rg: String?,
        val foreigner: Boolean
): Parcelable

@Parcelize
data class ActiveMerchant(
        val cnpj: Cnpj,
        val hierarchyLevel: String,
        val id: String,
        val individual: Boolean,
        val name: String,
        val receivableType: String,
        val tradingName: String
): Parcelable