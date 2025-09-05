package br.com.mobicare.cielo.posVirtual.utils

import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtual

sealed class UIPosVirtualRouterState {
    data class Loading(val isLoading: Boolean = true) : UIPosVirtualRouterState()

    abstract class Success : UIPosVirtualRouterState()

    object StatusPending : Success()
    object StatusCanceled : Success()
    object StatusFailed : Success()
    data class StatusSuccess(val data: PosVirtual) : Success()
    data class ImpersonateRequired(val data: PosVirtual) : Success()

    abstract class Error : UIPosVirtualRouterState()
    object AccreditationRequired : Error()
    object OnBoardingRequired : Error()
    data class GenericError(val message: String? = null) : Error()
}
