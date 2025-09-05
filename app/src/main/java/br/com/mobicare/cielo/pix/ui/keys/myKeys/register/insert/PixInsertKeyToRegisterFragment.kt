package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.insert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.inputtext.CieloInputText
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.InputTextHelper
import br.com.mobicare.cielo.commons.helpers.InputTextHelper.Companion.isValidPhoneNumber
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_INPUT_KEY_CREATE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_TYPE_KEY_ARGS
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import kotlinx.android.synthetic.main.fragment_pix_insert_key_to_register.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class PixInsertKeyToRegisterFragment : BaseFragment(), CieloNavigationListener,
    PixInsertKeyToRegisterContract.View {

    private val presenter: PixInsertKeyToRegisterPresenter by inject {
        parametersOf(this)
    }
    private val keyType: PixKeyTypeEnum? by lazy {
        arguments?.getSerializable(PIX_TYPE_KEY_ARGS) as? PixKeyTypeEnum
    }

    private var navigation: CieloNavigation? = null
    private var inputText: CieloInputText? = null
    private var key: String? = null
    private var isGetKey = false
    private var isCreate = true
    private var isClear = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_pix_insert_key_to_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCreate = true
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        if (isCreate)
            setupView()
    }

    override fun onPause() {
        super.onPause()
        isCreate = false
        isClear = false
        presenter.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_key_registration_pix))
            navigation?.setTextButton(getString(R.string.text_next_label))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        getKeyData()
        setupTypeInput()
        setupValueInput()
    }

    private fun getKeyData() {
        if (isGetKey)
            navigation?.getSavedData()?.get(PIX_INPUT_KEY_CREATE_ARGS)?.let {
                key = it as? String
            }
    }

    private fun setupTypeInput() {
        inputText = if (keyType == PixKeyTypeEnum.PHONE) {
            setupInputText(isPhone = true)
            InputTextHelper.phoneInput(
                inputText = input_text_phone,
                phoneMask = PHONE_MASK_FORMAT
            )
            input_text_phone
        } else {
            setupInputText(
                getString(R.string.text_pix_create_key_email_title),
                getString(R.string.text_pix_create_key_email_subtitle)
            )
            input_text_email
        }
    }

    private fun setupInputText(
        title: String = getString(R.string.text_pix_create_key_phone_title),
        subtitle: String = getString(R.string.text_pix_create_key_phone_subtitle),
        isPhone: Boolean = false
    ) {
        tv_title_new_key?.text = title
        tv_subtitle_new_key?.text = subtitle

        if (isPhone) {
            input_text_phone?.visible()
            input_text_email?.gone()
        } else {
            input_text_phone?.gone()
            input_text_email?.visible()
        }

        requireActivity().showKeyboard(inputText)
    }

    private fun setupValueInput() {
        val value = if (isClear) EMPTY else {
            if (isValidKey(key) && isGetKey)
                key ?: EMPTY else EMPTY
        }
        inputText?.setText(value)
        inputText?.setSelection(value.length)
    }

    private fun showErrorInput(
        inputText: CieloInputText?,
        errorMessage: String = getString(R.string.text_pix_insert_key_transfer_error),
        isShow: Boolean = true
    ) {
        inputText?.setError(errorMessage)
        inputText?.setErrorImage(R.drawable.ic_alert_red)
        inputText?.showErrorWithIcon(isShow)
    }

    private fun isValidKey(value: String?): Boolean {
        return if (keyType == PixKeyTypeEnum.PHONE)
            value?.isNotEmpty() == true && value.isValidPhoneNumber()
        else
            value?.isNotEmpty() == true && ValidationUtils.isEmail(value)
    }

    private fun toValidationCode() {
        keyType?.let {
            isGetKey = true
            saveData()
            findNavController().navigate(
                PixInsertKeyToRegisterFragmentDirections.actionPixInsertKeyToRegisterFragmentToPixValidationCodeFragment(
                    it, inputText?.getText() ?: EMPTY, false, EMPTY
                )
            )
        }
    }

    private fun saveData() {
        if (inputText != null) {
            val bundle = Bundle()
            bundle.putString(PIX_INPUT_KEY_CREATE_ARGS, inputText?.getText() ?: EMPTY)
            navigation?.saveData(bundle)
        }
    }

    private fun sendCode() {
        val isValidKey = isValidKey(inputText?.getText())
        showErrorInput(inputText = inputText, isShow = isValidKey.not())
        if (isValidKey)
            presenter.onSendValidationCode(inputText?.getText(), keyType)
    }

    override fun onButtonClicked(labelButton: String) {
        requireActivity().hideSoftKeyboard()
        sendCode()
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onSuccessSendCode() {
        toValidationCode()
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.back),
            error = processErrorMessage(
                error,
                getString(R.string.business_error),
                getString(R.string.text_pix_create_key_insert_code_error_subtitle)
            ),
            title = getString(R.string.text_pix_create_key_insert_code_error_title),
            isFullScreen = false
        )
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().hideSoftKeyboard()
        return super.onBackButtonClicked()
    }
}