package br.com.mobicare.cielo.biometricToken.presentation.selfie

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.ENABLE_ID
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.ERROR_VALIDATE_SELFIE
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.SCREEN_VIEW_BIOMETRIC_TIPS_SELFIE
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.VALIDATE_SELFIE
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_IS_LOGIN_FLOW
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SCREEN_NAME
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SCREEN_NAME_EXCEPTION
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SCREEN_NAME_GENERIC_ERROR
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SELFIE_SDK
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_USERNAME
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.STONEAGE
import br.com.mobicare.cielo.commons.constants.LgpdLinks
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.SIXTY_DOUBLE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.getStoneAgeEnvironment
import br.com.mobicare.cielo.commons.helpers.getStoneAgeTheme
import br.com.mobicare.cielo.commons.helpers.getUnicoConfig
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.convertBase64
import br.com.mobicare.cielo.commons.utils.createError
import br.com.mobicare.cielo.commons.utils.spannable.SpannableLink
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.databinding.FragmentBiometricTokenSelfieBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.showApplicationConfiguration
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.SCREEN_NAME_FORGOT_PASSWORD
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.WARNING
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_GENERIC_ERROR
import br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendSelfie.IDOnboardingPicturesSelfieGuideFragment
import br.com.stoneage.identify.enums.LiveSelfieValidationError
import br.com.stoneage.identify.models.LiveSelfieParameters
import br.com.stoneage.identify.models.LiveSelfieResult
import br.com.stoneage.identify.models.LiveSelfieResultError
import br.com.stoneage.identify.sdk.LiveSelfieActivity
import br.com.stoneage.identify.sdk.STAUserSession
import com.acesso.acessobio_android.AcessoBioListener
import com.acesso.acessobio_android.iAcessoBioSelfie
import com.acesso.acessobio_android.onboarding.AcessoBio
import com.acesso.acessobio_android.onboarding.camera.CameraListener
import com.acesso.acessobio_android.onboarding.camera.UnicoCheckCamera
import com.acesso.acessobio_android.onboarding.camera.UnicoCheckCameraOpener
import com.acesso.acessobio_android.services.dto.ErrorBio
import com.acesso.acessobio_android.services.dto.ResultCamera
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class BiometricTokenSelfieFragment : BaseFragment(), CieloNavigationListener,
    BiometricTokenSelfieContract.View, AllowMeContract.View {

    companion object {
        const val TAG = "Unico"
    }

    private val presenter: BiometricTokenSelfiePresenter by inject {
        parametersOf(this)
    }
    private var binding: FragmentBiometricTokenSelfieBinding? = null
    private var navigation: CieloNavigation? = null
    private var cameraListener: iAcessoBioSelfie? = null
    private var unicoCheckCamera: UnicoCheckCamera? = null
    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val analyticsGA4: BiometricTokenGA4 by inject()
    private lateinit var mAllowMeContextual: AllowMeContextual

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                showExplainPermissionBS()
            }
        }

    private var stoneAgeSelfieListener: ActivityResultLauncher<Intent>? = null

    private val cameraSDK: String? by lazy {
        arguments?.getString(ARG_BIOMETRIC_SELFIE_SDK)
    }
    private val userName: String? by lazy {
        arguments?.getString(ARG_BIOMETRIC_USERNAME)
    }
    private val screeNameGA: String? by lazy {
        arguments?.getString(ARG_BIOMETRIC_SCREEN_NAME)
    }
    private val screeNameExceptionGA: String? by lazy {
        arguments?.getString(ARG_BIOMETRIC_SCREEN_NAME_EXCEPTION)
    }
    private val screeNameGenericErrorGA: String? by lazy {
        arguments?.getString(ARG_BIOMETRIC_SCREEN_NAME_GENERIC_ERROR)
    }
    private val isLoginFlow: Boolean? by lazy {
        arguments?.getBoolean(ARG_BIOMETRIC_IS_LOGIN_FLOW, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBiometricTokenSelfieBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setupStoneAgeListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAllowMeContextual = allowMePresenter.init(requireContext())
        setupNavigation()
        setupText()
        setupListeners()
        setupSelfieCam()

        screeNameGA?.let { analyticsGA4.logScreenView(it) }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupText() {
        binding?.apply {
            tvTitle.setText(R.string.biometric_token_pictures_selfie_guide_title)
            tvSubtitle.setText(R.string.biometric_token_pictures_selfie_guide_subtitle)
            tvSelfieGlasses.setText(R.string.biometric_token_pictures_selfie_guide_glasses)
            tvSelfieSun.fromHtml(R.string.biometric_token_pictures_selfie_guide_sun)
            tvSelfiePosition.setText(R.string.biometric_token_pictures_selfie_guide_position)
            tfTermsReadConditions.setText(textOfTermsFormat(), TextView.BufferType.SPANNABLE)
            tfTermsReadConditions.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun textOfTermsFormat(): SpannableStringBuilder {
        return getString(R.string.biometric_token_selfie_security_primary_text)
            .addSpannable(
                TextAppearanceSpan(
                    requireContext(),
                    R.style.regular_montserrat_12_neutral_600_spacing_1
                )
            ).append(ONE_SPACE)
            .append(
                getString(R.string.biometric_token_selfie_security_secondary_text)
                    .addSpannable(
                        TextAppearanceSpan(
                            requireContext(),
                            R.style.regular_montserrat_12_accent_800
                        ),
                        SpannableLink({ openTermUrl() }, isUnderline = false)
                    )
            )
    }

    private fun openTermUrl() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(LgpdLinks.CieloPolicy)
            )
        )
    }

    private fun setupListeners() {
        binding?.apply {
            btBackArrow.setOnClickListener {
                activity?.onBackPressed()
            }

            btNext.setOnClickListener {
                navigation?.showLoading(true)
                screeNameGA?.let { itGA ->
                    if (isLoginFlow == true) {
                        analyticsGA4.logScreenView(itGA)
                    } else {
                        analyticsGA4.logOpenCameraClick(itGA)
                    }
                }
                checkCameraPermission()
            }
        }
    }

    private fun startCamera() {
        if (isLoginFlow == true) {
            analyticsGA4.logScreenView(VALIDATE_SELFIE)
        }

        if (presenter.isForeign() || cameraSDK == STONEAGE) {
             openStoneAgeSelfie()
        } else {
            openUnicoSelfie()
        }
    }

    private fun openUnicoSelfie() {
        unicoCheckCamera?.prepareCamera(getUnicoConfig(), object : CameraListener {
            override fun onCameraReady(cameraOpener: UnicoCheckCameraOpener.Camera) {
                cameraListener?.let {
                    cameraOpener.open(it)
                    navigation?.showLoading(false)
                } ?: kotlin.run {
                    setupSelfieCam()
                    cameraListener?.let { onCameraReady(cameraOpener) }
                }
            }

            override fun onCameraFailed(message: String?) {
                val error = message
                    ?: getString(R.string.id_onboarding_pictures_selfie_guide_cam_error_message)

                Timber.tag(TAG).e(error)
                Firebase.crashlytics.log("$TAG $error")

                setupError(message = error)
            }
        }) ?: run {
            setupSelfieCam()
            binding?.btNext?.callOnClick()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showExplainPermissionBS()
            }

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setupSelfieCam() {
        doWhenResumed(
            action = {
                val callback = object : AcessoBioListener {
                    override fun onErrorAcessoBio(errorBio: ErrorBio?) {
                        Firebase.crashlytics.log("$TAG | cod:${errorBio?.code} - ${errorBio?.description}")
                        setupError(errorBio?.code, errorBio?.description)
                    }

                    override fun onUserClosedCameraManually() {}

                    override fun onSystemClosedCameraTimeoutSession() {}

                    override fun onSystemChangedTypeCameraTimeoutFaceInference() {}
                }

                unicoCheckCamera = AcessoBio(requireContext(), callback)
                    .setAutoCapture(false)
                    .setSmartFrame(false)
                    .setTimeoutSession(SIXTY_DOUBLE)
                    .build()

                cameraListener = object : iAcessoBioSelfie {
                    override fun onSuccessSelfie(result: ResultCamera?) {
                        result?.let { resultCamera ->
                            doWhenResumed(
                                action = {
                                    presenter.sendBiometricSelfie(
                                        resultCamera.base64,
                                        resultCamera.encrypted,
                                        userName
                                    )
                                }
                            )
                        } ?: run {
                            showError()
                        }
                    }

                    override fun onErrorSelfie(errorBio: ErrorBio?) {
                        val error = errorBio?.description
                            ?: getString(R.string.id_onboarding_pictures_selfie_guide_selfie_error_message)

                        Firebase.crashlytics.log("$TAG | cod:${errorBio?.code} - $error")
                        setupError(errorBio?.code, errorBio?.description)
                    }
                }
            },
            errorCallback = { showError() }
        )
    }

    override fun onShowSelfieLoading() {
        navigation?.showAnimatedLoading(message = R.string.biometric_token_validate_photo)
    }

    override fun hideAnimatedLoading() {
        navigation?.hideAnimatedLoading()
    }

    override fun onSuccessSelfie() {
        if (cameraSDK.isNullOrEmpty() && userName.isNullOrEmpty()){
            navigation?.hideAnimatedLoading()
            navigation?.showAnimatedLoading(message = R.string.biometric_token_installing_token)
            generateHash()
        } else {
            doWhenResumed {
                navigation?.hideAnimatedLoading()
                navigateToPassword()
            }
        }
    }

    override fun onSelfieError() {
        if (isLoginFlow == true) {
            analyticsGA4.logDisplayContent(
                screenName = VALIDATE_SELFIE,
                description = ERROR_VALIDATE_SELFIE,
                contentType = WARNING
            )
        }

        doWhenResumed(
            action = {
                navigation?.hideAnimatedLoading()
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.biometric_home_bs_title),
                    message = getString(R.string.biometric_home_bs_message),
                    bt2Title = getString(R.string.entendi),
                    isCancelable = true,
                    isPhone = false
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun onSuccessRegister() {
        navigation?.hideAnimatedLoading()
        findNavController().navigate(BiometricTokenSelfieFragmentDirections.actionBiometricTokenSelfieFragmentToBiometricTokenSuccessFragment())
    }

    override fun showError(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        doWhenResumed(
            action = {
                logException(error)

                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.error_title_something_wrong),
                    message = HtmlCompat.fromHtml(
                        getString(R.string.id_onboarding_validate_p2_generic_error),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).toString(),
                    bt2Title = if (retryCallback != null)
                        getString(R.string.entendi)
                    else
                        null,
                    bt2Callback = {
                        retryCallback?.invoke()
                        false
                    }
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun setupError(code: Int? = null, message: String?) {
        try {
            val errorCode = code?.toString() ?: ANALYTICS_ID_GENERIC_ERROR
            showError(
                createError(errorCode, message)
            )
        } catch (ex: Exception) {
            showError(
                createError(ANALYTICS_ID_GENERIC_ERROR, message)
            )
        }
    }

    private fun generateHash() {
        allowMePresenter.collect(
            mAllowMeContextual = mAllowMeContextual,
            context = requireActivity(),
            mandatory = false
        )
    }

    override fun successCollectToken(result: String) {
        presenter.sendBiometricDevice(result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        if (mandatory) {
            showAlert(getString(R.string.text_title_error_fingerprint_allowme), errorMessage)
        } else {
            showError(
                ErrorMessage().apply {
                    this.errorMessage = errorMessage
                }
            )
        }
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    override fun showError(error: ErrorMessage?) {
        logException(error)

        navigation?.hideAnimatedLoading()
        navigation?.showError(error)
    }

    private fun logException(error: ErrorMessage?){
        screeNameGenericErrorGA?.let {
            analyticsGA4.logException(screenName = it,
                description = if (error?.errorMessage.isNullOrEmpty())
                    error?.errorCode.orEmpty()
                else error?.errorMessage.orEmpty(),
                code = error?.code.orEmpty())
        }
    }

    private fun showAlert(title: String, message: String) {
        CieloAlertDialogFragment
            .Builder()
            .title(title)
            .message(message)
            .closeTextButton(getString(R.string.ok))
            .build().showAllowingStateLoss(
                childFragmentManager,
                getString(R.string.text_cieloalertdialog)
            )
    }

    private fun setupStoneAgeListener() {
        stoneAgeSelfieListener = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val data = result.data
                    data?.let { d ->
                        d.getParcelableExtra<LiveSelfieResult>(
                            IDOnboardingPicturesSelfieGuideFragment.RESULT
                        )?.let {
                            doWhenResumed(
                                action = {
                                    if (it.selfieFile?.exists() == true){
                                        presenter.sendBiometricSelfie(
                                            it.selfieFile?.convertBase64(),
                                            it.selfieRequestId,
                                            userName
                                        )
                                    }
                                },
                                errorCallback = {
                                    genericError()
                                }
                            )
                        }
                    } ?:genericError()
                }
                else -> {
                    val data = result.data
                    data?.let { d ->
                        d.getParcelableExtra<LiveSelfieResultError>(
                            IDOnboardingPicturesSelfieGuideFragment.ERROR
                        )?.let {
                            doWhenResumed(
                                action = {
                                    if (it.errorMessage != null && it.validationErrors.isNullOrEmpty().not()){
                                        processCaptureError(it.validationErrors?.first())
                                    } else {
                                        genericError()
                                    }
                                },
                                errorCallback = {
                                    genericError()
                                }
                            )
                        }
                    } ?: genericError()
                }
            }
        }
    }

    private fun openStoneAgeSelfie() {
        doWhenResumed {
            presenter.getStoneAgeToken()
        }
    }

    override fun successStoneAgeToken(token: String) {
        doWhenResumed {
            STAUserSession.initialize(
                stage = getStoneAgeEnvironment(),
                sdkToken = token,
                owner = ContextWrapper(requireContext()),
                theme = getStoneAgeTheme(requireContext())
            )
            val intent = Intent(requireActivity(), LiveSelfieActivity::class.java)
            intent.putExtra(
                IDOnboardingPicturesSelfieGuideFragment.PARAMETERS, LiveSelfieParameters(
                    ZERO
                )
            )
            stoneAgeSelfieListener?.launch(intent) ?: genericError()
        }
    }

    override fun errorStoneAgeToken() {
        doWhenResumed {
            processCaptureError(LiveSelfieValidationError.UNKNOWN_ERROR)
        }
    }

    private fun genericError() {
        doWhenResumed {
            processCaptureError(LiveSelfieValidationError.UNKNOWN_ERROR)
        }
    }

    private fun processCaptureError(error: LiveSelfieValidationError?) {
        if (isLoginFlow == true) {
            analyticsGA4.logDisplayContent(
                screenName = VALIDATE_SELFIE,
                description = ERROR_VALIDATE_SELFIE,
                contentType = WARNING
            )
        }

        navigation?.showLoading(false)
        navigation?.hideAnimatedLoading()
        val message = requireContext().getString(
            when(error) {
                LiveSelfieValidationError.IMAGE_NOT_GOOD_ENOUGH -> {
                    R.string.selfie_image_not_good_message
                }
                LiveSelfieValidationError.NO_FACE_DETECTED -> {
                    R.string.selfie_no_face_detected_message
                }
                LiveSelfieValidationError.TOO_MANY_FACES -> {
                    R.string.selfie_too_many_faces_message
                }
                LiveSelfieValidationError.WEARING_HAT -> {
                    R.string.selfie_wearing_hat_message
                }
                LiveSelfieValidationError.WEARING_GLASSES -> {
                    R.string.selfie_wearing_glasses_message
                }
                LiveSelfieValidationError.WEARING_READING_GLASSES -> {
                    R.string.selfie_wearing_glasses_message
                }
                LiveSelfieValidationError.WEARING_MASK -> {
                    R.string.selfie_wearing_mask_message
                }
                LiveSelfieValidationError.FACE_NOT_CENTERED -> {
                    R.string.selfie_face_not_centered_message
                }
                LiveSelfieValidationError.TILTED_FACE ->{
                    R.string.selfie_face_not_centered_message
                }
                LiveSelfieValidationError.FACE_TOO_FAR -> {
                    R.string.selfie_face_too_far_message
                }
                LiveSelfieValidationError.FACE_IS_SMILING -> {
                    R.string.selfie_face_is_smiling_message
                }
                LiveSelfieValidationError.FACE_TOO_CLOSE -> {
                    R.string.selfie_face_too_close_message
                }
                LiveSelfieValidationError.FACE_TOO_BRIGHT -> {
                    R.string.selfie_face_too_bright_message
                }
                LiveSelfieValidationError.FACE_TOO_DARK -> {
                    R.string.selfie_face_too_dark_message
                }
                else -> {
                    R.string.capture_document_unknown_error_message
                }
            }
        )

        screeNameExceptionGA?.let { analyticsGA4.logExceptionErrorSelfie(it, error) }

        bottomSheetGenericFlui(
            image = R.drawable.img_selfie_correta,
            title = requireContext().getString(R.string.selfie_error_title),
            subtitle = message,
            nameBtn2Bottom = requireContext().getString(R.string.text_try_again_label),
            statusNameTopBar = false,
            statusBtnClose = false,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = false,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        if (screeNameGA.equals(SCREEN_VIEW_BIOMETRIC_TIPS_SELFIE))
                            analyticsGA4.logTryAgainClick()
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                    }

                    override fun onCancel() {
                        dismiss()
                    }
                }
        }.show(childFragmentManager, requireContext().getString(R.string.capture_document_error_bottom_sheet))
    }

    private fun showExplainPermissionBS() {
        if (isLoginFlow == true) {
            analyticsGA4.logDisplayContent(
                screenName = SCREEN_NAME_FORGOT_PASSWORD,
                description = ENABLE_ID,
                contentType = WARNING
            )
        }

        navigation?.showCustomBottomSheet(
            title = getString(R.string.id_onboarding_pictures_selfie_guide_permission_title),
            message = getString(R.string.id_onboarding_pictures_selfie_guide_permission_message),
            bt1Title = getString(R.string.id_onboarding_pictures_selfie_guide_permission_not_enable),
            bt1Callback = {
                baseLogout(true)
                false
            },
            bt2Title = getString(R.string.id_onboarding_pictures_selfie_guide_permission_enable),
            bt2Callback = {
                showApplicationConfiguration()
                false
            },
            isPhone = false
        ) ?: baseLogout()
    }

    private fun navigateToPassword() {
        findNavController().navigate(
            BiometricTokenSelfieFragmentDirections.actionBiometricTokenSelfieFragmentToBiometricTokenPasswordFragment(
                userName, presenter.getToken()
            )
        )
    }
}