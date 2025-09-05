package br.com.mobicare.cielo.commons.ui

import android.os.Bundle
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface ActivityStepCoordinatorListener {
    @Deprecated("Utilizar o outro onNextStep")
    fun onNextStep(isFinish: Boolean){}
    fun onNextStep(isFinish: Boolean, bundle: Bundle? = null){}
    fun onNextStep(isFinish: Boolean, bundle: Bundle? = null, step: Pair<Int, Int>) {}
    fun onBackToStep(bundle: Bundle? = null, step: Pair<Int, Int>) {}
    fun onLogout(){}
    fun setTitle(title: String){}
    fun setButtonName(title: String) {}
    fun onShowLoading() {}
    fun onHideLoading() {}
    fun onShowContent() {}
    fun onShowError(error: ErrorMessage) {}
    fun onTextChangeButton(text: String) {}
    fun enableNextButton(isEnabled: Boolean) {}
    fun onShowNextButton(isVisible: Boolean) {}
    fun onShowAlert(title: String? = null, message: String) {}
}