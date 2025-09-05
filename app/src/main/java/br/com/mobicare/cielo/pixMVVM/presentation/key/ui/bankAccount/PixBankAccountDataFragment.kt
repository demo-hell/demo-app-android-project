package br.com.mobicare.cielo.pixMVVM.presentation.key.ui.bankAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.databinding.FragmentPixBankAccountDataBinding
import br.com.mobicare.cielo.databinding.LayoutPixFooterRoundedButtonBinding
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixBankAccountKeyViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixBankAccountDataFragment : PixBankAccountBaseFragment(), CieloNavigationListener {

    override val viewModel: PixBankAccountKeyViewModel by sharedViewModel()

    private var _binding: FragmentPixBankAccountDataBinding? = null
    private val binding get() = _binding!!

    private var _bindingFooter: LayoutPixFooterRoundedButtonBinding? = null
    private val bindingFooter get() = _bindingFooter!!

    override val toolbarConfigurator get() = buildCollapsingToolbar(
        layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
        title = getString(R.string.pix_key_bank_account_data_title),
        footerView = bindingFooter.root
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixBankAccountDataBinding.inflate(inflater, container,false).also {
        _binding = it
        _bindingFooter = LayoutPixFooterRoundedButtonBinding.inflate(inflater, container, false)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInputTextFieldViews()
        setupInformationTextViews()
        setupNextButtonView()
    }

    override fun onDestroyView() {
        _binding = null
        _bindingFooter = null
        super.onDestroyView()
    }

    private fun setupInputTextFieldViews() {
        setupAgencyTextInputView()
        setupAccountTextInputView()
        setupAccountDigitTextInputView()
    }

    private fun setupAgencyTextInputView() {
        binding.tifAgency.apply {
            textInputEditText.isSaveEnabled = false
            setValidators(
                buildValidator(this, R.string.pix_key_bank_account_data_error_agency)
            )
            text = viewModel.bankAccount.bankBranchNumber.orEmpty()
        }
    }

    private fun setupAccountTextInputView() {
        binding.tifAccount.apply {
            textInputEditText.isSaveEnabled = false
            setValidators(
                buildValidator(this, R.string.pix_key_bank_account_data_error_account)
            )
            text = viewModel.bankAccount.bankAccountNumber.orEmpty()
        }
    }

    private fun setupAccountDigitTextInputView() {
        binding.tifAccountDigit.apply {
            textInputEditText.isSaveEnabled = false
            setValidators(
                buildValidator(
                    this,
                    R.string.pix_key_bank_account_data_error_account_digit,
                    R.string.pix_key_bank_account_data_helper_account_digit
                )
            )
            helperText = getString(R.string.pix_key_bank_account_data_helper_account_digit)
            text = viewModel.bankAccount.bankAccountDigit.orEmpty()
        }
    }

    private fun setupInformationTextViews() {
        binding.apply {
            tvBank.text = information.bankName
            tvAccountType.text = information.accountType
        }
    }

    private fun setupNextButtonView() {
        bindingFooter.button.setOnClickListener(::onNextTap)
    }

    private fun onNextTap(v: View) {
        viewModel.setBankAccountData(
            branchNumber = binding.tifAgency.textInputEditText.text.toString(),
            accountNumber = binding.tifAccount.textInputEditText.text.toString(),
            accountDigit = binding.tifAccountDigit.textInputEditText.text.toString(),
        )
        validate()
    }

    private fun validate() {
        if (viewModel.bankAccount.validateAccountData) {
            findNavController().navigate(
                PixBankAccountDataFragmentDirections
                    .actionPixBankAccountDataFragmentToPixBankAccountDocumentFragment()
            )
        } else {
            binding.apply {
                tifAgency.validate()
                tifAccount.validate()
                tifAccountDigit.validate()
            }
        }
    }

    private fun buildValidator(
        inputView: CieloTextInputField,
        @StringRes errorMessageRes: Int,
        @StringRes informativeMessageRes: Int? = null
    ) = CieloTextInputField.Validator(
        rule = { it.value.isNotBlank() },
        onResult = { isValid, _ ->
            inputView.helperText = if (isValid.not()) {
                getString(errorMessageRes)
            } else {
                informativeMessageRes?.let { getString(it) }
            }
        }
    )

}