package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.registration

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
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
import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.getFormattedKey
import br.com.mobicare.cielo.commons.utils.getKeyType
import br.com.mobicare.cielo.commons.utils.getKeyTypeName
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.databinding.FragmentPixKeyRegistrationBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_TYPE_KEY_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_TYPE_KEY_VALUE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_VALIDATION_CODE_VALUE_ARGS
import br.com.mobicare.cielo.pix.domain.ClaimsResponse
import br.com.mobicare.cielo.pix.domain.ValidateKeyResponse
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class PixKeyRegistrationFragment : BaseFragment(), CieloNavigationListener,
    PixKeyRegistrationContract.View {

    private val keyType: PixKeyTypeEnum? by lazy {
        arguments?.getSerializable(PIX_TYPE_KEY_ARGS) as? PixKeyTypeEnum
    }

    private val verificationCode: String? by lazy {
        arguments?.getString(PIX_VALIDATION_CODE_VALUE_ARGS)
    }
    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }
    private val presenter: PixKeyRegistrationPresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null
    private var key: String? = null
    private var binding: FragmentPixKeyRegistrationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        key = arguments?.getString(PIX_TYPE_KEY_VALUE_ARGS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPixKeyRegistrationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupKeyType()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_key_registration_pix))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupKeyType() {
        when (keyType) {
            PixKeyTypeEnum.PHONE -> setupPhoneRegistration()
            PixKeyTypeEnum.EMAIL -> setupEmailRegistration()
            PixKeyTypeEnum.EVP -> setupRandomRegistration()
            else -> setupDocumentRegistration()
        }
    }

    private fun setupDocumentRegistration() {
        key = presenter.getDocument()
        setupView(key = presenter.getDocument())
    }

    private fun setupRandomRegistration() {
        setupView(
            R.string.text_pix_key_registration_random,
            R.string.text_pix_key_registration_random_subtitle,
            R.string.text_pix_my_keys_random_key,
            R.drawable.ic_random_key_pix
        )
    }

    private fun setupPhoneRegistration() {
        setupView(
            R.string.text_pix_key_registration_phone,
            R.string.text_pix_key_registration_phone_subtitle,
            R.string.text_pix_my_keys_phone,
            R.drawable.ic_phone_key_pix,
            key
        )
    }

    private fun setupEmailRegistration() {
        setupView(
            R.string.text_pix_key_registration_email,
            R.string.text_pix_key_registration_email_subtitle,
            R.string.text_pix_my_keys_email,
            R.drawable.ic_email_key_pix,
            key
        )
    }

    private fun setupView(
        @StringRes title: Int = R.string.text_pix_key_registration_cnpj,
        @StringRes subTitle: Int = R.string.text_pix_key_registration_cnpj_subtitle,
        @StringRes typeKey: Int = R.string.text_pix_my_keys_cnpf,
        @DrawableRes icon: Int = R.drawable.ic_document_key_pix,
        key: String? = null
    ) {
        navigation?.setTextButton(getString(title))

        binding?.apply {
            tvTitleRegisterKey.text = getString(title)
            tvSubtitleInsertKey.text = getString(subTitle)
            tvTitleTypeKeyRegisterKey.text = getString(typeKey)

            if (key == null)
                tvKeyRegisterKey.gone()
            else {
                tvKeyRegisterKey.visible()
                tvKeyRegisterKey.text = key
            }

            ivDataRegisterKey.apply {
                setImageResource(icon)
                setColorFilter(ContextCompat.getColor(context, R.color.display_300))
            }
        }
    }

    private fun toMyKeys() {
        findNavController().safeNavigate(
            PixKeyRegistrationFragmentDirections.actionPixKeyRegistrationFragmentToPixMyKeysFragment(
                false
            )
        )
    }

    private fun bottomSheetSuccessClaims(
        @StringRes title: Int,
        @StringRes message: Int,
        dateDefault: String,
        claimsResponse: ClaimsResponse
    ) {
        val keyType = this.keyType?.name?.let { getKeyTypeName(it) } ?: EMPTY
        val key = binding?.tvKeyRegisterKey?.text ?: EMPTY
        val formattedKey = "$keyType $key"
        val date = claimsResponse.resolutionLimitDate?.let {
            val clearDate = it.clearDate()
            clearDate.formatterDate(LONG_TIME_NO_UTC)
        } ?: dateDefault

        val finalMessage = getString(message, formattedKey, date)
        bottomSheetGenericFlui(
            nameTopBar = "",
            R.drawable.ic_validado_transfer,
            getString(title),
            finalMessage,
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
                        toMyKeys()
                    }

                    override fun onSwipeClosed() {
                        toMyKeys()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun createKey() {
        keyType?.let {
            if (it == PixKeyTypeEnum.EVP)
                validationTokenWrapper.generateOtp(showAnimation = true,
                    onResult = { otpCode ->
                        presenter.onRegisterKey(
                            otp = otpCode,
                            key = key,
                            type = it.name,
                            code = verificationCode
                        )
                    }
                )
            else
                validationTokenWrapper.generateOtp(showAnimation = true,
                    onResult = { otpCode ->
                        presenter.onValidateKey(
                            otp = otpCode,
                            key = key,
                            type = it.name,
                            code = verificationCode
                        )
                    }
                )
        }
    }

    private fun showErrorMessage(
        error: ErrorMessage?,
        title: String = getString(R.string.text_pix_key_registration_error_title)
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
                    getString(R.string.text_pix_error_in_processing)
                ),
                title = title,
                isFullScreen = false
            )
        }
    }

    private fun createClaims(type: String) {
        keyType?.let {
            validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                presenter.onCreateClaim(
                    otpCode,
                    key,
                    it.name,
                    type,
                    verificationCode
                )
            }
        }
    }

    private fun toFaq() {
        requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_PIX,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.cielo_facilita_central_de_ajuda_pix),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true
        )
    }

    override fun onButtonClicked(labelButton: String) {
        createKey()
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onShowPortability(response: ValidateKeyResponse) {
        val key = "${getKeyType(key = response.key, type = response.keyType)} ${
            getFormattedKey(
                key = response.key, type = response.keyType
            )
        }"
        val message = getString(
            R.string.text_pix_key_registration_portability_subtitle,
            key, response.participantName ?: EMPTY
        )
        CieloDialog.create(
            title = getString(R.string.text_pix_key_registration_portability_title),
            message = message
        ).setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setPrimaryButton(getString(R.string.text_pix_key_registration_portability_btn_one))
            .setSecondaryButton(getString(R.string.text_pix_key_registration_portability_btn_two))
            .setOnPrimaryButtonClickListener {
                createClaims(PixClaimTypeEnum.PORTABILITY.name)
            }.setOnSecondaryButtonClickListener {
                toFaq()
            }.setOnCancelListener {
                toMyKeys()
            }.setOnCloseClickListener {
                toMyKeys()
            }.show(childFragmentManager, PixKeyRegistrationFragment::class.java.simpleName)
    }

    override fun onShowClaim(response: ValidateKeyResponse) {
        val key = "${getKeyType(key = response.key, type = response.keyType)} ${
            getFormattedKey(
                key = response.key, type = response.keyType
            )
        }"

        val message = getString(
            R.string.text_pix_key_registration_claim_subtitle,
            key
        )

        CieloDialog.create(
            title = getString(R.string.text_pix_key_registration_claim_title),
            message = message
        ).setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setPrimaryButton(getString(R.string.text_pix_key_registration_claim_btn_one))
            .setSecondaryButton(getString(R.string.text_pix_key_registration_claim_btn_two))
            .setOnPrimaryButtonClickListener {
                createClaims(PixClaimTypeEnum.OWNERSHIP.name)
            }.setOnSecondaryButtonClickListener {
                toFaq()
            }.setOnCancelListener {
                toMyKeys()
            }.setOnCloseClickListener {
                toMyKeys()
            }.show(childFragmentManager, PixKeyRegistrationFragment::class.java.simpleName)
    }

    override fun onShowErrorValidateKey(error: ErrorMessage?) {
        showErrorMessage(error)
    }

    override fun onSuccess(onAction: () -> Unit) {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    onAction.invoke()
                }
            })
    }

    override fun onSuccessRegisterKey() {
        toMyKeys()
    }

    override fun onShowError(onFirstAction: () -> Unit) {
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                onFirstAction.invoke()
            }
        })
    }

    override fun onErrorRegisterKey(error: ErrorMessage?) {
        showErrorMessage(error)
    }

    override fun onErrorCreateClaimOwnership(error: ErrorMessage?) {
        showErrorMessage(
            error = error,
            title = getString(R.string.text_pix_error_create_claims_ownership_title)
        )
    }

    override fun onErrorCreateClaimPortability(error: ErrorMessage?) {
        showErrorMessage(
            error = error,
            title = getString(R.string.text_pix_error_create_claims_portability_title)
        )
    }

    override fun onSuccessCreateClaimOwnership(response: ClaimsResponse) {
        bottomSheetSuccessClaims(
            title = R.string.text_pix_success_create_claims_ownership_title,
            message = R.string.text_pix_success_create_claims_ownership_message,
            dateDefault = getString(R.string.text_pix_create_claims_ownership_day),
            claimsResponse = response
        )
    }

    override fun onSuccessCreateClaimPortability(response: ClaimsResponse) {
        bottomSheetSuccessClaims(
            title = R.string.text_pix_success_create_claims_portability_title,
            message = R.string.text_pix_success_create_claims_portability_message,
            dateDefault = getString(R.string.text_pix_create_claims_portability_day),
            claimsResponse = response
        )
    }

    override fun onClickSecondButtonError() {
        toMyKeys()
    }

    override fun onActionSwipe() {
        toMyKeys()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}