package br.com.mobicare.cielo.selfRegistration.domains

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelfRegistrationConfirmationData(val email: String,
                                            val tokenExpirationInMinutes: Int) : Parcelable