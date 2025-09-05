package br.com.mobicare.cielo.security.presentation.presenter

import androidx.biometric.BiometricPrompt
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.security.presentation.ui.SecurityContract
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

import org.mockito.Mockito

private const val ERROR_HW_UNAVAILABLE = 1
private const val ERROR_NO_BIOMETRICS = 11

class SecurityPresenterUnitTest {

    private lateinit var mView: SecurityContract.View
    private lateinit var presenter: SecurityPresenter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mView = Mockito.mock(SecurityContract.View::class.java)
        presenter = SecurityPresenter(mView)
    }


    @Test
    fun enableFingerPrint() {
        val enableFingerPrint = true
        val callbackCaptor = argumentCaptor<BiometricPrompt.AuthenticationCallback>()

        presenter.enableFingerPrint(enableFingerPrint)
        verify(mView).biometricPrompt(eq(enableFingerPrint), callbackCaptor.capture())
        callbackCaptor.firstValue.onAuthenticationSucceeded(mock())
        verify(mView).showFingerprintCaptureSuccess(enableFingerPrint)
    }

    @Test
    fun disableFingerPrint() {
        val enableFingerPrint = false
        val callbackCaptor = argumentCaptor<BiometricPrompt.AuthenticationCallback>()

        presenter.enableFingerPrint(enableFingerPrint)
        verify(mView).biometricPrompt(eq(enableFingerPrint), callbackCaptor.capture())
        callbackCaptor.firstValue.onAuthenticationSucceeded(mock())
        verify(mView).showFingerprintCaptureSuccess(enableFingerPrint)
    }

    @Test
    fun enableFingerPrintError() {
        val enableFingerPrint = true
        val callbackCaptor = argumentCaptor<BiometricPrompt.AuthenticationCallback>()

        presenter.enableFingerPrint(enableFingerPrint)
        verify(mView).biometricPrompt(eq(enableFingerPrint), callbackCaptor.capture())
        callbackCaptor.firstValue.onAuthenticationError(
            ERROR_HW_UNAVAILABLE,
            mock()
        )
        verify(mView).showFingerprintCaptureError(R.string.text_unavailable)
    }

    @Test
    fun disableFingerPrintError() {
        val enableFingerPrint = false
        val callbackCaptor = argumentCaptor<BiometricPrompt.AuthenticationCallback>()

        presenter.enableFingerPrint(enableFingerPrint)
        verify(mView).biometricPrompt(eq(enableFingerPrint), callbackCaptor.capture())
        callbackCaptor.firstValue.onAuthenticationError(
            ERROR_HW_UNAVAILABLE, mock()
        )
        verify(mView).showFingerprintCaptureError(R.string.text_unavailable)
    }


    @Test
    fun enableFingerPrintErrorNoBiometrics() {
        val enableFingerPrint = true
        val callbackCaptor = argumentCaptor<BiometricPrompt.AuthenticationCallback>()

        presenter.enableFingerPrint(enableFingerPrint)
        verify(mView).biometricPrompt(eq(enableFingerPrint), callbackCaptor.capture())
        callbackCaptor.firstValue.onAuthenticationError(
            ERROR_NO_BIOMETRICS,
            mock()
        )
        verify(mView).showFingerprintCaptureError(R.string.text_not_enrolled)
    }

    @Test
    fun disableFingerPrintErrorNoBiometrics() {
        val enableFingerPrint = false
        val callbackCaptor = argumentCaptor<BiometricPrompt.AuthenticationCallback>()

        presenter.enableFingerPrint(enableFingerPrint)
        verify(mView).biometricPrompt(eq(enableFingerPrint), callbackCaptor.capture())
        callbackCaptor.firstValue.onAuthenticationError(
            ERROR_NO_BIOMETRICS, mock()
        )
        verify(mView).showFingerprintCaptureError(R.string.text_not_enrolled)
    }


}