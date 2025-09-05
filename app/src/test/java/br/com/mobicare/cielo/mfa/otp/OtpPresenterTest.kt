package br.com.mobicare.cielo.mfa.otp

import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.secure.presentation.ui.presenter.OtpContract
import br.com.mobicare.cielo.commons.secure.presentation.ui.presenter.OtpPresenter
import br.com.mobicare.cielo.commons.utils.totp.TotpClock
import br.com.mobicare.cielo.commons.utils.totp.TotpCounter
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.mfa.token.CieloMfaTokenGenerator
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.atLeast
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class OtpPresenterTest {

    @Mock
    lateinit var view: OtpContract.View

    @Mock
    lateinit var tOtpClock: TotpClock

    @Mock
    lateinit var tOtpCounter: TotpCounter

    @Mock
    lateinit var cieloMfaTokenGenerator: CieloMfaTokenGenerator

    @Mock
    lateinit var mfaUserInformation: MfaUserInformation

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    lateinit var presenter: OtpPresenter

    private val newSeedPattern = "GRXV J2PQ O5BT MUCP N5RX E4BZ TDPX OLVT"
    private val oldSeedPattern = "::DIGS=00::ORG_=MFA_SITE1::TSTP=00::TVAR=0::TYPE=TOTP::PTYP=8L2d::UDK_=D00F0000B00::UIDS=0001::UID_=000000::USER=000000::VER_=0.0.0::"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = OtpPresenter(cieloMfaTokenGenerator, view, tOtpClock, tOtpCounter, mfaUserInformation, userPreferences)
    }

    @Test
    fun `Check success on showOtpCodeForFirstTime`() {
        presenter.seed = newSeedPattern
        doReturn("123456").whenever(cieloMfaTokenGenerator).getOtpCode(any(), eq(true))

        presenter.showOtpCodeForFirstTime()

        verify(view, atLeast(3)).updateCountdownAnimation(any())
        verify(view).showOtp(any())
        verify(view, never()).errorOnOtpGeneration(any())
    }

    @Test
    fun `Check error on showOtpCodeForFirstTime`() {
        presenter.seed = oldSeedPattern
        presenter.showOtpCodeForFirstTime()

        verify(view).errorOnOtpGeneration(any())
        verify(view, never()).updateCountdownAnimation(any())
        verify(view, never()).showOtp(any())
    }

    @Test
    fun `Check if the cieloMfaTokenGenerator_seedHasCorrectPattern() returns true if the pattern it is correct`() {
        val itsValid = CieloMfaTokenGenerator.seedHasCorrectPattern(newSeedPattern)

        assertTrue(itsValid)
    }

    @Test
    fun `Check if the cieloMfaTokenGenerator_seedHasCorrectPattern() returns false if the pattern isn't correct`() {
        val itsValid = CieloMfaTokenGenerator.seedHasCorrectPattern(oldSeedPattern)

        assertFalse(itsValid)
    }
}