package br.com.mobicare.cielo.pixMVVM.presentation.key.ui

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.bottomsheet.CieloButtonListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.THIRTY_TWO
import br.com.mobicare.cielo.commons.constants.THREE_HUNDRED
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.InputTextHelper.Companion.isValidDocument
import br.com.mobicare.cielo.commons.helpers.InputTextHelper.Companion.isValidPhoneNumber
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentPixInsertAllKeysBinding
import br.com.mobicare.cielo.databinding.LayoutPixFooterRoundedButtonBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixKeyTypeButton
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixKeyTypeInput
import br.com.mobicare.cielo.pixMVVM.presentation.key.utils.PixInsertAllKeysUIState
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixInsertAllKeysViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixInsertAllKeysFragment : BaseFragment(), CieloNavigationListener {
    private val viewModel: PixInsertAllKeysViewModel by viewModel()

    private var binding: FragmentPixInsertAllKeysBinding? = null
    private var footerBinding: LayoutPixFooterRoundedButtonBinding? = null
    private var navigation: CieloNavigation? = null

    private val args: PixInsertAllKeysFragmentArgs by navArgs()

    private val keyTypeFromFragment by lazy {
        PixKeyTypeInput.fromOrdinal(args.keytypeargs) ?: PixKeyTypeInput.CPF_CNPJ
    }

    private val getKeyValue get() = binding?.tifKey?.textInputEditText?.text?.toString().orEmpty()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        LayoutPixFooterRoundedButtonBinding.inflate(
            inflater,
            container,
            false,
        ).also { footerBinding = it }

        return FragmentPixInsertAllKeysBinding.inflate(
            inflater,
            container,
            false,
        ).also { binding = it }.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setKeyTypeButton(keyTypeFromFragment)
        setupNavigation()
        setupListeners()
        setupObservers()
        setupTextInputField()
        focusOnTextInputField()
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        footerBinding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation =
                (requireActivity() as CieloNavigation).also {
                    it.setNavigationListener(this)
                    setupToolbar()
                }
        }
    }

    private fun setupToolbar() {
        navigation?.configureCollapsingToolbar(generateCollapsingToolbar())
    }

    private fun generateCollapsingToolbar() =
        CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar =
                CieloCollapsingToolbarLayout.Toolbar(
                    title = viewModel.keyTypeInput.value?.toolbarText?.let { getString(it) }.orEmpty(),
                    menu =
                        CieloCollapsingToolbarLayout.ToolbarMenu(
                            menuRes = R.menu.menu_help,
                            onOptionsItemSelected = ::onMenuOptionSelected,
                        ),
                ),
            footerView = footerBinding?.root,
        )

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix),
            )
        }
    }

    private fun setupListeners() {
        binding?.btnChangeKeyType?.setOnClickListener(::onChangeKeyType)
        footerBinding?.button?.setOnClickListener(::onNextButtonClicked)
    }

    private fun setupObservers() {
        setupObserverKeyTypeButton()
        setupObserverUIState()
    }

    private fun setupObserverKeyTypeButton() {
        viewModel.keyTypeInput.observe(viewLifecycleOwner) {
            setupToolbar()
            setupTextInputField()
        }
    }

    private fun setupObserverUIState() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is PixInsertAllKeysUIState.ShowLoading -> onShowLoading()
                is PixInsertAllKeysUIState.HideLoading -> onHideLoading()
                is PixInsertAllKeysUIState.Success -> onSuccess(uiState.pixValidateKey)
                is PixInsertAllKeysUIState.GenericError -> onGenericError(uiState.errorMessage)
                is PixInsertAllKeysUIState.InputError -> onInputError(uiState.errorMessage)
                is PixInsertAllKeysUIState.UnavailableService -> onUnavailableService(uiState.errorMessage)
            }
        }
    }

    private fun setupTextInputField() {
        viewModel.keyTypeInput.value?.let { keyType ->
            binding?.tifKey?.apply {
                hint = keyType.hintText?.let { hint -> getString(hint) }.orEmpty()
                keyType.mask?.let { setMask(it) } ?: run { removeMask() }
                setTextChangedListener {
                    helperText = null
                    unsetError()
                }
                setValidators(*getValidators())
                text = EMPTY
                textInputEditText.apply {
                    imeOptions = EditorInfo.IME_ACTION_DONE
                    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(keyType.maxLength))
                    keyType.inputType?.let { inputType = it }
                }
            }
        }
    }

    private fun onChangeKeyType(view: View) {
        requireActivity().hideSoftKeyboard(delayAfterClosing = THREE_HUNDRED.toLong())

        CieloButtonListBottomSheet.create(
            headerConfigurator =
                CieloBottomSheet.HeaderConfigurator(
                    title = getString(R.string.pix_home_transaction_transfer_bs_title),
                    showCloseButton = true,
                ),
            buttons =
                PixKeyTypeButton.values().map {
                    CieloButtonListBottomSheet.Button(
                        id = it.ordinal,
                        text = getString(it.label),
                        drawableRes = it.iconId,
                    )
                },
            onButtonItemClicked = { button, bs ->
                bs.dismissAllowingStateLoss()
                onButtonChangeKeyTypeClicked(button.id)
            },
        ).show(parentFragmentManager, PixInsertAllKeysFragment::class.java.simpleName)
    }

    private fun onButtonChangeKeyTypeClicked(id: Int) {
        PixKeyTypeInput.fromOrdinal(id)?.let {
            if (it == PixKeyTypeInput.BANK_ACCOUNT) {
                navigateToInsertBankAccount()
            } else {
                viewModel.setKeyTypeButton(it)
            }
        }
    }

    private fun onNextButtonClicked(view: View) {
        requireActivity().hideSoftKeyboard()

        binding?.tifKey?.let { textInputField ->
            textInputField.clearFocus()
            if (textInputField.validate()) {
                viewModel.validateKey(getKeyValue)
            }
        }
    }

    private fun getValidators() =
        arrayOf(
            CieloTextInputField.Validator(
                rule = { it.extractedValue.isNotBlank() },
                onResult = { isValid, _ ->
                    binding?.tifKey?.helperText =
                        if (isValid) null else getString(R.string.pix_insert_all_keys_error_empty_key)
                },
            ),
            getValidatorByKeyType(),
        )

    private fun getValidatorByKeyType() =
        viewModel.keyTypeInput.value.let { keyType ->
            when (keyType) {
                PixKeyTypeInput.CPF_CNPJ -> {
                    CieloTextInputField.Validator(
                        rule = { it.value.isValidDocument() },
                        onResult = { isValid, _ ->
                            binding?.tifKey?.helperText =
                                if (isValid) null else getString(R.string.pix_insert_all_keys_error_invalid_key)
                        },
                    )
                }

                PixKeyTypeInput.PHONE -> {
                    CieloTextInputField.Validator(
                        rule = { it.value.isValidPhoneNumber() },
                        onResult = { isValid, _ ->
                            binding?.tifKey?.helperText =
                                if (isValid) null else getString(R.string.pix_insert_all_keys_error_invalid_key)
                        },
                    )
                }

                PixKeyTypeInput.EMAIL -> {
                    CieloTextInputField.Validator(
                        rule = { ValidationUtils.isEmail(it.value) },
                        onResult = { isValid, _ ->
                            binding?.tifKey?.helperText =
                                if (isValid) null else getString(R.string.pix_insert_all_keys_error_invalid_key)
                        },
                    )
                }

                else -> {
                    CieloTextInputField.Validator(
                        rule = { it.extractedValue.length == THIRTY_TWO },
                        onResult = { isValid, _ ->
                            binding?.tifKey?.helperText =
                                if (isValid) null else getString(R.string.pix_insert_all_keys_error_invalid_key)
                        },
                    )
                }
            }
        }

    private fun focusOnTextInputField() {
        requireActivity().showSoftKeyboard(binding?.tifKey?.textInputEditText)
    }

    private fun onShowLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun onHideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun onSuccess(pixValidateKey: PixValidateKey) {
        findNavController().navigate(
            PixInsertAllKeysFragmentDirections.actionPixInsertAllKeysFragmentToPixTransferAmountFragment(
                pixValidateKey,
                null,
            ),
        )
    }

    private fun onGenericError(errorMessage: NewErrorMessage?) {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                title = getString(R.string.pix_insert_all_keys_title_bottom_sheet_impossible_to_validate_the_key),
                message = getString(R.string.pix_insert_all_keys_message_bottom_sheet_impossible_to_validate_the_key),
                labelPrimaryButton = getString(R.string.text_error_update),
                iconButtonEndHeaderContentDescription = getString(R.string.text_close),
                onPrimaryButtonClickListener =
                    object :
                        HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) {
                            viewModel.validateKey(getKeyValue)
                            dialog?.dismiss()
                        }
                    },
                onIconButtonEndHeaderClickListener =
                    object :
                        HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) {
                            dialog?.dismiss()
                        }
                    },
            )
        }
    }

    private fun onUnavailableService(errorMessage: NewErrorMessage?) {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                title = getString(R.string.error_title_service_unavailable),
                message = getString(R.string.pix_insert_all_keys_message_bottom_sheet_unavailable_service),
                labelPrimaryButton = getString(R.string.pix_insert_all_keys_label__primary_button_bottom_sheet_unavailable_service),
                iconButtonEndHeaderContentDescription = getString(R.string.text_close),
                onPrimaryButtonClickListener = getHandlerViewListenerToBack(),
                onIconButtonEndHeaderClickListener = getHandlerViewListenerToBack(),
            )
        }
    }

    private fun getHandlerViewListenerToBack(): HandlerViewBuilderFluiV2.HandlerViewListener {
        return object :
            HandlerViewBuilderFluiV2.HandlerViewListener {
            override fun onClick(dialog: Dialog?) {
                dialog?.dismiss()
                requireActivity().toHomePix()
            }
        }
    }

    private fun onInputError(errorMessage: NewErrorMessage?) {
        val message =
            errorMessage?.message.takeIf { it.isNullOrBlank().not() }
                ?: getString(R.string.pix_insert_all_keys_error_invalid_key)
        binding?.tifKey?.helperText = message
        navigation?.showContent()
    }

    private fun navigateToInsertBankAccount() {
        findNavController().run {
            popBackStack()
            navigate(
                PixInsertAllKeysFragmentDirections
                    .actionPixInsertAllKeysFragmentToPixBankAccountSearchFragment(),
            )
        }
    }
}
