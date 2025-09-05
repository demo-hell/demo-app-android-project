package br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.status

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1
import br.com.mobicare.cielo.idOnboarding.enum.IDOnboardingComeBackEnum
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingStatusResponse

private const val LAST_FIVE_DAYS: Long = 5
private const val EXPIRED: Long = 0

class IDOnboardingP1CompletionStatusPresenter(val view: IDOnboardingP1CompletionStatusContract.View) :
    IDOnboardingP1CompletionStatusContract.Presenter {

    override fun onDestination(
        checkpoint: IDOCheckpointP1,
        cpf: String?,
        isShowWarning: Boolean,
        isForeign: Boolean?
    ): Int {
        return when (checkpoint) {
            IDOCheckpointP1.NONE -> {
                if (cpf.isNullOrBlank())
                    if (isShowWarning)
                        R.id.action_idOnboardingP1CompletionStatusFragment_to_idOnboardingNewCpfFragment
                    else
                        R.id.action_to_idOnboardingNewCpfFragment
                else
                    if (isShowWarning)
                        R.id.action_idOnboardingP1CompletionStatusFragment_to_idOnboardingUpdateCpfFragment
                    else
                        R.id.action_to_idOnboardingUpdateCpfFragment
            }
            IDOCheckpointP1.CPF_NAME_VALIDATED -> {
                if (isShowWarning)
                    R.id.action_idOnboardingP1CompletionStatusFragment_to_idOnboardingUpdateEmailFragment
                else
                    R.id.action_to_idOnboardingUpdateEmailFragment
            }
            IDOCheckpointP1.EMAIL_VALIDATION_STARTED -> {
                if (isShowWarning)
                    R.id.action_idOnboardingP1CompletionStatusFragment_to_idOnboardingUpdateEmailValidationFragment
                else
                    R.id.action_to_idOnboardingUpdateEmailValidationFragment
            }
            IDOCheckpointP1.EMAIL_VALIDATION_CONFIRM -> {
                if (isShowWarning)
                    if (isForeign == true){
                        R.id.action_idOnboardingP1CompletionStatusFragment_to_idOnboardingUpdateForeignPhoneFragment
                    } else {
                        R.id.action_idOnboardingP1CompletionStatusFragment_to_idOnboardingUpdatePhoneFragment
                    }
                else {
                    if (isForeign == true){
                        R.id.action_to_idOnboardingUpdateForeignPhoneFragment
                    } else {
                        R.id.action_to_idOnboardingUpdatePhoneFragment
                    }
                }
            }
            IDOCheckpointP1.CELLPHONE_VALIDATION_STARTED -> {
                if (isShowWarning)
                    R.id.action_idOnboardingP1CompletionStatusFragment_to_idOnboardingUpdatePhoneValidationFragment
                else
                    R.id.action_to_idOnboardingUpdatePhoneValidationFragment
            }
            IDOCheckpointP1.CELLPHONE_VALIDATION_CONFIRM,
            IDOCheckpointP1.POLICY_1_REQUESTED,
            IDOCheckpointP1.POLICY_1_RESPONSE -> {
                if (isShowWarning)
                    R.id.action_idOnboardingP1CompletionStatusFragment_to_idOnboardingValidateP1PolicyFragment
                else
                    R.id.action_to_idOnboardingValidateP1PolicyFragment
            }
        }
    }

    override fun onProcessP1CompletionStatus(
        checkpoint: IDOCheckpointP1?,
        deadlineRemainingDays: Long?,
        statusResponse: IDOnboardingStatusResponse?
    ) {
        val isBlocked = isBlocked(statusResponse)

        deadlineRemainingDays?.let { remainingDays ->
            val isExpired = remainingDays.orZero == EXPIRED
            if (isBlocked) {
                if (IDOnboardingFlowHandler.userStatus.onboardingStatus?.userStatus?.foreign == true)
                    userStarted(isExpired)
                else
                    view.onBlocked(IDOnboardingComeBackEnum.BLOCKED)
            } else {
                if (checkpoint == IDOCheckpointP1.NONE)
                    userDidNotStart(remainingDays, isExpired)
                else
                    userStarted(isExpired)
            }
        }
    }

    private fun userDidNotStart(remainingDays: Long, isExpired: Boolean) {
        if (remainingDays > LAST_FIVE_DAYS)
            view.onDidNotStart(IDOnboardingComeBackEnum.HOME)
        else {
            if (isExpired)
                view.onDidNotStartAndIsBlocked(IDOnboardingComeBackEnum.LOGOUT)
            else
                view.onDidNotStartAndInTheLastDays(IDOnboardingComeBackEnum.DIALOG)
        }
    }

    private fun userStarted(isExpired: Boolean) {
        if (isExpired)
            view.onStartedAndIsBlocked(IDOnboardingComeBackEnum.LOGOUT)
        else
            view.onStarted(IDOnboardingComeBackEnum.HOME)
    }

    private fun isBlocked(statusResponse: IDOnboardingStatusResponse?): Boolean {
        return statusResponse?.p1Flow?.p1Validation?.responseOn != null
                && statusResponse.p1Flow?.p1Validation?.validated == false
    }
}