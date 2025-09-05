package br.com.mobicare.cielo.mfa.activation

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse

interface PutValueView {

    fun initExplanationSpannable()
    fun initTextChange()
    fun onValueSuccess()
    fun tokenLostWarning()
    fun onValueError(error: ErrorMessage)
    fun onInvalidRequestError(error: ErrorMessage)
    fun onBusinessError(error: ErrorMessage)
    fun incorrectValues()
    fun incorrectValuesThirdAttempt()
    fun showLoading()
    fun hideLoading()
    fun hideEnrollmentActiveBank()
    fun configureActiveBank(it: EnrollmentBankResponse?)
}