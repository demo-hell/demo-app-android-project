package br.com.mobicare.cielo.turboRegistration.presentation.bankData

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.FOUR
import br.com.cielo.libflue.util.THREE
import br.com.cielo.libflue.util.ZERO_POINT_SIX_FLOAT
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.DASH
import br.com.mobicare.cielo.commons.constants.ONE_FLOAT
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED_LONG
import br.com.mobicare.cielo.commons.constants.ZERO_TEXT
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.setMargins
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentBankDataBinding
import br.com.mobicare.cielo.databinding.ItemOperationBinding
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.formatBankName
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.turboRegistration.RegistrationUpdateViewModel
import br.com.mobicare.cielo.turboRegistration.analytics.TurboRegistrationAnalytics
import br.com.mobicare.cielo.turboRegistration.data.model.request.PaymentAccountRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Bank
import br.com.mobicare.cielo.turboRegistration.domain.model.Operation
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel


class BankDataFragment : BaseFragment(), CieloNavigationListener {

    private val binding: FragmentBankDataBinding by viewBinding()
    private val viewModel: BankInfoViewModel by viewModel()
    private val registrationViewModel: RegistrationUpdateViewModel by activityViewModels()
    private var navigation: CieloNavigation? = null
    private var isSavingsAccountChecked = false
    private var currentSelectedBank: Bank? = null
    private var operationsFullList: List<Operation> = emptyList()
    private var operationBottomSheet: CieloListBottomSheet<Operation>? = null
    private var paymentAccountRequest: PaymentAccountRequest? = null

    private val primaryColor = br.com.cielo.libflue.R.color.brand_400
    private val errorColor = br.com.cielo.libflue.R.color.red_500

