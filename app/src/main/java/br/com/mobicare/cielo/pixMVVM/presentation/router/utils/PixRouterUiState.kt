package br.com.mobicare.cielo.pixMVVM.presentation.router.utils

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment

sealed class PixRouterUiState {
    object Loading : PixRouterUiState()

    open class Error(val message: String? = null) : PixRouterUiState()
    data class Unavailable(val statusMessage: String?) : Error(statusMessage)
    data class MfaEligibilityError(val error: ErrorMessage?) : Error()

    abstract class Success : PixRouterUiState()

    object NotEligible : Success()

    object AccreditationRequired : Success()

    object ShowAuthorizationStatus : Success()

    object BlockPennyDrop : Success()

    object EnablePixPartner : Success()

    data class ShowPixExtract(
        val profileType: ProfileType,
        val pixAccount: OnBoardingFulfillment.PixAccount?,
        val settlementScheduled: OnBoardingFulfillment.SettlementScheduled?
    ) : Success()

    data class TokenConfigurationRequired(
        val isOnBoardingViewed: Boolean,
        val pixAccount: OnBoardingFulfillment.PixAccount?
    ) : Success()

    data class ShowPixHome(
        val pixAccount: OnBoardingFulfillment.PixAccount?
    ) : Success()

    data class OnBoardingRequired(
        val pixAccount: OnBoardingFulfillment.PixAccount?
    ) : Success()
}