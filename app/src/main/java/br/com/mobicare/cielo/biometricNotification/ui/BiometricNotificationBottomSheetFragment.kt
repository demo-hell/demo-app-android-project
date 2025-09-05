package br.com.mobicare.cielo.biometricNotification.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.helpers.BiometricHelper
import br.com.mobicare.cielo.commons.utils.createAead
import br.com.mobicare.cielo.commons.utils.fingerprint.createBiometricPrompt
import br.com.mobicare.cielo.commons.utils.fingerprint.isAndroidVersionOorOMR1
import br.com.mobicare.cielo.commons.utils.fingerprint.saveFingerPrints
import br.com.mobicare.cielo.databinding.FragmentBiometricNotificationBottomSheetBinding
import br.com.mobicare.cielo.notification.presenter.BiometricNotificationBottomSheetContract
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.crypto.tink.Aead
import org.koin.androidx.viewmodel.ext.android.viewModel

class BiometricNotificationBottomSheetFragment : BottomSheetDialogFragment(),
BiometricNotificationBottomSheetContract.View {

    private var isBottomSheetError = false
    private lateinit var aead: Aead
    private lateinit var fingerprintData: ByteArray
    private val viewModel: BiometricNotificationBottomSheetViewModel by viewModel()

    private var binding: FragmentBiometricNotificationBottomSheetBinding? = null

    companion object {
        val KEY_NAME = "br.com.cielo.welcomeInfoNotification.data"

        const val FINGERPRINT_EL_KEY = "br.com.cielo.notification.fingerprintElKey"
        const val MASTER_KEY_URI = "android-keystore://fingerprint_key"

        fun create(fingerprintData: ByteArray): BiometricNotificationBottomSheetFragment =
            BiometricNotificationBottomSheetFragment().apply {
                this.fingerprintData = fingerprintData
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBiometricNotificationBottomSheetBinding.inflate(
        inflater,
        container,
        false
    ).also{
        setupBottomSheet()
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupBottomSheet() {
        dialog?.setOnShowListener {
            val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)

                behavior.disableShapeAnimations()
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = ZERO

                dialog?.setCancelable(false)
                dialog?.setCanceledOnTouchOutside(false)

                behavior.isHideable = false
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING)
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
    }

    private fun setupView(){
        viewModel.saveCalledBiometricNotificationByLogin(false)
        binding?.apply {
            if (isBottomSheetError){
                imvTitle.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_content_symbol_close_round_red_500_72_dp)
                tvTitle.text = getString(R.string.enable_biometric_title_error)
                tvSubTitle.text = getString(R.string.enable_biometric_subtitle_error)
                btnNotEnable.visibility = View.GONE
                btnEnableNow.setText(getString(R.string.entendi))
            }
        }
    }

    private fun setupListeners(){
        binding?.apply {
            btnEnableNow.setOnClickListener{
                if (isBottomSheetError)
                    dismiss()
                else
                    if (BiometricHelper.canAuthenticateWithBiometrics(requireContext()))
                        showBiometricPrompt()

        }
            btnNotEnable.setOnClickListener{
                viewModel.saveShowBiometricNotification(false)
                dismiss()
            }

            imvClose.setOnClickListener{
                dismiss()
            }
        }
    }

    fun encryptData() {
        aead = requireActivity().createAead(KEY_NAME, MASTER_KEY_URI)
        val encrypted = aead.encrypt( viewModel.getKeepUserPassword().toByteArray(), null)
        viewModel.saveFingerprintData(encrypted)
    }

    override fun showBiometricPrompt() {
        this.createBiometricPrompt(title = getString(R.string.cielo),
            description = getString(R.string.enable_biometric_message),
            biometricPromptAuthCallback = object :
                BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (isAndroidVersionOorOMR1()) {
                        saveFingerPrints(requireContext())
                        encryptData()
                    }else{
                        encryptData()
                    }
                    viewModel.saveFingerprintRecorded(true)
                    dismiss()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    viewModel.saveShowBiometricNotification(true)
                    if (errorCode == ERROR_NEGATIVE_BUTTON){
                        showErrorBiometricPrompt(this)
                        isBottomSheetError = true
                        setupView()
                    }
                }
            })
    }

    fun showErrorBiometricPrompt(biometricPromptAuthCallback: BiometricPrompt.AuthenticationCallback) {
        this.createBiometricPrompt(title = getString(R.string.cielo),
            description = getString(R.string.enable_biometric_message_not_correspound),
            biometricPromptAuthCallback = biometricPromptAuthCallback)
    }
}