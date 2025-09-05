package br.com.mobicare.cielo.meuCadastroNovo.presetantion.userDataChange

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.lifecycle.lifecycleScope
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.SIX
import br.com.mobicare.cielo.commons.helpers.CieloTextInputViewHelper
import br.com.mobicare.cielo.commons.helpers.CieloTextInputViewHelper.Companion.isValidPhoneNumber
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.PHONE_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.phoneNumber
import br.com.mobicare.cielo.databinding.ActivityChangeUserDataBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.ARG_CHANGE_USER_DATA_EMAIL
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.ARG_CHANGE_USER_DATA_FOREIGN
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.ARG_CHANGE_USER_DATA_PHONE
import br.com.mobicare.cielo.meuCadastroNovo.utils.UserDataChangeUiState
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants
import br.com.mobicare.cielo.selfieChallange.presentation.SelfieChallengeActivity
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeError
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeParams
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeResult
import br.com.mobicare.cielo.selfieChallange.utils.SelfieErrorEnum
import br.com.mobicare.cielo.selfieChallange.utils.SelfieOperation
import kotlinx.android.synthetic.main.activity_change_user_data.btnNext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

class UserDataChangeActivity : BaseLoggedActivity() {

    private val viewModel: UserDataChangeViewModel by viewModel()
    private lateinit var binding: ActivityChangeUserDataBinding
    private val userEmail: String by lazy {
        intent?.extras?.getString(ARG_CHANGE_USER_DATA_EMAIL) ?: EMPTY
    }
    private val userPhone: String by lazy {
        intent?.extras?.getString(ARG_CHANGE_USER_DATA_PHONE) ?: EMPTY
    }
    private val userIsForeign: Boolean by lazy {
        intent?.extras?.getBoolean(ARG_CHANGE_USER_DATA_FOREIGN) ?: false
    }
    private var hasEmailChange = false
    private var hasPhoneChange = false
    private var hasPasswordChange = false
    private lateinit var selfieChallengeLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupClickListeners()
        setupObservers()
        setupSdkListener()
    }

    private fun setupView() {
        binding.apply {

            CieloTextInputViewHelper.phoneInput(
                inputText = this.tiPhone,
                phoneMask = PHONE_MASK_FORMAT
            )

            if (userEmail.isNotEmpty()) {
                tiEmail.setText(userEmail)
            }

            if (userPhone.isNotEmpty()) {
                tiPhone.setText(userPhone)
            }

            tiEmail.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!ValidationUtils.isEmail(tiEmail.getText())) {
                        hasEmailChange = false
                        tiEmail.setError(getString(R.string.user_data_change_text_error_email))
                    } else {
                        hasEmailChange = true
                        tiEmail.setError(null)
                    }
                    enableNextButton()
                }
            })

            tiPhone.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                var isUpdate = false
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let {
                        if (!isUpdate) {
                            isUpdate = true
                            val phoneNumberMask =
                                EditTextHelper.phoneMaskFormatter(it.toString(), PHONE_MASK_FORMAT)
                            val phoneNumberString = phoneNumberMask.formattedText.string
                            tiPhone.setText(phoneNumberString)
                            tiPhone.setSelection(phoneNumberString.length)
                            if (!phoneNumberString.isValidPhoneNumber()) {
                                hasPhoneChange = false
                                tiPhone.setError(getString(R.string.user_data_change_text_error_phone))
                            } else {
                                hasPhoneChange = true
                                tiPhone.setError(null)
                            }
                            isUpdate = false
                            enableNextButton()
                        }
                    }
                }
            })

            tiNewPassword.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btnNext.isEnabled = false
                    if (tiNewPassword.getText().length != SIX) {
                        tiNewPassword.setError(getString(R.string.user_data_change_password_six_digits))
                    } else {
                        tiNewPassword.setError(null)
                    }
                }
            })

            tiConfirmation.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (tiConfirmation.getText().length != SIX || tiConfirmation.getText() != tiNewPassword.getText()) {
                        hasPasswordChange = false
                        tiConfirmation.setError(getString(R.string.user_data_change_password_different))
                        btnNext.isEnabled = false
                    } else {
                        tiConfirmation.setError(null)
                        hasPasswordChange = true
                        enableNextButton()
                    }
                }
            })
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btBackArrow.setOnClickListener {
                finish()
            }

            btnNext.setOnClickListener {
                showLoading()
                viewModel.postUserValidateData(
                    email = checkEmailValue(),
                    password = checkPasswordValue(),
                    passwordConfirmation = checkPasswordConfirmationValue(),
                    cellphone = checkPhoneValue()
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.userDataChangeLiveData.observe(this) { uiState ->
            when (uiState) {
                is UserDataChangeUiState.GenericError -> {
                    showCustomBottomSheet()
                    hideLoading()
                }

                is UserDataChangeUiState.UserValidateEmailError -> {
                    binding.tiEmail.setError(uiState.message)
                    binding.btnNext.isEnabled = false
                    hideLoading()
                }

                is UserDataChangeUiState.UserValidatePasswordError -> {
                    binding.tiNewPassword.setError(uiState.message)
                    hideLoading()
                    binding.btnNext.isEnabled = false
                }

                is UserDataChangeUiState.UserValidatePhoneError -> {
                    binding.tiPhone.setError(uiState.message)
                    binding.btnNext.isEnabled = false
                    hideLoading()
                }

                is UserDataChangeUiState.UserValidateSuccess -> {
                    openSelfieChallange()
                    hideLoading()
                }

                is UserDataChangeUiState.UserUpdateError -> {
                    hideLoading()
                    showCustomBottomSheet(
                        image = R.drawable.img_10_erro,
                        title = getString(R.string.user_data_change_bs_error_title),
                        message = getString(R.string.user_data_change_bs_error_message),
                        bt2Title = getString(R.string.text_try_again_label)
                    )
                }

                is UserDataChangeUiState.UserUpdateSuccess -> {
                    hideLoading()
                    showCustomBottomSheet(
                        image = R.drawable.img_14_estrelas,
                        title = getString(R.string.user_data_change_bs_success_title),
                        message = getString(R.string.user_data_change_bs_success_message),
                        bt2Title = getString(R.string.finish),
                        bt2Callback = {
                            baseLogout()
                            false
                        }, closeCallback = {
                            baseLogout()
                        }
                    )
                }
            }
        }
    }

    private fun setupSdkListener() {
        selfieChallengeLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    it.data?.let { intent ->
                        selfieChallengeSuccess(intent)?.let { selfieChallengeResult ->
                                showLoading()
                                binding.apply {
                                    viewModel.putUserUpdateData(
                                        email = checkEmailValue(),
                                        password = checkPasswordValue(),
                                        passwordConfirmation = checkPasswordConfirmationValue(),
                                        cellphone = checkPhoneValue(),
                                        faceIdToken = selfieChallengeResult.faceIdToken.orEmpty()
                                    )
                                }
                            }
                    }
                }

                else -> {
                    it.data?.let { intent ->
                        selfieChallengeError(intent)?.let { selfieError ->
                            hideLoading()
                            when (selfieError.type) {
                                SelfieErrorEnum.CAMERA_CLOSED_MANUALLY -> {
                                    showCustomBottomSheet(
                                        image = R.drawable.img_10_erro,
                                        title = getString(R.string.user_data_change_bs_close_sdk_title),
                                        message = getString(R.string.user_data_change_bs_close_sdk_message),
                                        bt1Title = getString(R.string.user_data_change_bs_close_sdk_button1),
                                        bt2Title = getString(R.string.user_data_change_bs_close_sdk_button2),
                                        bt2Callback = {
                                            openSelfieChallange()
                                            false
                                        }
                                    )
                                }

                                SelfieErrorEnum.SDK_SELFIE_CHALLENGE_GENERIC_ERROR -> {
                                    showCustomBottomSheet(
                                        image = R.drawable.img_10_erro,
                                        title = getString(R.string.user_data_change_bs_generic_error_sdk_title),
                                        message = getString(R.string.user_data_change_bs_generic_error_sdk_message),
                                        bt2Title = getString(R.string.user_data_change_bs_generic_error_sdk_button),
                                        bt2Callback = {
                                            openSelfieChallange()
                                            false
                                        }
                                    )
                                }

                                SelfieErrorEnum.USER_DENIED_CAMERA_PERMISSION -> {
                                    showCustomBottomSheet(
                                        image = R.drawable.img_10_erro,
                                        title = getString(R.string.user_data_change_bs_permission_error_sdk_title),
                                        message = getString(R.string.user_data_change_bs_permission_error_sdk_message),
                                        bt2Title = getString(R.string.entendi),
                                        bt2Callback = {
                                            moveToHome()
                                            false
                                        }
                                    )
                                }

                                SelfieErrorEnum.SEND_SELFIE_ERROR -> {
                                    showCustomBottomSheet(
                                        image = R.drawable.img_10_erro,
                                        title = getString(R.string.user_data_change_bs_generic_api_error_sdk_title),
                                        message = getString(R.string.user_data_change_bs_generic_api_error_sdk_message),
                                        bt2Title = getString(R.string.entendi),
                                        bt2Callback = {
                                            moveToHome()
                                            false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            messageProgressView.showLoading()
            scrollView.gone()
        }
    }

    private fun hideLoading() {
        binding.apply {
            scrollView.visible()
            messageProgressView.hideLoading()
        }
    }

    private fun enableNextButton() {
        btnNext.isEnabled = hasEmailChange || hasPasswordChange || hasPhoneChange
    }

    private fun checkEmailValue(): String {
        return if (hasEmailChange) binding.tiEmail.getText() else EMPTY
    }

    private fun checkPhoneValue(): String {
        return if (hasPhoneChange) binding.tiPhone.getText().phoneNumber() else EMPTY
    }

    private fun checkPasswordValue(): String {
        return if (hasPasswordChange) binding.tiNewPassword.getText() else EMPTY
    }

    private fun checkPasswordConfirmationValue(): String {
        return if (hasPasswordChange) binding.tiConfirmation.getText() else EMPTY
    }

    private fun selfieChallengeSuccess(intent: Intent): SelfieChallengeResult? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            intent.getSerializableExtra(
                SelfieChallengeConstants.SELFIE_CHALLENGE_SUCCESS,
                SelfieChallengeResult::class.java
            )
        } else {
            intent.getSerializableExtra(SelfieChallengeConstants.SELFIE_CHALLENGE_SUCCESS) as SelfieChallengeResult?
        }
    }


    private fun selfieChallengeError(intent: Intent): SelfieChallengeError? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            intent.getSerializableExtra(
                SelfieChallengeConstants.SELFIE_CHALLENGE_ERROR,
                SelfieChallengeError::class.java
            )
        } else {
            intent.getSerializableExtra(SelfieChallengeConstants.SELFIE_CHALLENGE_ERROR) as SelfieChallengeError?
        }
    }

    private fun openSelfieChallange() {
        val intent = Intent(this, SelfieChallengeActivity::class.java).apply {
            val selfieChallengeParams = SelfieChallengeParams(
                isForeign = userIsForeign,
                operation = SelfieOperation.UPDATE_DATA
            )
            putExtra(
                SelfieChallengeConstants.SELFIE_CHALLENGE_PARAMS,
                selfieChallengeParams as Serializable
            )
        }
        selfieChallengeLauncher.launch(intent)
    }

    fun showCustomBottomSheet(
        @DrawableRes image: Int? = null,
        title: String? = null,
        message: String? = null,
        bt1Title: String? = null,
        bt2Title: String? = null,
        bt1Callback: (() -> Boolean)? = null,
        bt2Callback: (() -> Boolean)? = null,
        closeCallback: (() -> Unit)? = null
    ) {
        lifecycleScope.launchWhenResumed {
            bottomSheetGenericFlui(
                image = image ?: R.drawable.ic_generic_error_image,
                title = title ?: getString(R.string.generic_error_title),
                subtitle = message ?: getString(R.string.error_generic),
                nameBtn1Bottom = bt1Title ?: br.com.cielo.libflue.util.EMPTY,
                nameBtn2Bottom = bt2Title ?: getString(R.string.ok),
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET,
                statusBtnFirst = bt1Title != null,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
                isCancelable = true,
                isFullScreen = false,
                isPhone = false
            ).apply {
                onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                        override fun onBtnFirst(dialog: Dialog) {
                            if (bt1Callback?.invoke() != true) dismiss()
                        }

                        override fun onBtnSecond(dialog: Dialog) {
                            if (bt2Callback?.invoke() != true) dismiss()
                        }

                        override fun onSwipeClosed() {
                            closeCallback?.invoke()
                        }

                        override fun onCancel() {
                            closeCallback?.invoke()
                        }
                    }
            }.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }

}