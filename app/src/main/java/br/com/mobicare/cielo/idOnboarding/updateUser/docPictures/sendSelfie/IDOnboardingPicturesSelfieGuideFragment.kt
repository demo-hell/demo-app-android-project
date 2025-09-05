package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendSelfie

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.INVALID_SELFIE
import br.com.mobicare.cielo.commons.constants.LgpdLinks.CieloPolicy
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
import br.com.mobicare.cielo.commons.utils.firstWordCapitalize
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingPicturesSelfieGuideBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.showApplicationConfiguration
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ERROR_UNICO
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_GENERIC_ERROR
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SELFIE_TIPS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_TIPS_FOR_A_GOOD_SELFIE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_TIPS_SELFIE
import br.com.mobicare.cielo.idOnboarding.enum.IDOnboardingP2ReprocessErrorEnum
import br.com.mobicare.cielo.idOnboarding.enum.IDOnboardingP2ReprocessErrorTypeEnum
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

class IDOnboardingPicturesSelfieGuideFragment : BaseFragment(), CieloNavigationListener,
    IDOnboardingUploadSelfieContract.View {

    private val presenter: IDOnboardingUploadSelfiePresenter by inject {
        parametersOf(this)
    }

    private val analytics: IDOnboardingP2Analytics by inject()
    private val analyticsGA: IDOnboardingP2AnalyticsGA by inject()

    private var navigation: CieloNavigation? = null
    private var cameraListener: iAcessoBioSelfie? = null
    private var unicoCheckCamera: UnicoCheckCamera? = null
    private var readyToNavigate: Boolean = false
    private var stoneAgeSelfieListener: ActivityResultLauncher<Intent>? = null
    private var _binding: FragmentIdOnboardingPicturesSelfieGuideBinding? = null
    private val binding get() = _binding
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                showExplainPermissionBS()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentIdOnboardingPicturesSelfieGuideBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupText()
        setupListeners()
        setupSelfieCam()
        analytics.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_TIPS_FOR_A_GOOD_SELFIE, this.javaClass)
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_SELFIE_TIPS)
    }

    private fun setupSelfieCam() {
        doWhenResumed(
            action = {
                val callback = object : AcessoBioListener {
                    override fun onErrorAcessoBio(errorBio: ErrorBio?) {
                        Firebase.crashlytics.log("$TAG | cod:${errorBio?.code} - ${errorBio?.description}")
                        setupError(errorBio?.code, errorBio?.description)
                        hideBasicLoading()
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
                        result?.let {
                            doWhenResumed(
                                action = {
                                    presenter.onResume()
                                    presenter.uploadSelfie(it)
                                },
                                errorCallback = { showError() }
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

    private fun startCamera() {
        if (userStatus.onboardingStatus?.userStatus?.foreign == true) {
            doWhenResumed {
                openStoneAgeSelfie()
            }
        } else {
            unicoSelfie()
        }
    }

    private fun unicoSelfie() {
        basicLoading()

        unicoCheckCamera?.prepareCamera(getUnicoConfig(), object : CameraListener {
            override fun onCameraReady(cameraOpener: UnicoCheckCameraOpener.Camera) {
                cameraListener?.let {
                    cameraOpener.open(it)
                    hideBasicLoading()
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

    private fun openStoneAgeSelfie() {
        presenter.getStoneAgeToken()
    }

    override fun errorStoneAgeToken(error: ErrorMessage?) {
        doWhenResumed {
            val errorMessage = error?.message ?: INVALID_SELFIE
            analytics.logIDOnErrorSendSelfie(error?.code, errorMessage)
            processCaptureError(LiveSelfieValidationError.UNKNOWN_ERROR)
        }
    }

    override fun basicLoading() {
        navigation?.showLoading(true)
    }

    override fun hideBasicLoading() {
        navigation?.showLoading(false)
    }

    private fun genericError() {
        doWhenResumed {
            processCaptureError(LiveSelfieValidationError.UNKNOWN_ERROR)
        }
    }

    private fun processCaptureError(error: LiveSelfieValidationError?) {
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

    override fun successStoneAgeToken(token: String) {
        doWhenResumed {
            analytics.logIDOnSuccessSendSelfie()

            STAUserSession.initialize(
                stage = getStoneAgeEnvironment(),
                sdkToken = token,
                owner = ContextWrapper(requireContext()),
                theme = getStoneAgeTheme(requireContext())
            )
            val intent = Intent(requireActivity(), LiveSelfieActivity::class.java)
            intent.putExtra(PARAMETERS, LiveSelfieParameters(ZERO))
            stoneAgeSelfieListener?.launch(intent) ?: genericError()
        }
    }

    private fun setupStoneAgeListener() {
        stoneAgeSelfieListener = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val data = result.data
                    data?.let { d ->
                        d.getParcelableExtra<LiveSelfieResult>(RESULT)?.let {
                            doWhenResumed(
                                action = {
                                    if (it.selfieFile?.exists() == true){
                                        presenter.uploadSelfie(it.selfieFile?.convertBase64(), it.selfieRequestId)
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
                        d.getParcelableExtra<LiveSelfieResultError>(ERROR)?.let {
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

    override fun onPauseActivity() {
        super.onPauseActivity()
        presenter.onPause()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        verifyReadyToNavigate()
        setupStoneAgeListener()
    }

    private fun verifyReadyToNavigate() {
        if (readyToNavigate) {
            navigateToP2()
            readyToNavigate = false
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupText() {
        binding?.apply {
            analyticsGA.logIDSendSelfieDisplay()
            tvTitle.setText(R.string.id_onboarding_pictures_selfie_guide_title)
            tvSubtitle.setText(R.string.id_onboarding_pictures_selfie_guide_subtitle)
            tvSelfieGlasses.setText(R.string.id_onboarding_pictures_selfie_guide_glasses)
            tvSelfieSun.fromHtml(R.string.id_onboarding_pictures_selfie_guide_sun)
            tvSelfiePosition.setText(R.string.id_onboarding_pictures_selfie_guide_position)
            tvSelfiePrivacy.fromHtml(R.string.id_onboarding_pictures_selfie_guide_privacy)
        }
    }

    private fun setupListeners() {
        binding?.apply {
            btBackArrow.setOnClickListener {
                analytics.logIDOnClickComeBack(ANALYTICS_ID_TIPS_SELFIE)
                activity?.onBackPressed()
            }

            btNext.setOnClickListener {
                analytics.logIDOnClickOpenCamera()
                analyticsGA.logIDSendSelfieSignUp()

                showCheckIdentify()
            }

            tvSelfiePrivacy.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(CieloPolicy)
                    )
                )
            }
        }
    }

    private fun showSelfieValidationError() {
        userStatus.onboardingStatus?.userStatus?.p2ReprocessError?.let { error ->
            if (error.source == IDOnboardingP2ReprocessErrorTypeEnum.SELFIE.name)
                error.message?.let { message ->
                    p2ProcessError()?.let { processError ->
                        val titleError = getString(processError.title)
                        analyticsGA.logIDSendSelfieErrorDisplay(titleError)
                        navigation?.showCustomBottomSheet(
                            image = processError.img,
                            title = titleError,
                            message = message,
                            bt2Title = getString(R.string.continuar),
                            bt2Callback = {
                                analyticsGA.logIDSendSelfieErrorSignUp()
                                startCamera()
                                false
                            },
                        )
                    } ?: checkCameraPermission()
                } ?: checkCameraPermission()
            else checkCameraPermission()
        } ?: checkCameraPermission()
    }

    private fun p2ProcessError(): IDOnboardingP2ReprocessErrorEnum? {
        return when (userStatus.onboardingStatus?.userStatus?.p2ReprocessError?.code) {
            IDOnboardingP2ReprocessErrorEnum.CENTRALIZE_FACE_CAPTURE.code -> IDOnboardingP2ReprocessErrorEnum.CENTRALIZE_FACE_CAPTURE
            IDOnboardingP2ReprocessErrorEnum.CLOSE_CAMERA_FACE.code -> IDOnboardingP2ReprocessErrorEnum.CLOSE_CAMERA_FACE
            IDOnboardingP2ReprocessErrorEnum.CAMERA_FACE_AWAY.code -> IDOnboardingP2ReprocessErrorEnum.CAMERA_FACE_AWAY
            IDOnboardingP2ReprocessErrorEnum.NOT_SATISFACTORY_LIGHTING.code -> IDOnboardingP2ReprocessErrorEnum.NOT_SATISFACTORY_LIGHTING
            IDOnboardingP2ReprocessErrorEnum.PICTURE_OUTSIDE_FOCUS.code -> IDOnboardingP2ReprocessErrorEnum.PICTURE_OUTSIDE_FOCUS
            IDOnboardingP2ReprocessErrorEnum.TILTED_FACE.code -> IDOnboardingP2ReprocessErrorEnum.TILTED_FACE
            IDOnboardingP2ReprocessErrorEnum.FACE_SIDE.code -> IDOnboardingP2ReprocessErrorEnum.FACE_SIDE
            IDOnboardingP2ReprocessErrorEnum.REMOVE_GLASSES.code -> IDOnboardingP2ReprocessErrorEnum.REMOVE_GLASSES
            else -> null
        }
    }

    private fun isSelfieValidationError(): Boolean {
        return userStatus.onboardingStatus?.userStatus?.restartP2 == true &&
                userStatus.onboardingStatus?.userStatus?.p2ReprocessError != null
    }

    override fun showLoading(@StringRes loadingMessage: Int?, vararg messageArgs: String) {
        navigation?.showLoading(true, R.drawable.img_selfie, false)
    }

    override fun hideLoading(
        @StringRes successMessage: Int?,
        loadingSuccessCallback: (() -> Unit)?,
        vararg messageArgs: String
    ) {
        navigation?.showLoading(false, R.drawable.img_selfie, false, successMessage != null)
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

    override fun showErrorInvalidSelfie(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        val errorMessage = error?.message ?: INVALID_SELFIE
        analytics.logIDOnErrorSendSelfie(error?.code, errorMessage)
        analyticsGA.logIDSendSelfieExcepiton(error?.code ?: EMPTY)

        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.id_onboarding_invalid_selfie_error_title),
                    message = getString(R.string.id_onboarding_invalid_selfie_error_message),
                    bt1Title = getString(R.string.do_it_later),
                    bt1Callback = {
                        goToHome()
                        false
                    },
                    bt2Title = if (retryCallback != null)
                        getString(R.string.text_button_try_again)
                    else
                        null,
                    bt2Callback = {
                        analyticsGA.logIDSendSelfieInvalidClick()
                        false
                    }
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showError(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        val errorMessage = error?.message ?: ANALYTICS_ID_ERROR_UNICO
        analytics.logIDOnErrorSendSelfie(error?.code, errorMessage)
        doWhenResumed(
            action = {
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

    override fun successSendingSelfie() {
        analytics.logIDOnSuccessSendSelfie()
        doWhenResumed(
            action = {
                navigateToP2()
            },
            errorCallback = { readyToNavigate = true }
        )
    }

    private fun navigateToP2() {
        doWhenResumed(
            action = {
                findNavController().navigate(
                    IDOnboardingPicturesSelfieGuideFragmentDirections
                        .actionIdOnboardingPicturesSelfieGuideFragmentToIdOnboardingValidateP2PolicyFragment()
                )
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun goToHome() {
        activity?.moveToHome()
            ?: baseLogout()
    }

    private fun openSdkSelfie() {
        if (isSelfieValidationError())
            showSelfieValidationError()
        else
            checkCameraPermission()
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

    private fun showExplainPermissionBS() {
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

    private fun showCheckIdentify(){
        bottomSheetGenericFlui(
            image = R.drawable.img_selfie,
            title = getString(R.string.selfie_check_identify_title, firstWordCapitalize(userStatus.name, getString(R.string.hello))),
            subtitle = getString(R.string.selfie_check_identify_message, firstWordCapitalize(userStatus.name, getString(R.string.hello))),
            nameBtn1Bottom = getString(R.string.selfie_check_identify_button_negative),
            nameBtn2Bottom = getString(R.string.selfie_check_identify_button_positive),
            statusNameTopBar = false,
            statusBtnClose = false,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLACK,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = false,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnFirst(dialog: Dialog) {
                        dialog.dismiss()
                        showCheckIdentifyError()
                    }
                    override fun onBtnSecond(dialog: Dialog) {
                        openSdkSelfie()
                        dialog.dismiss()
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                    }

                    override fun onCancel() {
                        dismiss()
                    }
                }
        }.show(childFragmentManager, requireContext().getString(R.string.bottom_sheet_generic))
    }

    private fun showCheckIdentifyError(){
        bottomSheetGenericFlui(
            image = R.drawable.img_10_erro,
            title = getString(R.string.selfie_check_identify_title_error),
            subtitle = getString(R.string.selfie_check_identify_message_error, firstWordCapitalize(userStatus.name, getString(R.string.hello))),
            nameBtn2Bottom = getString(R.string.entendi),
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLACK,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK_CENTER,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
            isCancelable = false,
            isFullScreen = false,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                    override fun onBtnSecond(dialog: Dialog) {
                        dismiss()
                        activity?.finishAndRemoveTask()
                        System.exit(ZERO)
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                    }

                    override fun onCancel() {
                        dismiss()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    companion object {
        const val TAG = "Unico"
        const val RESULT = "result"
        const val ERROR = "error"
        const val PARAMETERS = "parameters"
    }
}
