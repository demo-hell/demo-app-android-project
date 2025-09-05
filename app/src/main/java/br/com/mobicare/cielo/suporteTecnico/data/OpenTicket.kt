package br.com.mobicare.cielo.suporteTecnico.data

import android.os.Parcelable
import br.com.mobicare.cielo.recebaMais.domain.OwnerPhone
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OpenTicket(
    val issueCode: Int?,
    val contactName: String?,
    val phones: List<OwnerPhone?>,
    val edited: Boolean?,
    val openingHourCode: String?,
    var openingHourText: String?,
    val address: Address?,
    val logicalNumber: String?,
    var logicalNumberDigit: String?,
    val version: String?,
    var technologyType: String?,
    val index: Int?,
    val rentalMachine: Boolean?
) : Parcelable

@Parcelize
data class Address(
    val id: String?,
    val streetAddress: String?,
    val streetAddress2: String?,
    val neighborhood: String?,
    val number: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val landMark: String?,
    val storeFront: String?
) : Parcelable
