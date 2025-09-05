package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDocument

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.INVALID_SELFIE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.getStoneAgeEnvironment
import br.com.mobicare.cielo.commons.helpers.getStoneAgeTheme
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
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingPicturesDocGuideBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.showApplicationConfiguration
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CNH
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CNH2022
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.DNI
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.RG
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CRNM
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.RNE
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ERROR_BR_SCAN
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_TIPS_FOR_A_GOOD_PHOTO
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_TIPS_DOCUMENT_PICTURES
import br.com.stoneage.identify.enums.CameraCaptureModeEnum
import br.com.stoneage.identify.enums.DocumentType
import br.com.stoneage.identify.enums.DocumentValidationError
import br.com.stoneage.identify.models.DocumentActivityErrorResult
import br.com.stoneage.identify.models.DocumentActivityResult
import br.com.stoneage.identify.sdk.DocumentBackActivity
import br.com.stoneage.identify.sdk.DocumentFrontActivity
import br.com.stoneage.identify.sdk.STACameraConfig
import br.com.stoneage.identify.sdk.STAUserSession
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingPicturesDocGuideFragment : BaseFragment(), CieloNavigationListener,
    IDOnboardingUploadDocumentContract.View {

    private val presenter: IDOnboardingUploadDocumentPresenter by inject {
        parametersOf(this)
    }

    private val analytics: IDOnboardingP2Analytics by inject()
    private val analyticsGA: IDOnboardingP2AnalyticsGA by inject()
    private var navigation: CieloNavigation? = null
    private val isCNH by lazy { userStatus.documentType?.get(ZERO)?.uppercase() == CNH }
    private val documentType by lazy { userStatus.documentType?.get(ZERO)?.uppercase() }
    private var captureResultLauncher: ActivityResultLauncher<Intent>? = null
    private var frontDocument: String? = null
    private var backDocument: String? = null
    private var isFrontDocument: Boolean = true
    private var _binding: FragmentIdOnboardingPicturesDocGuideBinding? = null
    private val binding get() = _binding
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                showExplainPermissionBS()
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentIdOnboardingPicturesDocGuideBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupView()
        setupListeners()
        analytics.logIDScreenViewDocumentType(ANALYTICS_ID_SCREEN_VIEW_TIPS_FOR_A_GOOD_PHOTO,
            userStatus.documentType?.get(ZERO)?.uppercase(), this.javaClass)
        analyticsGA.logIDSendPicturesDocGuideScreenView(userStatus.documentType?.get(ZERO)?.uppercase())
    }

    override fun onPauseActivity() {
        super.onPauseActivity()
        presenter.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setupDocumentCallback()
    }

    private fun setupDocumentCallback() {
        captureResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val data = result.data
                    data?.let { d ->
                        d.getParcelableExtra<DocumentActivityResult>(RESULT)?.let {
                            doWhenResumed(
                                action = {
                                    if (it.imageFile?.exists() == true){
                                        if (verifyDocumentType(it.documentType)) {
                                            processCaptureSuccess(it.imageFile?.convertBase64())
                                        } else {
                                            differentDocumentDetected()
                                        }
                                    }
                                },
                                errorCallback = {
                                    frontDocument = null
                                    backDocument = null
                                    genericError()
                                }
                            )
                        }
                    } ?:genericError()
                }
                else -> {
                    val data = result.data
                    data?.let { d ->
                        d.getParcelableExtra<DocumentActivityErrorResult>(ERROR)?.let {
                            doWhenResumed(
                                action = {
                                    if (it.validationErrors != null && it.validationErrors.isNullOrEmpty().not()){
                                        processCaptureError(it.validationErrors?.first())
                                    } else {
                                        genericError()
                                    }
                                },
                                errorCallback = {
                                    frontDocument = null
                                    backDocument = null
                                    genericError()
                                }
                            )
                        }
                    } ?:genericError()
                }
            }
        }
    }

    private fun verifyDocumentType(documentType: DocumentType?): Boolean {
        return when(userStatus.documentType?.get(ZERO)?.uppercase()){
            RG, DNI -> {
                documentType?.toString() == RG
            }
            CNH, CNH2022 -> {
                documentType?.toString() == CNH
            }
            CRNM -> {
                documentType?.toString() == RNE
            }
            else -> {
                false
            }
        }
    }

    private fun differentDocumentDetected() {
        doWhenResumed {
            resetDocumentCapture()

            bottomSheetGenericFlui(
                image = R.drawable.img_20_erro_documento,
                title = requireContext().getString(R.string.capture_document_not_detected_title),
                subtitle = requireContext().getString(R.string.capture_document_different_documents_detected_message),
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
    }

    private fun processCaptureSuccess(image: String?) {
        if (isFrontDocument) {
            isFrontDocument = false
            frontDocument = image
            val intent = Intent(requireActivity(), DocumentBackActivity::class.java)
            captureResultLauncher?.launch(intent) ?: genericError()
        } else {
            isFrontDocument = true
            backDocument = image
            presenter.uploadDocument(frontDocument, backDocument)
        }
    }

    private fun processCaptureError(error: DocumentValidationError?) {
        resetDocumentCapture()
        var title = requireContext().getString(R.string.id_onboarding_title_bs_error_p2_login)
        var message = requireContext().getString(R.string.capture_document_unknown_error_message)
        var image = R.drawable.ic_generic_error_image

        when(error) {
            DocumentValidationError.IMAGE_NOT_GOOD_ENOUGH -> {
                title = requireContext().getString(R.string.capture_document_no_face_detected_title)
                message = requireContext().getString(R.string.capture_document_no_face_detected_message)
                image = R.drawable.img_56_rg_plastico
            }
            DocumentValidationError.DOCUMENT_NOT_DETECTED -> {
                title = requireContext().getString(R.string.capture_document_not_detected_title)
                message = requireContext().getString(R.string.capture_document_not_detected_message)
                image = R.drawable.img_20_erro_documento
            }
            DocumentValidationError.NO_FACE_DETECTED -> {
                title = requireContext().getString(R.string.capture_document_no_face_detected_title)
                message = requireContext().getString(R.string.capture_document_no_face_detected_message)
                image = R.drawable.img_130_posicao_selfie
            }
            DocumentValidationError.TOO_MANY_FACES -> {
                title = requireContext().getString(R.string.capture_document_too_many_faces_title)
                message = requireContext().getString(R.string.capture_document_too_many_faces_message)
                image = R.drawable.img_130_posicao_selfie
            }
            DocumentValidationError.DOCUMENT_FRONT_DETECTED,
            DocumentValidationError.DOCUMENT_BACK_DETECTED -> {
                title = requireContext().getString(R.string.capture_document_wrong_side_detected_title)
                message = requireContext().getString(R.string.capture_document_wrong_side_detected_message)
                image = R.drawable.img_20_erro_documento
            }
            DocumentValidationError.NO_DOCUMENT_SIDE_DETECTED -> {
                title = requireContext().getString(R.string.capture_document_no_document_side_detected_title)
                message = requireContext().getString(R.string.capture_document_no_document_side_detected_message)
                image = R.drawable.ic_generic_error_image
            }
            DocumentValidationError.MULTIPLE_DOCUMENTS -> {
                title = requireContext().getString(R.string.capture_document_multiple_documents_detected_title)
                message = requireContext().getString(R.string.capture_document_multiple_documents_detected_message)
                image = R.drawable.img_20_erro_documento
            }
            DocumentValidationError.CROPPED_DOCUMENT -> {
                title = requireContext().getString(R.string.capture_document_cropped_document_title)
                message = requireContext().getString(R.string.capture_document_cropped_document_message)
                image = R.drawable.img_take_document_picture
            }
            DocumentValidationError.OPENED_DOCUMENT -> {
                title = requireContext().getString(R.string.capture_document_opened_document_title)
                message = requireContext().getString(R.string.capture_document_opened_document_message)
                image = R.drawable.img_take_document_picture
            }
            else -> {
                title = requireContext().getString(R.string.id_onboarding_title_bs_error_p2_login)
                message = requireContext().getString(R.string.capture_document_unknown_error_message)
                image = R.drawable.ic_generic_error_image
            }
        }

        bottomSheetGenericFlui(
            image = image,
            title = title,
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
                        analyticsGA.logIDSendPicturesDocGuideCaptureErrorSignUp(userStatus.documentType?.get(ZERO)?.uppercase())
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                    }

                    override fun onCancel() {
                        dismiss()
                    }
                }
        }.show(childFragmentManager, requireContext().getString(R.string.capture_document_error_bottom_sheet))
        analyticsGA.logIDSendPicturesDocGuideCaptureErrorDisplay(userStatus.documentType?.get(ZERO)?.uppercase(), title)
    }

    private fun resetDocumentCapture() {
        isFrontDocument = true
        frontDocument = null
        backDocument = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        binding?.apply {
            analyticsGA.logIDSendPicturesDocGuideDisplay(userStatus.documentType?.get(ZERO)?.uppercase())
            tvTitle.text = requireContext().getString(R.string.id_onboarding_pictures_doc_guide_title)

            llDocCase.visible(documentType != CRNM)

            ivDocCase.setImageResource(
                if (isCNH) R.drawable.ic_cnh_case
                else R.drawable.ic_rg_card_case
            )
            tvDocCase.fromHtml(
                if (isCNH) R.string.id_onboarding_pictures_doc_guide_cnh_case
                else R.string.id_onboarding_pictures_doc_guide_rg_case
            )

            ivDocBg.setImageResource(
                when(documentType) {
                    RG, DNI -> R.drawable.ic_rg_profile_card
                    CNH, CNH2022 -> R.drawable.ic_cnh
                    CRNM -> R.drawable.ic_cnh
                    else -> R.drawable.ic_rg_profile_card
                }
            )

            tvDocBg.fromHtml(R.string.id_onboarding_pictures_doc_guide_bg)

            tvDocSun.fromHtml(R.string.id_onboarding_pictures_doc_guide_rg_sun)

            tvDocPosition.fromHtml(
                when(documentType) {
                    RG, DNI -> R.string.id_onboarding_pictures_doc_guide_rg_position
                    CNH, CNH2022 -> R.string.id_onboarding_pictures_doc_guide_cnh_position
                    CRNM -> R.string.id_onboarding_pictures_doc_guide_rne_position
                    else -> R.string.id_onboarding_pictures_doc_guide_rne_position
                }
            )
        }
    }

    private fun setupListeners() {
        binding?.apply {
            btBackArrow.setOnClickListener {
                analytics.logIDOnClickComeBackWithDocument(
                    ANALYTICS_ID_TIPS_DOCUMENT_PICTURES,
                    userStatus.documentType?.get(ZERO)?.uppercase()
                )
                navigation?.getNavController()?.popBackStack()
            }

            btNext.setOnClickListener {
                analytics.logIDOnClickNextWithDocument(
                    ANALYTICS_ID_TIPS_DOCUMENT_PICTURES,
                    userStatus.documentType?.get(ZERO)?.uppercase()
                )
                analyticsGA.logIDSendPicturesDocGuideSignUp(userStatus.documentType?.get(ZERO)?.uppercase())
                openCameraBottomSheet()
            }
        }
    }

    private fun openCamera() {
        doWhenResumed {
            presenter.getStoneAgeToken()
        }
    }

    override fun successStoneAgeToken(token: String) {
        doWhenResumed {
            analytics.logIDOnSuccessSendDocument(
                userStatus.documentType?.get(ZERO)?.uppercase()
            )

            STAUserSession.initialize(
                stage = getStoneAgeEnvironment(),
                sdkToken = token,
                owner = ContextWrapper(requireContext()),
                theme = getStoneAgeTheme(requireContext())
            )
            val intent = Intent(requireActivity(), DocumentFrontActivity::class.java)
            hideLoading()
            captureResultLauncher?.launch(intent) ?: genericError()
        }
    }

    override fun errorStoneAgeToken(error: ErrorMessage?) {
        doWhenResumed {
            val errorMessage = error?.message ?: INVALID_SELFIE
            analytics.logIDOnErrorSendDocument(
                userStatus.documentType?.get(ZERO)?.uppercase(),
                error?.code,
                errorMessage
            )
            processCaptureError(DocumentValidationError.UNKNOWN_ERROR)
        }
    }

    private fun genericError() {
        doWhenResumed {
            processCaptureError(DocumentValidationError.UNKNOWN_ERROR)
        }
    }

    private fun openCameraBottomSheet() {
        val title = requireContext().getString(
            when(documentType) {
                RG, DNI -> R.string.id_onboarding_pictures_attention_front_picture_rg
                CNH, CNH2022 -> R.string.id_onboarding_pictures_attention_front_picture_cnh
                CRNM -> R.string.id_onboarding_pictures_attention_front_picture_crnm
                else -> R.string.id_onboarding_pictures_attention_front_picture_crnm
            }
        )

        val message = requireContext().getString(
            when(documentType) {
                RG, DNI -> R.string.id_onboarding_pictures_attention_for_tips_rg
                CNH, CNH2022 -> R.string.id_onboarding_pictures_attention_for_tips_cnh
                CRNM -> R.string.id_onboarding_pictures_attention_for_tips_crnm
                else -> R.string.id_onboarding_pictures_attention_for_tips_crnm
            }
        )
        val image =
            when (documentType) {
                CNH, CNH2022 -> R.drawable.img_cnh_front_cell
                RG,DNI -> R.drawable.img_dni_front_cell
                CRNM -> R.drawable.img_cnh_front_cell
                else -> R.drawable.img_rg_front_cell
            }

        bottomSheetGenericFlui(
            image = image,
            title = title,
            subtitle = message,
            nameBtn2Bottom = requireContext().getString(R.string.id_onboarding_pictures_send_front_picture),
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
                        checkCameraPermission()
                        analyticsGA.logIDSendPicturesDocGuideSignUp(userStatus.documentType?.get(ZERO)?.uppercase())
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                    }

                    override fun onCancel() {
                        dismiss()
                    }
                }
        }.show(childFragmentManager, requireContext().getString(R.string.bottom_sheet_generic))

        analyticsGA.logIDSendPicturesDocGuideBSDisplay(userStatus.documentType?.get(ZERO)?.uppercase(),title)
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                showExplainPermissionBS()
            }

            else -> requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
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

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showLoading(false)
    }

    override fun showLoading(@StringRes loadingMessage: Int?, vararg messageArgs: String) {
        val image = when (userStatus.documentType?.get(ZERO)?.uppercase()) {
            CNH, CNH2022 -> R.drawable.img_cnh_front_cell
            RG,DNI -> R.drawable.img_dni_front_cell
            CRNM -> R.drawable.img_cnh_front_cell
            else -> R.drawable.img_rg_front_cell
        }
        navigation?.showLoading(true, image, true)
    }

    override fun hideLoading(
        @StringRes successMessage: Int?,
        loadingSuccessCallback: (() -> Unit)?,
        vararg messageArgs: String
    ) {
        navigation?.showLoading(false, null, true, isSuccess = successMessage != null)
    }

    override fun showError(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        resetDocumentCapture()
        val errorMessage = error?.message ?: ANALYTICS_ID_ERROR_BR_SCAN
        analytics.logIDOnErrorSendDocument(
            userStatus.documentType?.get(ZERO)?.uppercase(),
            error?.code,
            errorMessage
        )
        analyticsGA.logIDSendPicturesDocGuideExcepiton(
            userStatus.documentType?.get(ZERO)?.uppercase(),
            error?.code ?: EMPTY
        )

        navigation?.showCustomBottomSheet(
            image = R.drawable.ic_generic_error_image,
            title = R.string.generic_error_title,
            message = error?.message,
            bt1Title = if (retryCallback != null)
                R.string.text_try_again_label
            else
                null,
            bt1Callback = {
                retryCallback?.invoke()
                false
            },
            bt2Title = R.string.entendi,
            bt2Callback = {
                false
            },
        )
    }

    override fun successSendingDocument() {
        analytics.logIDOnSuccessSendDocument(
            userStatus.documentType?.get(ZERO)?.uppercase()
        )
        resetDocumentCapture()
        navigateToSelfie()
    }

    private fun navigateToSelfie() {
        findNavController().navigate(R.id.action_to_idOnboardingPicturesSelfieGuideFragment)
    }

    companion object {
        const val RESULT = "result"
        const val ERROR = "error"
    }
}