package br.com.mobicare.cielo.pix.ui.transfer.agency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.helpers.InputTextHelper
import br.com.mobicare.cielo.commons.helpers.InputTextHelper.Companion.isValidDocument
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.getAccountFormatted
import br.com.mobicare.cielo.commons.utils.getTitlePix
import br.com.mobicare.cielo.commons.utils.showKeyboard
import br.com.mobicare.cielo.databinding.FragmentPixInsertDocumentDataBinding
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.PIX_BALANCE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_IS_TRUSTED_DESTINATION_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_PAYEE_ARGS
import br.com.mobicare.cielo.pix.domain.ManualPayee
import br.com.mobicare.cielo.pix.enums.BankAccountTypeEnum
import br.com.mobicare.cielo.pix.enums.BeneficiaryTypeEnum

class PixInsertDocumentDataFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentPixInsertDocumentDataBinding? = null
    private var navigation: CieloNavigation? = null
    private val payeeRequest: ManualPayee? by lazy {
        arguments?.getParcelable(PIX_PAYEE_ARGS)
    }

    private val balance: String? by lazy {
        arguments?.getString(PIX_BALANCE_ARGS)
    }

    private val isTrustedDestination: Boolean by lazy {
        arguments?.getBoolean(PIX_IS_TRUSTED_DESTINATION_ARGS, false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentPixInsertDocumentDataBinding.inflate(
                inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupLayout()
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(getTitlePix(isTrustedDestination)))
            navigation?.setTextButton(getString(R.string.text_pix_next))
            navigation?.showButton(true)
            navigation?.showContainerButton(isShow = true)
            navigation?.showFirstButton(isShow = false)
            navigation?.enableButton(false)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupLayout() {
        binding?.apply {
            requireActivity().showKeyboard(etDocumentNumber)
            if (isTrustedDestination)
                tvTitle.text = getString(R.string.text_document_title_trusted_destination)

            payeeRequest?.let {
                includeTransferDetails.apply {
                    tvInstitutionName.text = it.bankName
                    llInstitutionName.visible()

                    tvAccountType.text = BankAccountTypeEnum.acronymToName(it.bankAccountType)
                    llAccountType.visible()

                    tvAgencyNumber.text = it.bankBranchNumber
                    llAgencyNumber.visible()

                    tvAccountWithDigit.text = it.bankAccountNumber?.getAccountFormatted()
                    llTvAccountWithDigit.visible()
                }
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            InputTextHelper.cPForCNPJInput(
                    etDocumentNumber,
                    getString(R.string.incorrect_document_error_message)
            ) {
                val documentIsValid = etDocumentNumber.getText().isValidDocument()
                navigation?.enableButton(documentIsValid)
            }
        }
    }

    override fun onButtonClicked(labelButton: String) {
        super.onButtonClicked(labelButton)

        findNavController().navigate(
            PixInsertDocumentDataFragmentDirections.actionPixInsertDocumentDataFragmentToPixInsertRecipientDataFragment(
                setupRequestObject(),
                balance
                    ?: DEFAULT_BALANCE,
                isTrustedDestination
            )
        )
    }

    private fun setupRequestObject(): ManualPayee {
        val documentNumber = binding?.etDocumentNumber?.getText()?.filter { it.isLetterOrDigit() }

        return ManualPayee(
            bankName = payeeRequest?.bankName,
            bankAccountNumber = payeeRequest?.bankAccountNumber,
            bankAccountType = payeeRequest?.bankAccountType,
            bankBranchNumber = payeeRequest?.bankBranchNumber,
            documentNumber = documentNumber,
            ispb = payeeRequest?.ispb,
            name = null,
            beneficiaryType = if (documentNumber?.length == ELEVEN)
                BeneficiaryTypeEnum.CPF.key
            else
                BeneficiaryTypeEnum.CNPJ.key
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}