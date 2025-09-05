package br.com.mobicare.cielo.idOnboarding.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.RG
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.SMS
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointUserCnpj
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class IDOnboardingStatusResponse(
    val id: String? = null,
    val createdDate: String? = null, //"yyyy-MM-dd'T'HH:mm:ss.SSS"
    val userCnLdap: String? = null,
    val cnpjRoot: String? = null,
    val userStatus: UserStatus? = null,
    val validationStartedOn: String? = null, //"yyyy-MM-dd"
    val validationFinishedOn: String? = null, //"yyyy-MM-dd"
    var onboardingCheckpointCode: Int? = IDOCheckpointUserCnpj.NONE.code,
    var onboardingCheckpoint: String? = IDOCheckpointUserCnpj.NONE.name,
    val role: String? = null, //"ADMIN" or "READER"
    val error: Error? = null
) : Parcelable {

    @Keep
    @Parcelize
    data class Error(
        val code: String? = null,
        val message: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class UserStatus(
        val id: String? = null,
        val createdDate: String? = null,
        val userCnLdap: String? = null,
        val policy1ValidationFlow: Policy1ValidationFlow? = null,
        val policy2ValidationFlow: Policy2ValidationFlow? = null,
        val restartP1: Boolean? = null,
        val restartP2: Boolean? = null,
        val p2ReprocessError: P2ReprocessError? = null,
        val foreign: Boolean? = null
    ) : Parcelable {

        @Keep
        @Parcelize
        data class Policy1ValidationFlow(
            var p1CheckpointCode: Int? = IDOCheckpointP1.NONE.code,
            var p1Checkpoint: String? = IDOCheckpointP1.NONE.name,
            val startedOn: String? = null, //"yyyy-MM-dd"
            val finishedOn: String? = null, //"yyyy-MM-dd"
            val deadlineOn: String? = null, //"yyyy-MM-dd"
            val cpfValidation: CpfValidation? = null,
            val emailValidation: EmailValidation? = null,
            val cellphoneValidation: CellphoneValidation? = null,
            val p1Validation: P1Validation? = null,
            val deadlineRemainingDays: Long? = null
        ) : Parcelable {

            @Keep
            @Parcelize
            data class CheckCode(
                val requestedOn: String? = null,
                val checkedOn: String? = null,
                val remainingTries: Long? = null,
                val deadline: String? = null,
                val expiresIn: Long? = null
            ) : Parcelable

            @Keep
            @Parcelize
            data class CpfValidation(
                val cpf: String? = null,
                val cpfOld: String? = null,
                val validated: Boolean? = null,
                val checkedOn: String? = null,
                val name: String? = null,
                val nameOld: String? = null,
                val error: Error? = null,
                val validationBlockedTimeRemainingInMinutes: Int? = null,
                val validationBlockedTimeRemainingAsText: String? = null,
                val countTries: Int? = null,
                val maxTries: Int? = null
            ) : Parcelable

            @Keep
            @Parcelize
            data class EmailValidation(
                val validated: Boolean? = null,
                val checkCode: CheckCode? = null,
                val email: String? = null,
                val emailOld: String? = null,
                val codeRequestsBlockedTimeRemainingInMinutes: Int? = null,
                val codeRequestsBlockedTimeRemainingAsText: String? = null
            ) : Parcelable

            @Keep
            @Parcelize
            data class CellphoneValidation(
                val validated: Boolean? = null,
                val checkCode: CheckCode? = null,
                val cellphone: String? = null,
                val target: String? = SMS,
                val cellphoneOld: String? = null,
                val codeRequestsBlockedTimeRemainingInMinutes: Int? = null,
                val codeRequestsBlockedTimeRemainingAsText: String? = null
            ) : Parcelable

            @Keep
            @Parcelize
            data class P1Validation(
                val validated: Boolean? = null,
                val requestedOn: String? = null,
                val responseOn: String? = null,
                val error: Error? = null,
                val inRevision: Boolean? = null
            ) : Parcelable
        }

        @Keep
        @Parcelize
        data class Policy2ValidationFlow(
            var p2CheckpointCode: Int? = IDOCheckpointP2.NONE.code,
            var p2Checkpoint: String? = null,
            val startedOn: String? = null, //"yyyy-MM-dd"
            val finishedOn: String? = null, //"yyyy-MM-dd"
            val lgpdValidation: LgpdValidation? = null,
            var documentPhotoValidation: DocumentPhotoValidation? = null,
            var selfiePhotoValidation: SelfiePhotoValidation? = null,
            var allowmeValidation: AllowmeValidation? = null,
            var p2Validation: P2Validation? = null
        ) : Parcelable {

            @Keep
            @Parcelize
            data class LgpdValidation(
                val lgpdAgree: Boolean? = null,
                val agreeOn: String? = null
            ) : Parcelable

            @Keep
            @Parcelize
            data class DocumentPhotoValidation(
                var documentType: String? = RG, // "RG" / "CNH"
                var uploaded: Boolean? = null,
                var uploadedOn: String? = null
            ) : Parcelable

            @Keep
            @Parcelize
            data class SelfiePhotoValidation(
                var uploaded: Boolean? = null,
                var uploadedOn: String? = null
            ) : Parcelable

            @Keep
            @Parcelize
            data class AllowmeValidation(
                var sentOn: String? = null,
                var done: Boolean? = null
            ) : Parcelable

            @Keep
            @Parcelize
            data class P2Validation(
                var validated: Boolean? = null,
                var requestedOn: String? = null,
                var responseOn: String? = null,
                var error: Error? = null
            ) : Parcelable
        }
    }

    @Keep
    @Parcelize
    data class P2ReprocessError(
        val code: String? = null,
        val message: String? = null,
        val source: String? = null
    ) : Parcelable

    val p1Flow: UserStatus.Policy1ValidationFlow?
        get() {
            return userStatus?.policy1ValidationFlow
        }

    val p2Flow: UserStatus.Policy2ValidationFlow?
        get() {
            return userStatus?.policy2ValidationFlow
        }
}