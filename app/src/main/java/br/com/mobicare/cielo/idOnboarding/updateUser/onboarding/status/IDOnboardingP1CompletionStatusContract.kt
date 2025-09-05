package br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.status

import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1
import br.com.mobicare.cielo.idOnboarding.enum.IDOnboardingComeBackEnum
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingStatusResponse

interface IDOnboardingP1CompletionStatusContract {

    interface View {
        fun onBlocked(comeBack: IDOnboardingComeBackEnum)
        fun onStarted(comeBack: IDOnboardingComeBackEnum)
        fun onStartedAndIsBlocked(comeBack: IDOnboardingComeBackEnum)

        fun onDidNotStart(comeBack: IDOnboardingComeBackEnum)
        fun onDidNotStartAndIsBlocked(comeBack: IDOnboardingComeBackEnum)
        fun onDidNotStartAndInTheLastDays(comeBack: IDOnboardingComeBackEnum)
    }

    interface Presenter {
        fun onDestination(checkpoint: IDOCheckpointP1, cpf: String?, isShowWarning: Boolean, isForeign: Boolean?): Int

        fun onProcessP1CompletionStatus(
            checkpoint: IDOCheckpointP1?,
            deadlineRemainingDays: Long?,
            statusResponse: IDOnboardingStatusResponse?
        )
    }
}