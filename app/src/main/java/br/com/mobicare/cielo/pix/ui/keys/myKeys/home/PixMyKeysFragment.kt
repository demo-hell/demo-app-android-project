package br.com.mobicare.cielo.pix.ui.keys.myKeys.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.Key
import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.pix.model.ListKeyType
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.adapter.PixMyKeysAdapter
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.PixKeyDetailsBottomSheet
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.PixMyKeysDetails
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.claims.received.PixKeyReceivedClaimBottomSheet
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.claims.received.PixKeyReceivedClaimContract
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.claims.sent.PixKeySentClaimDetailsBottomSheet
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.claims.sent.PixKeySentClaimDetailsContract
import br.com.mobicare.cielo.pix.ui.transfer.type.PixSelectorBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.type.PixSelectorContract
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import kotlinx.android.synthetic.main.fragment_pix_my_keys.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

private const val MAX_NUMBER_KEYS = 20

class PixMyKeysFragment : BaseFragment(), CieloNavigationListener, PixMyKeysContract.View {

    private val presenter: PixMyKeysPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private var navigation: CieloNavigation? = null
    private var isFinish = true
    private var isShowCNPJ = true
    private var keyToDelete = ""
    private var masterKey: MyKey? = null
    private var currentKey: MyKey? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            it.getParcelable<MyKey>(PIX_MASTER_KEY_ARGS)?.apply {
                masterKey = this
            }

            it.getBoolean(PIX_REDIRECT_RANDOM_KEY).apply {
                if (this) toRegisterKey(PixKeyTypeEnum.EVP)
            }

