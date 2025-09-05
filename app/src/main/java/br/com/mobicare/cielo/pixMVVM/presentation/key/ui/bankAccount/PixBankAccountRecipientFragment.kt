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
import br.com.mobicare.cielo.databinding.FragmentPixBankAccountRecipientBinding
import br.com.mobicare.cielo.databinding.LayoutPixFooterRoundedButtonBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixBeneficiaryType
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixBankAccountKeyViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixBankAccountRecipientFragment : PixBankAccountBaseFragment(), CieloNavigationListener {

    override val viewModel: PixBankAccountKeyViewModel by sharedViewModel()

    private var _binding: FragmentPixBankAccountRecipientBinding? = null
    private val binding get() = _binding!!

    private var _bindingFooter: LayoutPixFooterRoundedButtonBinding? = null
    private val bindingFooter get() = _bindingFooter!!

    override val toolbarConfigurator get() = buildCollapsingToolbar(
        layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
        title = getString(textResources.title),
        footerView = bindingFooter.root
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixBankAccountRecipientBinding.inflate(inflater, container,false).also {
        _binding = it
        _bindingFooter = LayoutPixFooterRoundedButtonBinding.inflate(inflater, container, false)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInputTextFieldView()
        setupInformationTextViews()
        setupNextButtonView()
    }

    override fun onDestroyView() {
        _binding = null
        _bindingFooter = null
        super.onDestroyView()
    }

    private fun setupInputTextFieldView() {
        binding.tifRecipient.apply {
            hint = getString(textResources.hint)
            text = viewModel.bankAccount.recipientName.orEmpty()
            setValidators(
                CieloTextInputField.Validator(
                    rule = { it.value.isNotBlank() },
                    onResult = { isValid, _ ->
                        helperText = if (isValid.not()) getString(textResources.error) else null
                    }
                )
            )
        }
    }

    private fun setupInformationTextViews() {
        binding.apply {
            tvBank.text = information.bankName
            tvAccountType.text = information.accountType
            tvAgency.text = information.bankBranchName
            tvAccount.text = information.bankAccountNumber
            tvAccountDigit.text = information.bankAccountDigit
        }
    }

    private fun setupNextButtonView() {
        bindingFooter.button.setOnClickListener(::onNextTap)
    }

    private fun onNextTap(v: View) {
        if (binding.tifRecipient.validate()) {
            viewModel.setRecipient(binding.tifRecipient.textInputEditText.text.toString())
            validate()
        }
    }

    private fun validate() {
        if (viewModel.bankAccount.validateRecipient) {
            findNavController().safeNavigate(
                PixBankAccountRecipientFragmentDirections.actionPixBankAccountRecipientFragmentToPixTransferAmountFragment(
                    null, viewModel.bankAccount
                )
            )
        } else {
            binding.tifRecipient.apply {
                text = EMPTY
                setError(getString(textResources.error))
            }
        }
    }

    private val textResources get() =
        if (viewModel.bankAccount.beneficiaryType == PixBeneficiaryType.CNPJ) {
            TextResources(
                title = R.string.pix_key_bank_account_recipient_title_cnpj,
                hint = R.string.pix_key_bank_account_recipient_hint_cnpj,
                error = R.string.pix_key_bank_account_recipient_error_blank_cnpj
            )
        } else {
            TextResources(
                title = R.string.pix_key_bank_account_recipient_title_cpf,
                hint = R.string.pix_key_bank_account_recipient_hint_cpf,
                error = R.string.pix_key_bank_account_recipient_error_blank_cpf
            )
        }

    data class TextResources(
        @StringRes val title: Int,
        @StringRes val hint: Int,
        @StringRes val error: Int
    )

}