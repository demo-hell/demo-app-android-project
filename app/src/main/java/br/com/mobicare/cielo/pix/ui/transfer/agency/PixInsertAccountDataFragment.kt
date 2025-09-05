package br.com.mobicare.cielo.pix.ui.transfer.agency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.inputtext.CieloInputText
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.getAccountFormatted
import br.com.mobicare.cielo.commons.utils.getTitlePix
import br.com.mobicare.cielo.databinding.FragmentPixInsertAccountDataBinding
import br.com.mobicare.cielo.extensions.clearCNPJMask
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.ManualPayee
import br.com.mobicare.cielo.pix.enums.BankAccountTypeEnum

class PixInsertAccountDataFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentPixInsertAccountDataBinding? = null
    private val payeeRequest: ManualPayee? by lazy {
        arguments?.getParcelable(PIX_PAYEE_ARGS)
    }

    private val balance: String? by lazy {
        arguments?.getString(PIX_BALANCE_ARGS)
    }

    private val isTrustedDestination: Boolean by lazy {
        arguments?.getBoolean(PIX_IS_TRUSTED_DESTINATION_ARGS, false) ?: false
    }

    private var navigation: CieloNavigation? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View =
            FragmentPixInsertAccountDataBinding.inflate(
                    inflater, container, false
            ).also {
                binding = it
            }.root

    override fun onResume() {
        super.onResume()

        navigation?.getSavedData()?.let { bundle ->
            bundle.getString(PIX_AGENCY_NUMBER_ARGS)?.let {
                binding?.etAgencyNumber?.setText(it)
            }
        }
    }

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
        payeeRequest?.let {
            binding?.apply {
                includeTransferDetails.apply {
                    tvInstitutionName.text = it.bankName
                    llInstitutionName.visible()

                    tvAccountType.text = BankAccountTypeEnum.acronymToName(it.bankAccountType)
                    llAccountType.visible()
                }
            }
        }
    }

    private fun setupListeners() {
        setUpAccountNumberListener()
        setupAgencyNumberListener()
    }

    private fun setUpAccountNumberListener() {
        binding?.apply {
            val accountNumberListener = object : CieloInputText.TextChangeListener {
                var wasMaskApplied = false

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!wasMaskApplied) {
                        val accountNumberFormatted = s.toString().getAccountFormatted()
                        wasMaskApplied = true
                        etAccountNumber.setText(accountNumberFormatted)
                    } else {
                        wasMaskApplied = false
                    }
                    navigation?.enableButton(enableButton())
                    etAccountNumber.setSelection(etAccountNumber.getText().length)
                }
            }
            etAccountNumber.setOnTextChangeListener(accountNumberListener)
        }
    }

    private fun setupAgencyNumberListener() {
        val agencyNumberTextChangeListener = object : CieloInputText.TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                navigation?.enableButton(enableButton())
            }
        }

         binding?.etAgencyNumber?.setOnTextChangeListener(agencyNumberTextChangeListener)
    }

    override fun onButtonClicked(labelButton: String) {
        super.onButtonClicked(labelButton)

        navigation?.saveData(Bundle().apply {
            this.putString(PIX_AGENCY_NUMBER_ARGS, binding?.etAgencyNumber?.getText())
        })

        findNavController().navigate(
            PixInsertAccountDataFragmentDirections.actionPixInsertAccountDataFragmentToPixInsertDocumentDataFragment(
                setupRequestObject(), balance
                    ?: DEFAULT_BALANCE,
                isTrustedDestination
            )
        )
    }

    private fun setupRequestObject(): ManualPayee {
        return ManualPayee(
            bankName = payeeRequest?.bankName,
            bankAccountNumber = binding?.etAccountNumber?.getText().clearCNPJMask(),
            bankAccountType = payeeRequest?.bankAccountType,
            bankBranchNumber = binding?.etAgencyNumber?.getText().clearCNPJMask(),
            beneficiaryType = null,
            documentNumber = null,
            ispb = payeeRequest?.ispb,
            name = null
        )
    }

    private fun enableButton(): Boolean {
        return binding?.etAgencyNumber?.getText()?.let {
            it.length >= ONE
        } == true && binding?.etAccountNumber?.getText()?.let {
            it.length >= ONE
        } == true
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}