    private var errorTracked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doWhenResumed {
            TurboRegistrationAnalytics.screenViewSelfRegistrationBankData()
        }
        viewModel.getCaixaOperations()
        paymentAccountRequest = registrationViewModel.getPaymentAccount()
        setupNavigation()
        makeScreen()
        setListeners()
        addObserver()
    }

    private fun TextInputLayout.setErrorState() {
        setBoxStrokeColorStateList(getBoxStrokeColorState(errorColor))
        defaultHintTextColor = getColorState(errorColor)
    }

    private fun TextInputLayout.setDisabledState() {
        boxBackgroundColor = getColor(R.color.color_EFF1F3)
    }

    private fun TextInputLayout.setNormalState() {
        boxBackgroundColor = getColor(android.R.color.transparent)
        setBoxStrokeColorStateList(getBoxStrokeColorState(R.color.border_neutral))
        setBoxStrokeColorStateList(ColorStateList.valueOf(getColor(R.color.selector_currency_state_list)))
        defaultHintTextColor = getColorState(R.color.neutral_600)
    }

    private fun makeScreen() {
        binding.bankDataName.apply {
            isLongClickable = false
            isFocusable = false
            editText?.isFocusable = false
            editText?.isLongClickable = false
        }

        binding.bankDataOperation.apply {
            isLongClickable = false
            isFocusable = false
            editText?.isFocusable = false
            editText?.isLongClickable = false
            operationGone()
        }

        binding.apply {
            bankDataName.setNormalState()
            val selectedOperation = registrationViewModel.getOperation()
            selectedOperation?.let {
                bankDataOperation.editText?.setText(selectedOperation.label?.take(FOUR)?.trim())
                setupOperationText(it)
            }
            bankDataOperation.isEnabled = selectedOperation != null
            if (bankDataOperation.isEnabled) {
                bankDataOperation.setNormalState()
                operationVisible()
            } else {
                bankDataOperation.setDisabledState()
                operationGone()
            }
            bankDataAgency.setNormalState()
            bankDataAccountNumber.setNormalState()
            bankDataAccountDigit.setNormalState()
        }
    }

    private fun operationGone() {
        binding.bankDataOperation.gone()
        binding.operationGuideline.setGuidelinePercent(ONE_FLOAT)
        binding.bankDataAgency.setMargins(marginEnd = R.dimen.dimen_0dp)
    }

    private fun operationVisible() {
        binding.bankDataOperation.visible()
        binding.operationGuideline.setGuidelinePercent(ZERO_POINT_SIX_FLOAT)
        binding.bankDataAgency.setMargins(marginEnd = R.dimen.dimen_16dp)
    }

    private fun addObserver() {
        viewModel.operations.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is RegistrationResource.Loading -> {}
                is RegistrationResource.Success -> {
                    operationsFullList = resource.data
                }

                is RegistrationResource.Error -> {}
                RegistrationResource.Empty -> {}
            }
        }
    }

    private fun getBoxStrokeColorState(@ColorRes colorId: Int) = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled)
        ),
        intArrayOf(
            getColor(colorId),
            getColor(R.color.border_neutral)
        )
    )

    private fun getColorState(@ColorRes resId: Int) = ColorStateList.valueOf(getColor(resId))

    private fun getColor(@ColorRes resId: Int) = ContextCompat.getColor(requireContext(), resId)

    private fun setListeners() {
        binding.bankDataName.editText?.setOnClickListener {
            setupBottomSheet()
        }

        binding.accountTypeRadio.setOnCheckedChangeListener { _, checkedId ->
            isSavingsAccountChecked = checkedId == R.id.savingsAccount
            registrationViewModel.saveOperation(null)
            binding.bankDataOperation.editText?.setText(null)
            binding.bankDataNameHelper.setText(null)
            if ((currentSelectedBank?.code ?: EMPTY) == CAIXA_CODE) {
                binding.bankDataNameHelper.text = getString(R.string.label_bank_operation_caixa)
            }
            validateButtonContinue()
        }
        binding.bankDataOperation.editText?.setOnClickListener {
            setupBottomSheetOperation()
        }

        binding.bankDataAgency.editText?.setOnFocusChangeListener { _, focused ->
            if (focused.not() && binding.bankDataAgency.editText?.text.isNullOrEmpty()) {
                binding.bankDataAgencyHelper.apply {
                    visible()
                    text = getString(R.string.error_empty_agency)
                }
                binding.bankDataAgency.setErrorState()
            } else {
                binding.bankDataAgencyHelper.apply {
                    text = null
                    gone()
                }
                binding.bankDataAgency.setNormalState()
            }
            validateButtonContinue()
        }
        binding.bankDataAgency.editText?.doAfterTextChanged { validateButtonContinue() }

        binding.bankDataAccountNumber.editText?.setOnFocusChangeListener { _, focused ->
            if (focused.not() && binding.bankDataAccountNumber.editText?.text.isNullOrEmpty()) {
                binding.bankDataAccountNumberHelper.apply {
                    visible()
                    text = getString(R.string.error_empty_account)
                }
                binding.bankDataAccountNumber.setErrorState()
            } else {
                binding.bankDataAccountNumberHelper.apply {
                    text = null
                    gone()
                }
                binding.bankDataAccountNumber.setNormalState()
            }
            validateButtonContinue()
        }
        binding.bankDataAccountNumber.editText?.doAfterTextChanged { validateButtonContinue() }

        binding.bankDataAccountDigit.editText?.setOnFocusChangeListener { _, focused ->
            if (focused.not() && binding.bankDataAccountDigit.editText?.text.isNullOrEmpty()) {
                binding.bankDataAccountDigitHelper.apply {
                    gone()
                    text = DASH
                }
                binding.bankDataAccountDigit.setErrorState()
            } else {
                binding.bankDataAccountDigitHelper.apply {
                    text = null
                    gone()
                }
                binding.bankDataAccountDigit.setNormalState()
            }
            validateButtonContinue()
        }
        binding.bankDataAccountDigit.editText?.doAfterTextChanged { validateButtonContinue() }


        binding.btContinue.setOnClickListener {
            if (isFieldsValid()) {
                val selectedOperation = registrationViewModel.getOperation()
                registrationViewModel.setPaymentAccount(
                    account = binding.bankDataAccountNumber.editText?.text.toString(),
                    agency = binding.bankDataAgency.editText?.text.toString(),
                    accountDigit = binding.bankDataAccountDigit.editText?.text.toString(),
                    bankCode = currentSelectedBank?.code ?: ZERO_TEXT,
                    selectedOperation = selectedOperation,
                    isSavingsAccount = isSavingsAccountChecked
                )
                TurboRegistrationAnalytics.clickSelfRegistrationFinishButton()
                findNavController().safeNavigate(BankDataFragmentDirections.actionNavBankDataToNavRegistration())
            }
        }
    }

    private fun setupBottomSheet() {
        val bottomSheet = BanksBottomSheetFragment.newInstance { selectedBank ->
            currentSelectedBank = selectedBank
            binding.bankDataName.editText?.setText(currentSelectedBank?.name.capitalizeWords().formatBankName())
            setupOperation(currentSelectedBank?.code ?: ZERO_TEXT)
        }
        bottomSheet.show(childFragmentManager, BANKS_BOTTOM_SHEET)
    }

    private fun setupOperation(bankCode: String) {
        binding.bankDataOperation.apply {
            isEnabled = bankCode == CAIXA_CODE
            if (isEnabled) setNormalState() else setDisabledState()
            isLongClickable = false
            isFocusable = false
            editText?.isFocusable = false
            editText?.isLongClickable = false
            editText?.setText(null)
        }

        registrationViewModel.saveOperation(null)

        if (bankCode == CAIXA_CODE) {
            binding.bankDataNameHelper.visible()
            binding.bankDataNameHelper.text = getString(R.string.label_bank_operation_caixa)
            operationVisible()
        } else {
            operationGone()
            binding.accountTypeRadio.visible()
            binding.bankDataNameHelper.gone()
            binding.bankDataNameHelper.text = null
        }
        validateButtonContinue()
    }

    private fun setupBottomSheetOperation() {
        operationBottomSheet?.dismiss()
        operationBottomSheet = CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.label_select_operation)
            ),
            layoutItemRes = R.layout.item_operation,
            data = operationsFullList.filter {
                if (isSavingsAccountChecked) {
                    it.isSavingsAccount == true
                } else {
                    it.isSavingsAccount?.not() == true
                }
            },
            onViewBound = { operation, isSelected, itemView ->
                val operationItemBinding =
                    ItemOperationBinding.bind(itemView)
                operationItemBinding.apply {
                    rbChoose.isSelected = isSelected
                    tvOperation.text = operation.label
                }
            },
            onItemClicked = { operation, position, bottomSheet ->
                binding.bankDataOperation.editText?.setText(operation.label?.take(FOUR)?.trim())
                registrationViewModel.saveOperation(operation)
                bottomSheet.updateSelectedPosition(position)
                setupOperationText(operation)
                validateButtonContinue()
                bottomSheet.dismiss()
            },
            dividerItemDecoration = DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        requireActivity().hideSoftKeyboard()
        binding.apply {
            bankDataAgency.clearFocus()
            bankDataAccountNumber.clearFocus()
            bankDataAccountDigit.clearFocus()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            operationBottomSheet?.show(childFragmentManager, OPERATION_BOTTOM_SHEET)
        }, ONE_HUNDRED_LONG)
    }

    private fun setupOperationText(itOperation: Operation) {
        binding.bankDataNameHelper.visible()
        binding.bankDataNameHelper.text = getString(R.string.label_message_bank_operation, itOperation.label)
    }

    private fun isFieldsValid(): Boolean {
        binding.apply {
            return validateFields(
                bankDataName.editText?.text.toString(),
                bankDataAgency.editText?.text.toString(),
                bankDataAccountNumber.editText?.text.toString(),
                bankDataAccountDigit.editText?.text.toString(),
                bankDataOperation.editText?.text.toString()
            )
        }
    }

    private fun validateFields(
        bankCode: String,
        agency: String,
        account: String,
        accountDigit: String,
        operation: String
    ): Boolean {
        var isValid = true

        binding.apply {
            if (bankCode.isBlank()) {
                bankDataNameHelper.apply {
                    visible()
                    text = getString(R.string.error_empty_bank)
                }
                isValid = false
            }

            if (account.isBlank()) {
                bankDataAccountNumberHelper.apply {
                    visible()
                    text = getString(R.string.error_empty_account)
                }
                isValid = false
            }

            if (agency.isBlank()) {
                bankDataAgencyHelper.apply {
                    visible()
                    text = getString(R.string.error_empty_agency)
                }
                isValid = false
            }

            if (accountDigit.isBlank()) {
                bankDataAccountDigitHelper.apply {
                    visible()
                    text = EMPTY
                }
                isValid = false
            }

            if (bankDataOperation.isEnabled) {
                if (operation.isBlank()) {
                    bankDataOperationHelper.apply {
                        visible()
                        text = getString(R.string.error_empty_operation)
                    }
                    isValid = false
                }
            }
        }
        return isValid
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(
                title = getString(R.string.title_your_data),
                isCollapsed = false,
                subtitle = getString(R.string.subtitle_bank_data)
            )
            navigation?.showBackButton(isShow = true)
            navigation?.onStepChanged(if (UserPreferences.getInstance().isLegalEntity) THREE else RegistrationStepError.BANK.ordinal)
            navigation?.onAdjustSoftInput(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    private fun validateButtonContinue() {
        val hasAlertsErrors = hasAlertsErrors()
        if (hasAlertsErrors && errorTracked.not()) {
            errorTracked = true
            TurboRegistrationAnalytics.displayContentBankDataWarning()
        }
        if (hasAlertsErrors.not()) {
            errorTracked = false
        }
        binding.btContinue.isButtonEnabled = hasEmptyFields().not() && hasAlertsErrors.not()
    }

    private fun hasEmptyFields(): Boolean {
        binding.apply {
            return bankDataName.editText?.text.isNullOrEmpty()
                    || bankDataAgency.editText?.text.isNullOrEmpty()
                    || (bankDataOperation.isEnabled && bankDataOperation.editText?.text.isNullOrEmpty())
                    || bankDataAccountNumber.editText?.text.isNullOrEmpty()
                    || bankDataAccountDigit.editText?.text.isNullOrEmpty()
        }
    }

    private fun hasAlertsErrors(): Boolean {
        binding.apply {
            return (bankDataNameHelper.text.isNullOrEmpty().not() && bankDataOperation.isEnabled.not())
                    || bankDataAgencyHelper.text.isNullOrEmpty().not()
                    || (bankDataOperation.isEnabled && bankDataOperationHelper.text.isNullOrEmpty().not())
                    || bankDataAccountNumberHelper.text.isNullOrEmpty().not()
                    || bankDataAccountDigitHelper.text.isNullOrEmpty().not()
        }
    }

    companion object {
        const val BANKS_BOTTOM_SHEET = "banks_bottom_sheet"
        const val OPERATION_BOTTOM_SHEET = "banks_bottom_sheet"
        const val CAIXA_CODE = "104"
    }
}