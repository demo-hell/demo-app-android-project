package br.com.mobicare.cielo.pix.ui.transfer.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.inputtext.CieloInputText
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.InputTextHelper
import br.com.mobicare.cielo.commons.helpers.InputTextHelper.Companion.isValidDocument
import br.com.mobicare.cielo.commons.helpers.InputTextHelper.Companion.isValidPhoneNumber
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.PHONE_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.getTitlePix
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.keyTypes
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.commons.utils.showKeyboard
import br.com.mobicare.cielo.databinding.FragmentPixInsertKeyBinding
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_BALANCE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_INPUT_KEY_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_IS_TRUSTED_DESTINATION_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_TYPE_KEY_ARGS
import br.com.mobicare.cielo.pix.domain.ValidateKeyResponse
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.pix.model.ListKeyType
import br.com.mobicare.cielo.pix.ui.transfer.type.PixSelectorBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.type.PixSelectorContract
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixInsertKeyFragment : BaseFragment(), CieloNavigationListener, PixInsertKeyContract.View {
    private var binding: FragmentPixInsertKeyBinding? = null
    private val balance: String? by lazy {
        arguments?.getString(PIX_BALANCE_ARGS)
    }

    private val isTrustedDestination: Boolean by lazy {
        arguments?.getBoolean(PIX_IS_TRUSTED_DESTINATION_ARGS, false) ?: false
    }

    private val presenter: PixInsertKeyPresenter by inject {
        parametersOf(this)
    }

    private var keyType: PixKeyTypeEnum? = null
    private var navigation: CieloNavigation? = null
    private var inputText: CieloInputText? = null
    private var key: String? = null
    private var isGetKey = false
    private var isCreate = true
    private var isClear = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        FragmentPixInsertKeyBinding.inflate(inflater, container, false)
            .also {
                binding = it
            }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        isCreate = true
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        if (isCreate) {
            setupView()
        }

        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        isCreate = false
        isClear = false
        presenter.onPause()
    }

    private fun setupView() {
        setupInputTexts()
        setupTypeInput()
        changeKeyType()
        setupSubTitle()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(getTitlePix(isTrustedDestination)))
            navigation?.setTextButton(getString(R.string.text_pix_next))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.enableButton(isEnabled = true)
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupSubTitle() {
        binding?.tvSubtitleInsertKey?.text =
            if (isTrustedDestination) {
                getString(R.string.text_pix_insert_key_trusted_destination_subtitle)
            } else {
                getString(R.string.text_pix_insert_key_transfer_subtitle)
            }
    }

    private fun setupInputTexts() {
        binding?.apply {
            InputTextHelper.cPForCNPJInput(
                inputText = inputTextDocument,
            )
            InputTextHelper.phoneInput(
                inputText = inputTextPhone,
                phoneMask = PHONE_MASK_FORMAT,
            )
        }
    }

    private fun setupTypeInput() {
        getKeyData()
        when (keyType) {
            PixKeyTypeEnum.PHONE -> changeView(getString(R.string.text_pix_insert_key_transfer_title_phone))
            PixKeyTypeEnum.EMAIL -> changeView(getString(R.string.text_pix_insert_key_transfer_title_email))
            PixKeyTypeEnum.EVP -> changeView(getString(R.string.text_pix_insert_key_transfer_title_random))
            PixKeyTypeEnum.ACCOUNT ->
                changeView(
                    getString(R.string.text_pix_insert_key_transfer_title_account),
                    true,
                )

            else -> changeView()
        }
    }

    private fun changeView(
        title: String = getString(R.string.text_pix_insert_key_transfer_title_document),
        isAccount: Boolean = false,
    ) {
        setupInputTypesVisibility(isAccount)
        binding?.tvTitleInsertKey?.text = title
    }

    private fun setupInputTypesVisibility(isAccount: Boolean) {
        binding?.apply {
            if (isAccount) {
                includeInsertAccount.root.visible()
                containerInputKey.gone()
            } else {
                includeInsertAccount.root.gone()
                containerInputKey.visible()
                updateInputText()
            }
        }
    }

    private fun updateInputText() {
        listInputText()?.forEachIndexed { index, cieloInputText ->
            if (index == keyType?.id) {
                inputText = cieloInputText
                showErrorInput(isShow = false)
                setupValueInput()
                inputText.visible()
                requireActivity().showKeyboard(inputText)
            } else {
                cieloInputText.gone()
            }
        }
    }

    private fun setupValueInput() {
        val value =
            if (isClear) {
                EMPTY
            } else {
                if (isValidKey(key) && isGetKey) {
                    key ?: EMPTY
                } else {
                    EMPTY
                }
            }
        inputText?.setText(value)
        inputText?.setSelection(value.length)
    }

    private fun getKeyData() {
        if (keyType == null) {
            keyType = arguments?.getSerializable(PIX_TYPE_KEY_ARGS) as? PixKeyTypeEnum
        }
        if (isGetKey) {
            navigation?.getSavedData()?.get(PIX_INPUT_KEY_ARGS)?.let {
                key = it as? String
            }
        }
    }

    private fun listInputText() =
        binding?.run {
            listOf(
                inputTextDocument,
                inputTextPhone,
                inputTextEmail,
                inputTextRandom,
            )
        }

    private fun changeKeyType() {
        binding?.tvChangeKeyType?.setOnClickListener {
            val options = ListKeyType(keyTypes(requireContext(), isEVP = true, isBranch = true))

            requireActivity().hideSoftKeyboard()
            PixSelectorBottomSheet.onCreate(
                object : PixSelectorContract.Result {
                    override fun onShowKeyTypeSelected(keyType: PixKeyTypeEnum) {
                        if (this@PixInsertKeyFragment.keyType != keyType) {
                            if (keyType == PixKeyTypeEnum.ACCOUNT) {
                                goToSelectBank()
                            } else {
                                this@PixInsertKeyFragment.keyType = keyType
                                isClear = true
                                setupTypeInput()
                            }
                        }
                    }
                },
                options,
                getString(R.string.text_title_selector_pix),
            )
                .show(childFragmentManager, tag)
        }
    }

    private fun goToSelectBank() {
        findNavController().navigate(
            PixInsertKeyFragmentDirections.actionPixInsertKeyFragmentToPixSelectBankFragment(
                isTrustedDestination,
                balance ?: DEFAULT_BALANCE,
            ),
        )
    }

    private fun showErrorInput(
        errorMessage: String = getString(R.string.text_pix_insert_key_transfer_error),
        isShow: Boolean = true,
    ) {
        inputText?.setError(errorMessage)
        inputText?.setErrorImage(R.drawable.ic_alert_red)
        inputText?.showErrorWithIcon(isShow)
    }

    private fun saveData() {
        if (inputText != null) {
            val bundle = Bundle()
            bundle.putString(PIX_INPUT_KEY_ARGS, inputText?.getText() ?: EMPTY)
            navigation?.saveData(bundle)
        }
    }

    override fun onButtonClicked(labelButton: String) {
        val isValidKey = isValidKey(inputText?.getText())
        showErrorInput(isShow = isValidKey.not())
        if (isValidKey) {
            validateKey()
        }
    }

    private fun validateKey() {
        inputText?.getText()?.let {
            presenter.onValidateKey(it, keyType)
        }
    }

    private fun isValidKey(value: String?): Boolean =
        when (keyType?.id) {
            PixKeyTypeEnum.CNPJ.id ->
                value
                    ?.isNotEmpty() == true && value.isValidDocument()

            PixKeyTypeEnum.PHONE.id ->
                value
                    ?.isNotEmpty() == true && value.isValidPhoneNumber()

            PixKeyTypeEnum.EMAIL.id ->
                value
                    ?.isNotEmpty() == true && ValidationUtils.isEmail(value)

            PixKeyTypeEnum.EVP.id -> value?.isNotEmpty() == true
            else -> false
        }

    override fun showLoading() {
        requireActivity().hideSoftKeyboard()
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        requireActivity().hideSoftKeyboard()
        navigation?.showContent(true)
    }

    override fun onValidKey(response: ValidateKeyResponse) {
        isGetKey = true
        saveData()

        findNavController().navigate(
            if (isTrustedDestination) {
                PixInsertKeyFragmentDirections.actionPixInsertKeyFragmentToPixMyLimitsAddNewTrustedDestinationFragment(
                    null,
                    response,
                    true,
                )
            } else {
                PixInsertKeyFragmentDirections.actionPixInsertKeyFragmentToPixTransferSummaryFragment(
                    response,
                    balance ?: DEFAULT_BALANCE,
                    null,
                )
            },
        )
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.text_error_update),
            error =
                processErrorMessage(
                    error,
                    getString(R.string.business_error),
                    getString(R.string.text_pix_key_error_message),
                ),
            title = getString(R.string.text_pix_key_error_title),
        )
    }

    override fun onClickSecondButtonError() {
        validateKey()
    }

    override fun onErrorInput(error: ErrorMessage) {
        showErrorInput(error.message)
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().hideSoftKeyboard()
        return super.onBackButtonClicked()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
