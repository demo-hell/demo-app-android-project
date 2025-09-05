package br.com.mobicare.cielo.component.impersonate.utils

sealed class UIImpersonateState {

    object SendMessageUpdateMainBottomNavigation : UIImpersonateState()

    object Success : UIImpersonateState()

    object ImpersonateError: UIImpersonateState()

    object LogoutError: UIImpersonateState()
    object WithoutAccess: UIImpersonateState()

}