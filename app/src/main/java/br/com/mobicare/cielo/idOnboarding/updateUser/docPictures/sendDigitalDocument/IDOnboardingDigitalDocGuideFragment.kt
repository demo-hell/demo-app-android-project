package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDigitalDocument

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.bottomsheet.CieloPdfBottomSheet
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.Intent.KILOBYTE
import br.com.mobicare.cielo.commons.constants.Intent.PDF_TYPE
import br.com.mobicare.cielo.commons.constants.Intent.SELECT_FILE_TAG
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.getStoneAgeEnvironment
import br.com.mobicare.cielo.commons.helpers.getStoneAgeTheme
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingDigitalDocGuideBinding
import br.com.mobicare.cielo.extensions.convertToBase64
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.selectFileIntent
import br.com.mobicare.cielo.extensions.showApplicationConfiguration
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CNH
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CNH2022
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.DNI
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.RG
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.StoneAgeDocumentValidation.CIN_FRENTE
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.StoneAgeDocumentValidation.CIN_VERSO
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.StoneAgeDocumentValidation.CNH_FRENTE
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.StoneAgeDocumentValidation.CNH_VERSO
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.StoneAgeDocumentValidation.RGNOVO_FRENTE
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.StoneAgeDocumentValidation.RGNOVO_VERSO
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.StoneAgeDocumentValidation.RG_FRENTE
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.StoneAgeDocumentValidation.RG_VERSO
import br.com.stoneage.identify.enums.DocumentClassification
import br.com.stoneage.identify.exceptions.documentDetection.DocumentDetectionException
import br.com.stoneage.identify.models.DigitalDocumentInfoResult
import br.com.stoneage.identify.models.GetDigitalDocumentInfoDelegate
import br.com.stoneage.identify.sdk.STADigitalDocument
import br.com.stoneage.identify.sdk.STAUserSession
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingDigitalDocGuideFragment : BaseFragment(), CieloNavigationListener,
    IDOnboardingDigitalDocumentContract.View {

    private val presenter: IDOnboardingDigitalDocumentPresenter by inject {
        parametersOf(this)
    }
    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingDigitalDocGuideBinding? = null
    private var storageLauncher: ActivityResultLauncher<Intent>? = null
    private val documentType by lazy {
        IDOnboardingFlowHandler.userStatus.documentType?.get(ZERO)?.uppercase()
    }
    private var documentBase64: String? = null
    private var documentUri: Uri? = null
    private var stoneAgeToken: String? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openStorageOlderVersions()
            } else {
                showExplainPermission()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentIdOnboardingDigitalDocGuideBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setupStorageResultListener()
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
        binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        binding?.apply {

            tvFile.fromHtml(R.string.id_onboarding_digital_doc_guide_file)
            tvFolder.fromHtml(R.string.id_onboarding_digital_doc_guide_folder)
            tvDoc.fromHtml(R.string.id_onboarding_digital_doc_guide_document)

            btBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }

            attachDocument.containerAddAttachment.setOnClickListener {
                presenter.getStoneAgeToken()
            }

            attachDocument.tvChangeAttachment.setOnClickListener {
                checkDeviceVersion()

            }

            attachDocument.ivRemoveAttachment.setOnClickListener {
                createDialogDelete()
            }

            attachDocument.tvMyAttachment.setOnClickListener {
                showPdfBottomSheet()
            }

            btNext.isEnabled = false
            btNext.setOnClickListener {
                presenter.uploadDigitalDocument(digitalDocumentBase64 = documentBase64)
            }
        }
    }

    private fun showPdfBottomSheet() {
        CieloPdfBottomSheet
            .create(
                title = requireContext().getString(R.string.pdf_bottom_sheet_title),
                documentBase64.orEmpty()
            ).show(childFragmentManager, tag)
    }

    private fun createDialogDelete() {
        CieloDialog.create(
            title = requireContext().getString(R.string.id_onboarding_digital_doc_guide_attach_delete_title),
            message = ONE_SPACE
        )
            .setTitleTextAlignment(View.TEXT_ALIGNMENT_CENTER)
            .setMessageColor(R.color.cloud_500)
            .setPrimaryButton(requireContext().getString(R.string.text_no_label))
            .setSecondaryButton(requireContext().getString(R.string.text_yes_label))
            .setOnSecondaryButtonClickListener {
                clearFile()
                checkButton()
            }.show(childFragmentManager, tag)
    }

    private fun checkButton() {
        binding?.btNext?.isEnabled = documentBase64.isNullOrEmpty().not()
    }

    private fun checkDeviceVersion() {
        if (android.os.Build.VERSION.SDK_INT > 32) {
            openStorageNewerVersions()
        } else {
            checkStoragePermission()
        }
    }

    private fun openStorageNewerVersions() {
        storageLauncher?.launch(
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
            }
        )
    }

    private fun checkStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    == PackageManager.PERMISSION_GRANTED -> {
                openStorageOlderVersions()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showExplainPermission()
            }

            else -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun openStorageOlderVersions() {
        storageLauncher?.launch(
            Intent.createChooser(
                selectFileIntent(arrayOf(PDF_TYPE)),
                SELECT_FILE_TAG
            )
        )
    }

    private fun clearFile() {
        documentBase64 = null
        setupAttachmentView(hasAttachment = false)
    }

    private fun setupAttachmentView(hasAttachment: Boolean) {
        binding?.attachDocument?.apply {
            if (hasAttachment) validatePDF()

            containerMyAttachment.visible(hasAttachment)
            containerAddAttachment.visible(hasAttachment.not())
        }
    }

    private fun validatePDF() {
        binding?.attachDocument?.tvFileTypeError.gone()
        if (isPDFSizeValid(documentUri)) {
            stoneAgePdfCheck()
        } else {
            attachError(requireContext().getString(R.string.id_onboarding_digital_doc_guide_attach_size_error))
        }
    }

    private fun showExplainPermission() {
        CieloDialog.create(
            title = getString(R.string.storage_permission_request_title),
            message = getString(R.string.storage_permission_request_message)
        ).setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setMessageColor(R.color.color_3C3C43)
            .setPrimaryButton(getString(R.string.access_permission_button))
            .setSecondaryButton(getString(R.string.cancelar))
            .setOnPrimaryButtonClickListener {
                showApplicationConfiguration()
            }.show(childFragmentManager, tag)
    }

    private fun setupStorageResultListener() {
        storageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    setupPdfFile(result.data?.data)
                }
            }
    }

    private fun setupPdfFile(data: Uri?) {
        data?.let { itUri ->
            clearFile()
            documentBase64 = convertToBase64(itUri)
            documentUri = itUri
            extractPdfFileName(itUri.toString())
        }
        checkButton()
    }

    private fun stoneAgePdfCheck() {
        showLoading()
        STAUserSession.initialize(
            stage = getStoneAgeEnvironment(),
            sdkToken = stoneAgeToken.orEmpty(),
            owner = ContextWrapper(requireContext()),
            theme = getStoneAgeTheme(requireContext())
        )
        STADigitalDocument.getDigitalDocumentInfo(
            documentBase64,
            object : GetDigitalDocumentInfoDelegate {
                override fun getDigitalDocumentInfoCallback(digitalDocumentInfoResult: DigitalDocumentInfoResult) {
                    if (validateDocumentSelected(digitalDocumentInfoResult.documentInfos?.get(0)?.classification)) {
                        hideLoading()
                        checkButton()
                    } else {
                        hideLoading()
                        showFailedStoneAgeValidation()
                    }
                }

                override fun getDigitalDocumentInfoCallbackError(documentDetectionException: DocumentDetectionException) {
                    hideLoading()
                    showFailedStoneAgeValidation()
                }

            })
    }

    private fun validateDocumentSelected(classification: DocumentClassification?): Boolean {
        when (documentType) {
            CNH, CNH2022 -> {
                return when (classification?.name) {
                    CNH, CNH_FRENTE, CNH_VERSO -> true
                    else -> false
                }
            }

            RG, DNI -> {
                return when (classification?.name) {
                    RG, RG_FRENTE, RG_VERSO, RGNOVO_FRENTE, RGNOVO_VERSO, CIN_FRENTE, CIN_VERSO -> true
                    else -> false
                }
            }

            else -> return false
        }
    }

    private fun showFailedStoneAgeValidation() {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_20_erro_documento,
            title = requireContext().getString(R.string.id_onboarding_digital_doc_guide_error_bs_title),
            message = requireContext().getString(R.string.id_onboarding_digital_doc_guide_error_bs_text),
            bt2Title = requireContext().getString(R.string.text_try_again_label),
            bt2Callback = {
                clearFile()
                checkButton()
                false
            },
            closeCallback = {
                clearFile()
                checkButton()
            }
        ) ?: baseLogout()
    }

    private fun isPDFSizeValid(uri: Uri?): Boolean {
        val maxFileSize = FOUR * KILOBYTE * KILOBYTE
        val fileDescriptor =
            uri?.let { activity?.contentResolver?.openAssetFileDescriptor(it, "r") }
        val fileSize = fileDescriptor?.length

        return fileSize != null && fileSize <= maxFileSize
    }

    private fun setAttachmentName(type: String) {
        binding?.attachDocument?.tvMyAttachment?.text = type
    }

    private fun extractPdfFileName(uri: String) {
        val path = uri.replaceFirst(".*/(.*?)$".toRegex(), "$1")
        setAttachmentName(path)
        setupAttachmentView(hasAttachment = true)
    }

    private fun attachError(message: String? = null) {
        binding?.attachDocument?.tvFileTypeError?.apply {
            visible()
            text = if (message.isNullOrEmpty())
                requireContext().getText(R.string.id_onboarding_digital_doc_guide_attach_size_error)
            else
                message
        }
    }

    private fun navigateToSelfie() {
        findNavController().navigate(R.id.action_to_idOnboardingPicturesSelfieGuideFragment)
    }

    override fun successStoneAgeToken(token: String) {
        stoneAgeToken = token
        checkDeviceVersion()
    }

    override fun errorStoneAgeToken(error: ErrorMessage?) {
        showFailedStoneAgeValidation()
    }

    override fun successSendingDigitalDocument() {
        navigateToSelfie()
    }

    override fun errorSendingDigitalDocument(error: ErrorMessage?) {
        navigation?.showError(error)
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showLoading(false)
    }
}