package br.com.mobicare.cielo.forgotMyPassword.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.esqueciSenha.domains.entities.Bank
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ForgotMyPasswordPid(
    val merchantId: String? = null,
    val bankAccount: Bank? = null,
    val cardProxy: String? = null,
) : Parcelable
