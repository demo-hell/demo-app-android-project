package br.com.mobicare.cielo.security.presentation.ui.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import androidx.annotation.Keep
import androidx.biometric.BiometricPrompt
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.helpers.BiometricHelper
import br.com.mobicare.cielo.commons.helpers.BiometricHelper.Companion.canAuthenticateWithBiometrics
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.createAead
import br.com.mobicare.cielo.commons.utils.fingerprint.createBiometricPrompt
import br.com.mobicare.cielo.commons.utils.fingerprint.isAndroidVersionOorOMR1
import br.com.mobicare.cielo.commons.utils.fingerprint.saveFingerPrints
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.databinding.SecurityLayoutBinding
import br.com.mobicare.cielo.extensions.errorNotBooting
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.biometricNotification.ui.BiometricNotificationBottomSheetFragment
import br.com.mobicare.cielo.security.presentation.presenter.SecurityPresenter
import br.com.mobicare.cielo.security.presentation.ui.SecurityContract
import com.akamai.botman.CYFMonitor
import com.google.crypto.tink.Aead
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.security.GeneralSecurityException

@Keep
class SecurityFragment : BaseFragment(), SecurityContract.View,
    SecurityBottomSheet.OnDismissListener,
    DialogInterface.OnCancelListener,
    CompoundButton.OnCheckedChangeListener {

    private val presenter: SecurityPresenter by inject {
        parametersOf(this)
    }
    private lateinit var aead: Aead
    private lateinit var fingerprintData: ByteArray

    private val userPreferences: UserPreferences = UserPreferences.getInstance()
    private var securityBottomSheet: SecurityBottomSheet? = null
    private var inProcess: Boolean = false

    private var binding: SecurityLayoutBinding? = null

    companion object {
        fun create(fingerprintData: ByteArray): SecurityFragment =
            SecurityFragment().apply {
                this.fingerprintData = fingerprintData
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMonitor()
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ) = SecurityLayoutBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupMonitor() {
        CYFMonitor.initialize(requireActivity().application, BuildConfig.HOST_API)
    }

    private fun setupView() {
        val isHardwareDetected = BiometricHelper.isHardwareDetected(requireContext())
        binding?.dividerSecurity?.visible(isHardwareDetected)
        binding?.toogleFingerprint?.visible(isHardwareDetected)

        setupCacheClear()
        setupFingerPrint(isHardwareDetected)
    }

    private fun setupFingerPrint(isHardwareDetected: Boolean) {
        if (isHardwareDetected) {
            userPreferences.fingerprintRecorded.also {
                binding?.toogleFingerprint?.isChecked = it
            }
            binding?.toogleFingerprint?.setOnCheckedChangeListener(this)
            configureToolbarActionListener?.changeTo(title = getString(R.string.menu_security))
            securityBottomSheet = SecurityBottomSheet()
        }
    }

    private fun setupCacheClear() {
        binding?.tvCacheClear?.setOnClickListener {
            CieloDialog.create(
                title = getString(R.string.security_text_clear_cache_alert_title),
                message = getString(R.string.security_text_clear_cache_alert_message)
            )
                .setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                .setTitleColor(R.color.brand_600)
                .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                .setPrimaryButton(getString(R.string.security_text_clear_cache_alert_btn))
                .setSecondaryButton(getString(R.string.back))
                .setOnPrimaryButtonClickListener {
                    confirmCacheClear()
                }.show(childFragmentManager, SecurityFragment::class.java.simpleName)
        }
    }

    private fun confirmCacheClear() {
        UserPreferences.getInstance().cacheClear(requireContext())
        baseLogout(isLoginScreen = false)
    }

    override fun showFingerprintCaptureSuccess(activate: Boolean) {
        inProcess = false
        if (activate) {
            if (isAndroidVersionOorOMR1()) {
                saveFingerPrints(requireContext())
                encryptData()
            } else {
                encryptData()
            }
            userPreferences.saveFingerprintRecorded(true)
            requireActivity().showMessage(
                requireContext().getString(R.string.text_fingerprint_registration_success),
                requireContext().getString(R.string.text_fingerprint_registration_title)
            ) {
                this.setBtnRight(requireContext().getString(R.string.ok))
                this.setCancelable(false)
            }
        } else {
            userPreferences.cleanFingerprintData()
        }
    }

    override fun showFingerprintCaptureError(errorCustomMessage: Int) {
        binding?.toogleFingerprint?.isChecked = binding?.toogleFingerprint?.isChecked?.not() == true
        if (isAttached() && errorCustomMessage != R.string.text_not_enrolled)
            requireActivity()
                .showMessage(
                    message = getString(errorCustomMessage),
                    title = getString(R.string.text_fingerprint_prompt_error_title)
                ) {
                    this.setBtnRight(getString(R.string.ok))
                }
        else
            requireActivity()
                .showMessage(
                    message = getString(errorCustomMessage),
                    title = getString(R.string.text_fingerprint_prompt_error_title)
                ) {
                    this.setBtnRight(getString(R.string.ok))
                    this.setOnclickListenerRight {
                        startActivityForResult(Intent(Settings.ACTION_SECURITY_SETTINGS), 0)
                    }
                }
    }

    private fun encryptData() {
        try {
            aead = requireActivity().createAead(
                BiometricNotificationBottomSheetFragment.KEY_NAME,
                BiometricNotificationBottomSheetFragment.MASTER_KEY_URI
            )
            val encrypted = aead.encrypt(
                userPreferences.keepUserPassword.toByteArray(),
                null
            )
            userPreferences.saveFingerprintData(encrypted)
        } catch (ex: GeneralSecurityException) {
            FirebaseCrashlytics.getInstance()
                .log(getString(R.string.security_log, ex.message))
            userPreferences.cleanFingerprintData()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val enableFingerPrint = isChecked
                && userPreferences.fingerprintRecorded.not()
                && canAuthenticateWithBiometrics(requireContext())
        when {
            userPreferences.keepLogin && inProcess.not() -> {
                inProcess = true
                presenter.enableFingerPrint(enable = enableFingerPrint)
            }
            inProcess.not() -> {
                securityBottomSheet?.let {
                    it.onDismissListener = this@SecurityFragment
                    it.onCancelListener = this@SecurityFragment
                    it.show(
                        requireActivity().supportFragmentManager,
                        tag
                    )
                    inProcess = true
                }
            }
            inProcess -> inProcess = false
        }
    }

    override fun biometricPrompt(
        activate: Boolean,
        authenticationCallback: BiometricPrompt.AuthenticationCallback
    ) {

        this.createBiometricPrompt(
            title = if (activate) requireContext()
                .getString(R.string.text_fingerprint_prompt_title) else requireContext()
                .getString(R.string.text_fingerprint_prompt_title_disable),
            description = if (activate) requireContext()
                .getString(R.string.text_fingerprint_prompt_description) else requireContext()
                .getString(R.string.text_fingerprint_prompt_description_disable),
            biometricPromptAuthCallback = authenticationCallback
        )
    }

    override fun onDismiss() {
        val enableFingerPrint = userPreferences.fingerprintRecorded.not()
                && canAuthenticateWithBiometrics(requireContext())
        presenter.enableFingerPrint(enable = enableFingerPrint)
    }

    override fun onNotBooting() {
        requireActivity().errorNotBooting(
            onAction = {
                setupMonitor()
                binding?.toogleFingerprint?.performClick()
            },
            message = getString(R.string.error_not_booting_login_message)
        )
    }

    override fun onCancel(dialog: DialogInterface?) {
        binding?.toogleFingerprint?.isChecked = binding?.toogleFingerprint?.isChecked?.not() == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}


