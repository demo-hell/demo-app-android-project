package br.com.mobicare.cielo.commons.presentation

import androidx.annotation.StringRes
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface BaseView {

    fun showLoading() { showLoading(null) }
    fun hideLoading() { hideLoading(null, null) }

    fun showLoading(@StringRes loadingMessage: Int? = null, vararg messageArgs: String) {}
    fun hideLoading(@StringRes successMessage: Int? = null, loadingSuccessCallback: (() -> Unit)? = null, vararg messageArgs: String) {}

    fun showLoadingMore() {}
    fun hideLoadingMore() {}

    fun showError(error: ErrorMessage? = null) { showError(error, null) }
    fun showError(error: ErrorMessage? = null, retryCallback: (() -> Unit)? = null) {}

    fun logout(msg: ErrorMessage? = null) {}

    fun lockScreen() {}
    fun unlockScreen() {}

    fun showSuccess(result: Any) {}

}