            it.getBoolean(PIX_REDIRECT_MAIN_KEY_DETAILS).apply {
                if (this) toMainKeyDetails(masterKey)
            }
        }
    }

    private fun toMainKeyDetails(mainKey: MyKey?) {
        mainKey?.let {
            onShowKeyDetails(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_pix_my_keys, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        setupNavigation()
        onGetMyKeys()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_my_keys_pix))
            navigation?.showContainerButton()
            navigation?.showButton()
            navigation?.showHelpButton(isShow = true)
            navigation?.setNavigationListener(this)
        }
    }

    override fun onGetMyKeys() {
        presenter.getMyKeys()
    }

    private fun toRegisterKey(keyType: PixKeyTypeEnum) {
        findNavController().navigate(
            PixMyKeysFragmentDirections.actionPixMyKeysFragmentToPixKeyRegistrationFragment(
                keyType,
                EMPTY,
                EMPTY
            )
        )
    }

    private fun toInsertKey(keyType: PixKeyTypeEnum) {
        findNavController().navigate(
            PixMyKeysFragmentDirections.actionPixMyKeysFragmentToPixInsertKeyToRegisterFragment(
                keyType
            )
        )
    }

    private fun registerNewKey() {
        PixSelectorBottomSheet.onCreate(
            object : PixSelectorContract.Result {
                override fun onShowKeyTypeSelected(keyType: PixKeyTypeEnum) {
                    when (keyType) {
                        PixKeyTypeEnum.PHONE -> toInsertKey(keyType)
                        PixKeyTypeEnum.EMAIL -> toInsertKey(keyType)
                        PixKeyTypeEnum.EVP -> toRegisterKey(keyType)
                        else -> toRegisterKey(keyType)
                    }
                }
            },
            ListKeyType(keyTypes(requireContext(), isEVP = true, isCNPJ = isShowCNPJ)),
            getString(R.string.text_title_which_key_do_you_want_to_register)
        ).show(childFragmentManager, tag)
    }

    private fun setupListener() {
        btn_more_information_keys_pix?.setOnClickListener {
            onHelpButtonClicked()
        }

        container_add_key?.setOnClickListener {
            registerNewKey()
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onShowMyKeys(keys: Key?, isShowCNPJ: Boolean) {
        this.isShowCNPJ = isShowCNPJ
        container_with_keys?.visible()
        container_without_main_key?.gone()

        recyclerview_my_keys_pix?.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        recyclerview_my_keys_pix?.adapter =
            keys?.keys?.let {
                PixMyKeysAdapter(it, this, requireContext())
            }

        keys?.count?.let {
            tv_key_quantity?.text = getString(R.string.text_my_keys_quantity, it)
            if (it >= MAX_NUMBER_KEYS) container_add_key?.gone()
        }
    }

    override fun onShowVerificationKeys(keys: List<MyKey>) {
        container_verification_keys?.visible()
        container_without_main_key?.gone()

        recyclerview_verification_keys?.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        recyclerview_verification_keys?.adapter =
            PixMyKeysAdapter(keys, this, requireContext(), true)
    }

    override fun onHideVerificationKeys() {
        container_verification_keys?.gone()
    }

    override fun onHideMyKeys() {
        container_with_keys?.gone()
    }

    override fun onNoKeyRegistered() {
        onHideMyKeys()
        onHideVerificationKeys()
        container_without_main_key?.visible()
    }

    override fun showError(error: ErrorMessage?) {
        isFinish = true
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.back),
            error = error
        )
    }

    override fun onShowKeyDetails(key: MyKey) {
        PixKeyDetailsBottomSheet.onCreate(key, object : PixMyKeysDetails.View {
            override fun copyKey(keyValue: String) {
                Utils.copyToClipboard(requireContext(), keyValue, showMessage = false)
                Toast(requireContext()).showCustomToast(
                    message = getString(R.string.key_copied_text),
                    activity = requireActivity(),
                    trailingIcon = R.drawable.ic_check_toast
                )
            }

            override fun shareKey(keyValue: String) {
                shareText(getString(R.string.this_is_my_pix_key, keyValue), requireContext())
            }

            override fun deleteMainKey(keyValue: String) {
                showDeleteConfirmationDialog(
                    keyValue,
                    R.string.bs_details_delete_main_key_confirm_deletion_title,
                    R.string.bs_details_delete_main_key_confirm_deletion_subtitle
                )
            }

            override fun deleteNormalKey(keyValue: String) {
                showDeleteConfirmationDialog(
                    keyValue,
                    R.string.bs_details_delete_key_confirm_deletion_title,
                    R.string.bs_details_delete_key_confirm_deletion_subtitle
                )
            }

            override fun showWhatIsMainKeyFAQ() {
                onHelpButtonClicked()
            }
        }).show(childFragmentManager, tag)
    }

    override fun onShowReceiveClaimKeysDetails(key: MyKey) {
        PixKeyReceivedClaimBottomSheet.onCreate(key, object : PixKeyReceivedClaimContract.View {
            override fun keepKey(key: MyKey) {
                currentKey = key

                validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                    presenter.cancelClaim(
                        otpCode,
                        currentKey,
                        isPortabilityOrClaimKey = true,
                        isClaimer = false
                    )
                }
            }

            override fun releaseKey(key: MyKey) {
                currentKey = key

                validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                    presenter.confirmClaim(otpCode, currentKey)
                }
            }

            override fun ownershipValidation(
                key: MyKey,
                keyType: PixKeyTypeEnum
            ) {
                findNavController().navigate(
                    PixMyKeysFragmentDirections.actionPixMyKeysFragmentToPixValidationCodeFragment(
                        keyType,
                        key.key ?: EMPTY,
                        true,
                        key.claimDetail?.claimId ?: EMPTY
                    )
                )
            }
        }).show(childFragmentManager, tag)
    }

    override fun onShowSuccessToKeepKey() {
        onShowBottomSheetSuccess(
            title = R.string.bs_portability_text_key_kept,
            subtitle = R.string.text_to_keep_key_confirm_message
        )
    }

    override fun onShowSuccessToReleaseKey() {
        onShowBottomSheetSuccess(
            title = R.string.bs_portability_text_key_released,
            subtitle = R.string.text_to_release_key_confirm_message
        )
    }

    private fun onShowBottomSheetSuccess(@StringRes title: Int, @StringRes subtitle: Int) {
        val key = this.currentKey?.key
        val keyType = this.currentKey?.keyType?.let {
            getKeyTypeName(it)
        }

        val formattedKey = "$keyType $key"

        bottomSheetGenericFlui(
            nameTopBar = "",
            R.drawable.ic_validado_transfer,
            getString(title),
            getString(subtitle, formattedKey),
            nameBtn1Bottom = getString(R.string.text_pix_success_create_claims_action),
            nameBtn2Bottom = getString(R.string.back),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = true,
            statusBtnSecond = false,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnFirst(dialog: Dialog) {
                        dialog.dismiss()
                        onGetMyKeys()
                    }

                    override fun onSwipeClosed() {
                        onGetMyKeys()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun onShowClaimKeysDetails(key: MyKey) {
        PixKeySentClaimDetailsBottomSheet.onCreate(key, object : PixKeySentClaimDetailsContract {
            override fun onCancel(key: MyKey?) {
                val title: String
                val message: String
                if (key?.claimType == PixClaimTypeEnum.OWNERSHIP.name) {
                    title = getString(R.string.text_pix_claims_cancel_ownership_title)
                    message = getString(R.string.text_pix_claims_cancel_ownership_message)
                } else {
                    title = getString(R.string.text_pix_claims_cancel_portability_title)
                    message = getString(R.string.text_pix_claims_cancel_portability_message)
                }
                CieloAskQuestionDialogFragment
                    .Builder()
                    .title(title)
                    .message(message)
                    .cancelTextButton(getString(R.string.text_pix_transfer_cancel))
                    .positiveTextButton(getString(R.string.back))
                    .setCancelButtonBackgroundResource(ResourcesCompat.ID_NULL)
                    .build().let {
                        it.onCancelButtonClickListener = View.OnClickListener {
                            currentKey = key

                            validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                                presenter.cancelClaim(otpCode, currentKey)
                            }
                        }
                        it.show(childFragmentManager, PixMyKeysFragment::class.java.simpleName)
                    }
            }
        }).show(childFragmentManager, tag)
    }

    private fun showDeleteConfirmationDialog(
        keyValue: String,
        @StringRes title: Int,
        @StringRes subtitle: Int
    ) {
        CieloDialog.create(
            title = getString(title),
            message = getString(subtitle)
        ).setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setPrimaryButton(getString(R.string.bs_details_delete_key_btn_yes))
            .setPrimaryButtonColor(R.color.danger_400)
            .setOnPrimaryButtonClickListener {
                keyToDelete = keyValue

                validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                    presenter.deleteKey(otpCode, keyToDelete, true)
                }
            }.show(childFragmentManager, PixMyKeysFragment::class.java.simpleName)
    }

    override fun onShowAllErrors(onFirstAction: () -> Unit) {
        isFinish = false
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                onFirstAction.invoke()
            }
        })
    }

    override fun onErrorCreateClaimOwnership(error: ErrorMessage?) {
        showErrorMessage(
            error = error,
            title = getString(R.string.text_pix_error_create_claims_ownership_title_alternative)
        )
    }

    override fun onErrorCreateClaimPortability(error: ErrorMessage?) {
        showErrorMessage(
            error = error,
            title = getString(R.string.text_pix_error_create_claims_portability_title_alternative)
        )
    }

    private fun showErrorMessage(
        error: ErrorMessage?,
        title: String = getString(R.string.text_pix_key_registration_error_title),
        message: String = getString(R.string.text_pix_error_in_processing)
    ) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        ) {
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.back),
                error = processErrorMessage(
                    error,
                    getString(R.string.business_error),
                    message
                ),
                title = title,
                isFullScreen = false
            )
        }
    }


    override fun onErrorDefault(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.back),
            error = error,
            isFullScreen = false
        )
    }

    override fun onErrorConfirmClaim(error: ErrorMessage?) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(OTP)) {
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.back),
                error = processErrorMessage(
                    error,
                    getString(R.string.business_error),
                    getString(R.string.text_pix_error_in_processing)
                ),
                title = getString(R.string.text_pix_key_delete_error_title),
                isFullScreen = false
            )
        }
    }

    override fun onErrorDelete(error: ErrorMessage?) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(OTP)) {
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.back),
                error = processErrorMessage(
                    error,
                    getString(R.string.business_error),
                    getString(R.string.text_pix_error_in_processing)
                ),
                title = getString(R.string.text_pix_key_delete_error_title),
                isFullScreen = false
            )
        }
    }

    override fun onSuccess(onAction: () -> Unit) {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    onAction.invoke()
                }
            })
    }

    override fun onHelpButtonClicked() {
        requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_PIX,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.cielo_facilita_central_de_ajuda_pix),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true
        )
    }

    override fun onClickSecondButtonError() {
        if (isFinish)
            requireActivity().finish()
    }

    override fun onActionSwipe() {
        if (isFinish)
            requireActivity().finish()
    }
}