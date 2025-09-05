package br.com.mobicare.cielo.pixMVVM.presentation.key.ui.bankAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.showKeyboard
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentPixBankAccountDocumentBinding
import br.com.mobicare.cielo.databinding.LayoutPixFooterRoundedButtonBinding
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixBankAccountKeyViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixBankAccountDocumentFragment : PixBankAccountBaseFragment(), CieloNavigationListener {

    override val viewModel: PixBankAccountKeyViewModel by sharedViewModel()

    private var _binding: FragmentPixBankAccountDocumentBinding? = null
    private val binding get() = _binding!!

    private var _bindingFooter: LayoutPixFooterRoundedButtonBinding? = null
    private val bindingFooter get() = _bindingFooter!!

    override val toolbarConfigurator get() = buildCollapsingToolbar(
        layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
        title = getString(R.string.pix_key_bank_account_document_title),
        footerView = bindingFooter.root
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixBankAccountDocumentBinding.inflate(inflater, container,false).also {
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
        binding.tifDocument.apply {
            setMask(CieloTextInputField.MaskFormat.CPF_OR_CNPJ)
            setValidators(buildEmptyValidator(), buildInvalidValidator())
            text = viewModel.bankAccount.documentNumber.orEmpty()
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
        if (binding.tifDocument.validate()) {
            setDocument()
            validate()
        }
    }

    private fun setDocument() {
        viewModel.setDocument(
            documentNumber = binding.tifDocument.textInputEditText.text.toString().trim()
        )
    }

    private fun validate() {
        if (viewModel.bankAccount.validateDocument) {
            findNavController().navigate(
                PixBankAccountDocumentFragmentDirections
                    .actionPixBankAccountDocumentFragmentToPixBankAccountRecipientFragment()
            )
        } else {
            binding.tifDocument.apply {
                text = EMPTY
                setError(getString(R.string.pix_key_bank_account_document_error_invalid))
            }
        }
    }

    private fun buildEmptyValidator() = CieloTextInputField.Validator(
        rule = { it.extractedValue.isNotBlank() },
        onResult = { isValid, _ ->
            binding.tifDocument.helperText = if (isValid.not()) {
                getString(R.string.pix_key_bank_account_document_error_blank)
            } else {
                null
            }
        }
    )

    private fun buildInvalidValidator() = CieloTextInputField.Validator(
        rule = {
            ValidationUtils.isCPF(it.extractedValue) || ValidationUtils.isCNPJ(it.extractedValue)
        },
        errorMessage = getString(R.string.pix_key_bank_account_document_error_invalid)
    )

}