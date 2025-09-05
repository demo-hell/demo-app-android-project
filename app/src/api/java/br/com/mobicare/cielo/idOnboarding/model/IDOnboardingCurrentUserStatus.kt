package br.com.mobicare.cielo.idOnboarding.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.RG
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class IDOnboardingCurrentUserStatus(
    var isForeigner: Boolean? = null,
    var cpf: String? = null,
    var name: String? = null,
    var email: String? = null,
    var cellphone: String? = null,
    var phoneTarget: String? = null,
    var documentType: Array<String>? =  arrayOf(RG),
): Parcelable {
    var onboardingStatus: IDOnboardingStatusResponse? = null
        set(value) {
            field = value
            field?.p1Flow?.run {
                cpfValidation?.cpf?.let { cpf = it }
                cpfValidation?.name?.let { name = it }
                emailValidation?.email?.let { email = it }
                cellphoneValidation?.cellphone?.let { cellphone = it }
                cellphoneValidation?.target?.let { phoneTarget = it }
            }
        }

    val p1Flow: IDOnboardingStatusResponse.UserStatus.Policy1ValidationFlow?
        get() {
            return onboardingStatus?.p1Flow
        }

    val p2Flow: IDOnboardingStatusResponse.UserStatus.Policy2ValidationFlow?
        get() {
            return onboardingStatus?.p2Flow
        }
}
