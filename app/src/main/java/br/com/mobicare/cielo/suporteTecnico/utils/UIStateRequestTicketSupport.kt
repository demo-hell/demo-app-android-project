package br.com.mobicare.cielo.suporteTecnico.utils

sealed class UIStateRequestTicketSupport{

    object AuthorizationSuccess: UIStateRequestTicketSupport()
    object AuthorizationError: UIStateRequestTicketSupport()
    object Error: UIStateRequestTicketSupport()
    object Loading: UIStateRequestTicketSupport()
    object Empty: UIStateRequestTicketSupport()
}