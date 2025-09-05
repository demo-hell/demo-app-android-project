package br.com.mobicare.cielo.pix.ui.transfer.agency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.inputtext.CieloInputText
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentPixInsertRecipientDataBinding
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.PIX_BALANCE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_IS_TRUSTED_DESTINATION_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_PAYEE_ARGS
import br.com.mobicare.cielo.pix.domain.ManualPayee
import br.com.mobicare.cielo.pix.enums.BankAccountTypeEnum
import br.com.mobicare.cielo.pix.enums.BeneficiaryTypeEnum

class PixInsertRecipientDataFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentPixInsertRecipientDataBinding? = null
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
        FragmentPixInsertRecipientDataBinding.inflate(
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
            requireActivity().showKeyboard(itCompanyOrPersonName)

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

                    if (it.beneficiaryType == BeneficiaryTypeEnum.CNPJ.key) {
                        val hint = if (isTrustedDestination)
                            R.string.hint_text_recipient_name_pj_trusted_destination
                        else
                            R.string.hint_text_recipient_name_pj

                        val title = if (isTrustedDestination)
                            R.string.text_recipient_name_pj_title_trusted_destination
                        else
                            R.string.text_recipient_name_pj_title

                        setupBeneficiaryInfo(
                                it.documentNumber,
                                R.string.mask_cnpj_step4,
                                R.string.cnpj_title,
                                title,
                                hint
                        )
                    } else {
                        val hint = if (isTrustedDestination)
                            R.string.hint_text_recipient_name_pf_trusted_destination
                        else
                            R.string.hint_text_recipient_name_pj

                        val title = if (isTrustedDestination)
                            R.string.text_recipient_name_pf_title_trusted_destination
                        else
                            R.string.text_recipient_name_pf_title

                        setupBeneficiaryInfo(
                                it.documentNumber,
                                R.string.mask_cpf_step4,
                                R.string.cpf_title,
                                title,
                                hint
                        )
                    }
                }
            }
        }

    }

    private fun setupBeneficiaryInfo(
        documentNumber: String?,
        @StringRes documentMask: Int,
        @StringRes documentTitle: Int,
        @StringRes recipientTitle: Int,
        @StringRes fieldHint: Int
    ) {
        binding?.apply {
            includeTransferDetails.tvCpfOrCnpjNumber.text = addMaskCPForCNPJ(documentNumber, getString(documentMask))
            includeTransferDetails.tvCpforCnpjTitle.text = getString(documentTitle)
            tvRecipientTitle.text = getString(recipientTitle)
            itCompanyOrPersonName.setHint(getString(fieldHint))
            includeTransferDetails.llCpfOrCnpj.visible()
        }
    }

    private fun setupListeners() {
        binding?.apply {
            val companyOrPersonNameChangeListener = object : CieloInputText.TextChangeListener {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    navigation?.enableButton(enableButton())
                }
            }
            itCompanyOrPersonName.setOnTextChangeListener(companyOrPersonNameChangeListener)
        }
    }

    private fun enableButton(): Boolean {
        return binding?.itCompanyOrPersonName?.getText()?.let {
            it.length >= FOUR
        } == true
    }

    override fun onButtonClicked(labelButton: String) {
        super.onButtonClicked(labelButton)

        findNavController().navigate(
            if (isTrustedDestination)
                PixInsertRecipientDataFragmentDirections.actionPixInsertRecipientDataFragmentToPixMyLimitsAddNewTrustedDestinationFragment(
                    setupRequestObject(),
                    null,
                    false
                )
            else
                PixInsertRecipientDataFragmentDirections.actionPixInsertRecipientDataFragmentToPixTransferSummaryFragment(
                    null,
                    balance ?: DEFAULT_BALANCE, setupRequestObject()
                )
        )
    }

    private fun setupRequestObject(): ManualPayee {
        return ManualPayee(
            bankName = payeeRequest?.bankName,
            bankAccountNumber = payeeRequest?.bankAccountNumber,
            bankAccountType = payeeRequest?.bankAccountType,
            bankBranchNumber = payeeRequest?.bankBranchNumber,
            beneficiaryType = payeeRequest?.beneficiaryType,
            documentNumber = payeeRequest?.documentNumber,
            ispb = payeeRequest?.ispb,
            name = binding?.itCompanyOrPersonName?.getText()
        )
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