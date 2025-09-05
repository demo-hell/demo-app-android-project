package br.com.mobicare.cielo.centralDeAjuda.domains.entities

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class CentralAjudaObj (
    val manager: Manager? = null,
    val merchant: CentralAjudaDefaultObj? = null,
    val user: CentralAjudaDefaultObj? = null,
    val pwd: CentralAjudaDefaultObj? = null,
    val faq: CentralAjudaDefaultObj? = null,
    val technicalQuestions: CentralAjudaDefaultObj? = null,
    val technicalSupport: CentralAjudaDefaultObj? = null,
    val onlineConsultant: CentralAjudaDefaultObj? = null,
    val phonesSupport: List<PhoneSupport> = emptyList()
): Parcelable