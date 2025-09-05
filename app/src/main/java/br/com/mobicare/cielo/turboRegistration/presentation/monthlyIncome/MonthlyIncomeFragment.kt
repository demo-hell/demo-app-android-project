package br.com.mobicare.cielo.turboRegistration.presentation.monthlyIncome

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentMonthlyIncomeBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.turboRegistration.RegistrationUpdateViewModel
import br.com.mobicare.cielo.turboRegistration.analytics.TurboRegistrationAnalytics
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError
import java.math.BigDecimal

class MonthlyIncomeFragment : BaseFragment(), CieloNavigationListener {

    private val binding: FragmentMonthlyIncomeBinding by viewBinding()
    private var navigation: CieloNavigation? = null
    private val registrationViewModel: RegistrationUpdateViewModel by activityViewModels()
    private var rawValue: BigDecimal = BigDecimal.ZERO

    private var isErrorTracked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TurboRegistrationAnalytics.screenViewSelfRegistrationMonthlyIncome()
        setupNavigation()
        setupInputField()
        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            btContinue.setOnClickListener {
                val inputValueText = inputValue.textInputEditText.text.toString().trim()
                rawValue = inputValueText.replace("[,.]".toRegex(), EMPTY_STRING).toBigDecimalOrNull() ?: BigDecimal.ZERO
                if (isFieldValid(inputValueText)) {
                    registrationViewModel.setBilling(rawValue.divide(BigDecimal(ONE_HUNDRED)))
                    if (UserPreferences.getInstance().isLegalEntity) {
                        findNavController().safeNavigate(MonthlyIncomeFragmentDirections.actionNavMonthlyIncomeToNavBankData())
                    } else {
                        findNavController().safeNavigate(MonthlyIncomeFragmentDirections.actionNavMonthlyIncomeToNavLineBusiness())
                    }
                }
            }
        }
    }

    private fun setupInputField() {
        binding.inputValue.apply {
            setMoneyMask()
            setValidators(
                CieloTextInputField.Validator(
                    rule = { it.value.isNotEmpty() && (it.extractedValue.toBigDecimalOrNull() ?: BigDecimal.ZERO) >= BigDecimal.ONE },
                    errorMessage = getString(R.string.error_monthly_income),
                    onResult = { isValid, _ ->
                        binding.btContinue.isButtonEnabled = isValid
                        if (isValid) {
                            isErrorTracked = false
                        }
                        if (isValid.not() && isErrorTracked.not()) {
                            isErrorTracked = true
                            TurboRegistrationAnalytics.displayContentMonthlyIncomeWarning()
                        }
                    }
                )
            )
            validationMode = CieloTextInputField.ValidationMode.TEXT_CHANGED
            this.textInputEditText.setOnEditorActionListener { _, _, _ ->
                clearFocus()
                false
            }
            this.textInputLayout.prefixText = getString(R.string.cifrao)
            this.textInputLayout.setPrefixTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.selector_currency_state_list
                    )
                )
            )
            this.hint = if (UserPreferences.getInstance().isLegalEntity) {
                getString(R.string.hint_monthly_invoice)
            } else {
                getString(R.string.hint_monthly_income)
            }
            this.textInputLayout.setEndIconOnClickListener {
                this.textInputEditText.text = null
                binding.btContinue.isButtonEnabled = false
                this.setError(getString(R.string.error_monthly_income))
            }
        }
    }

    private fun isFieldValid(value: String): Boolean {
        return if (value.isEmpty() && (value.toBigDecimalOrNull() ?: BigDecimal.ZERO) >= BigDecimal.ONE) {
            binding.apply {
                inputValue.setError(getString(R.string.error_monthly_income))
            }
            false
        } else {
            true
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(
                title = getString(R.string.title_your_data),
                isCollapsed = false,
                subtitle = if (UserPreferences.getInstance().isLegalEntity) getString(R.string.subtitle_monthly_invoice) else getString(R.string.subtitle_monthly_income)
            )
            navigation?.showBackButton(isShow = true)
            navigation?.onStepChanged(RegistrationStepError.MONTHLY_INCOME.ordinal)
            navigation?.onAdjustSoftInput(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }
}