package br.com.mobicare.cielo.chargeback.presentation.refuse

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_PENDING_REFUSE
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_PENDING_REFUSE_SUCESS
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.DEFAULT_EMPTY_VALUE
import br.com.mobicare.cielo.chargeback.utils.UiRefuseState
import br.com.mobicare.cielo.commons.constants.FORTY
import br.com.mobicare.cielo.commons.constants.Intent.IMAGE_TYPE
import br.com.mobicare.cielo.commons.constants.Intent.PDF_TYPE
import br.com.mobicare.cielo.commons.constants.Intent.SELECT_FILE_TAG
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.getNewErrorMessage
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentChargebackRefuseBinding
import br.com.mobicare.cielo.extensions.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChargebackRefuseFragment : BaseFragment(), CieloNavigationListener {

    private val ga4: ChargebackGA4 by inject()
    private val viewModel: ChargebackRefuseViewModel by viewModel()
    private val handlerValidationToken: HandlerValidationToken by inject()

    private var navigation: CieloNavigation? = null
    private val args: ChargebackRefuseFragmentArgs by navArgs()
    private var binding: FragmentChargebackRefuseBinding? = null

    private var file: String? = null
    private var refuseReason: String? = null
    private var chargeback: Chargeback? = null

    private var storageLauncher: ActivityResultLauncher<Intent>? = null

    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted)
            openStorage()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChargebackRefuseBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chargeback = args.chargeback
        setupView()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_VIEW_CHARGEBACK_PENDING_REFUSE)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupView() {
        setupNavigation()
        setCaseId()
        reasonListener()
        observeLoading()
        observeResult()
        checkAttachment()
        addAttachmentListener()
        changeAttachmentListener()
        removeAttachmentListener()
    }

    private fun observeResult() {
        viewModel.chargebackRefuseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is UiRefuseState.FileExtensionIsAccepted -> onFileExtensionIsAccepted()
                is UiRefuseState.FileExtensionIsNotAccepted -> onErrorFileExtensionIsNotAccepted()
                is UiRefuseState.Success -> onSuccess()
                is UiRefuseState.Error -> onError(it.error)
                is UiRefuseState.ErrorToken -> onErrorToken(it.error)
            }
        }
    }

    private fun observeLoading() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            onHideLoading()
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.showHelpButton()
            navigation?.showButton(isShow = true)
            navigation?.enableButton(isEnabled = false)
            navigation?.showContainerButton(isShow = true)
            navigation?.setTextButton(getString(R.string.chargeback_confirm_refusal))
            navigation?.setNavigationListener(this)
        }
    }

    private fun setCaseId() {
        binding?.includeAttachDocument?.tvIdCase?.text =
            chargeback?.caseId?.toString() ?: DEFAULT_EMPTY_VALUE
    }

    private fun reasonListener() {
        binding?.etRefuseReason?.addTextChangedListener {
            it?.let { reason ->
                refuseReason = reason.trim().toString()
                checkButton()
            }
        }
    }

    private fun addAttachmentListener() {
        binding?.includeAttachDocument?.containerAddAttachment?.setOnClickListener {
            selectAttachment()
        }
    }

    private fun changeAttachmentListener() {
        binding?.includeAttachDocument?.tvChangeAttachment?.setOnClickListener {
            selectAttachment()
        }
    }

    private fun removeAttachmentListener() {
        binding?.includeAttachDocument?.ivRemoveAttachment?.setOnClickListener {
            CieloDialog.create(
                title = getString(R.string.chargeback_refuse_delete_attachment),
            ).setTitleTextAlignment(View.TEXT_ALIGNMENT_CENTER)
                .setMessageColor(R.color.color_3C3C43)
                .setPrimaryButton(getString(R.string.text_no_label))
                .setSecondaryButton(getString(R.string.text_yes_label))
                .setOnSecondaryButtonClickListener {
                    clearFile()
                    checkButton()
                }.show(childFragmentManager, tag)
        }
    }

    private fun clearFile() {
        file = null
        setupAttachmentView(hasAttachment = false)
    }

    private fun selectAttachment() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openStorage()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> showPermissionRequest()
            else -> requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun openStorage() {
        storageLauncher?.launch(
            Intent.createChooser(
                selectFileIntent(arrayOf(IMAGE_TYPE, PDF_TYPE)),
                SELECT_FILE_TAG
            )
        )
    }

    private fun checkAttachment() {
        storageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    uri?.let { itUri ->
                        file = convertToBase64(itUri)
                        setAttachmentName(getMimeType(itUri))
                        setupAttachmentView(hasAttachment = true)
                    }
                    checkButton()
                }
            }
    }

    private fun showPermissionRequest() {
        CieloDialog.create(
            title = getString(R.string.storage_permission_request_title),
            message = getString(R.string.storage_permission_request_message)
        ).setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setMessageColor(R.color.color_3C3C43)
            .setPrimaryButton(getString(R.string.access_permission_button))
            .setSecondaryButton(getString(R.string.cancelar))
            .setOnPrimaryButtonClickListener {
                showStoragePermission()
            }.show(childFragmentManager, tag)
    }

    private fun checkButton() {
        val isEnableButton = refuseReason?.let {
            it.length > ONE && file.isNullOrEmpty().not()
        } ?: false
        navigation?.enableButton(isEnableButton)
    }

    private fun setupAttachmentView(hasAttachment: Boolean) {
        binding?.includeAttachDocument?.apply {
            if (hasAttachment.not())
                tvFileTypeError.gone()

            containerMyAttachment.visible(hasAttachment)
            containerAddAttachment.visible(hasAttachment.not())
            tvAttachmentError.visible(file.isNullOrEmpty())
        }
    }

    private fun onFileExtensionIsAccepted() {
        binding?.includeAttachDocument?.tvFileTypeError?.gone()
    }

    private fun onErrorFileExtensionIsNotAccepted() {
        binding?.includeAttachDocument?.tvFileTypeError?.apply {
            visible()
            text = getString(R.string.chargeback_refuse_file_type_error)
            navigation?.enableButton(isEnabled = false)
        }
    }

    private fun setAttachmentName(type: String) {
        binding?.includeAttachDocument?.tvMyAttachment?.text = "${chargeback?.caseId}$type"
        viewModel.checkFileType(type)
    }

    override fun onButtonClicked(labelButton: String) {
        getToken()
    }

    private fun getToken() {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) {
                    viewModel.chargebackRefuse(
                        context = context,
                        otpCode = token,
                        reasonToRefuse = refuseReason,
                        fileName = binding?.includeAttachDocument?.tvMyAttachment?.text?.toString(),
                        fileBase64 = file,
                        chargeback = chargeback
                    )
                }

                override fun onError() {
                    onErrorToken()
                }
            })
    }

    private fun onHideLoading() {
        handlerValidationToken.hideAnimation(
            isDelay = false,
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {})
    }

    private fun onSuccess() {
        handlerValidationToken.playAnimationSuccess(callbackAnimationSuccess =
        object : HandlerValidationToken.CallbackAnimationSuccess {
            override fun onSuccess() {
                navigation?.showCustomHandlerView(
                    contentImage = R.drawable.ic_129_secure_access_management,
                    title = getString(R.string.chargeback_refuse_success_title),
                    message = getString(R.string.chargeback_refuse_success_message),
                    labelSecondButton = getString(R.string.chargeback_refuse_success_label_btn),
                    isShowButtonClose = true,
                    messageAlignment = View.TEXT_ALIGNMENT_CENTER,
                    messageMargin = FORTY,
                    callbackClose = {
                        findNavController().navigate(
                            ChargebackRefuseFragmentDirections
                                .actionChargebackRefuseFragmentToChargebackInitFragment(false)
                        )
                    },
                    callbackSecondButton = {
                        findNavController().navigate(
                            ChargebackRefuseFragmentDirections
                                .actionChargebackRefuseFragmentToChargebackInitFragment(true)
                        )
                    }
                )
                ga4.logScreenView(SCREEN_VIEW_CHARGEBACK_PENDING_REFUSE_SUCESS)
            }
        })
    }

    private fun onErrorToken(error: NewErrorMessage? = null) {
        handlerValidationToken.playAnimationError(error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() {
                    getToken()
                }
            })
    }

    private fun onError(error: NewErrorMessage?) {
        handlerValidationToken.hideAnimation(callbackStopAnimation =
        object : HandlerValidationToken.CallbackStopAnimation {
            override fun onStop() {
                navigation?.showCustomHandlerView(
                    title = getString(R.string.commons_generic_error_title),
                    message = requireContext().getNewErrorMessage(
                        error,
                        R.string.commons_generic_error_message
                    ),
                    labelSecondButton = getString(R.string.entendi),
                    isShowButtonClose = true,
                    messageAlignment = View.TEXT_ALIGNMENT_CENTER
                )
            }
        })
        ga4.logException(
            SCREEN_VIEW_CHARGEBACK_PENDING_REFUSE,
            error
        )
    }
}