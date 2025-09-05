package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateRedirect {
    object ExpiredTimeConsent : UIStateRedirect()
    object ConsentActive : UIStateRedirect()
}