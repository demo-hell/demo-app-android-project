package br.com.mobicare.cielo.tapOnPhone.utils

import androidx.annotation.StringRes
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface TapOnPhoneBaseView {
    fun onChangeLoadingText(@StringRes message: Int? = null) {}
    fun onShowLoading(@StringRes message: Int? = null) {}
    fun onShowLoadingAlert(@StringRes message: Int? = null) {}
    fun onShowLoadingError(@StringRes message: Int? = null) {}
    fun onShowLoadingSuccess(@StringRes message: Int? = null) {}
    fun onHideLoading() {}
    fun onShowError(error: ErrorMessage? = null) {}
}