package br.com.mobicare.cielo.pix.ui.transfer.agency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.getTitlePix
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentPixSelectAccountTypeBinding
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.ManualPayee
import br.com.mobicare.cielo.pix.enums.BankAccountTypeEnum
import br.com.mobicare.cielo.pix.model.PixBank

class PixSelectAccountTypeFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentPixSelectAccountTypeBinding? = null
    private var navigation: CieloNavigation? = null
    private var accountTypeSelected = BankAccountTypeEnum.CURRENT_ACCOUNT.key
    private val bank: PixBank? by lazy {
        arguments?.getParcelable(PIX_BANK_ARGS)
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
        FragmentPixSelectAccountTypeBinding.inflate(inflater, container, false)
                .also {
                    binding = it
                }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupLayout()
        setupListeners()
    }

    private fun setupListeners() {
        binding?.accountTypes?.radioGroupAccountType?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCheckingAccount -> accountTypeSelected =
                    BankAccountTypeEnum.CURRENT_ACCOUNT.key
                R.id.rbSavingsAccount -> accountTypeSelected =
                    BankAccountTypeEnum.SAVINGS_ACCOUNT.key
                R.id.rbPaymentAccount -> accountTypeSelected =
                    BankAccountTypeEnum.PAYMENT_ACCOUNT.key
            }
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(getTitlePix(isTrustedDestination)))
            navigation?.setTextButton(getString(R.string.text_pix_next))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.enableButton(isEnabled = true)
            navigation?.showFirstButton(isShow = false)
            navigation?.showHelpButton(false)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupLayout() {
        bank?.let {
            binding?.includeTransferDetails?.tvInstitutionName?.text = it.name
        }
    }

    override fun onButtonClicked(labelButton: String) {
        super.onButtonClicked(labelButton)

        navigation?.saveData(Bundle().apply {
            this.putString(PIX_AGENCY_NUMBER_ARGS, EMPTY)
        })

        findNavController().navigate(
            PixSelectAccountTypeFragmentDirections.actionPixSelectAccountTypeFragmentToPixInsertAccountDataFragment(
                setupRequestObject(),
                balance ?: DEFAULT_BALANCE,
                isTrustedDestination
            )
        )
    }

    private fun setupRequestObject(): ManualPayee {
        return ManualPayee(
            bankName = bank?.name,
            bankAccountNumber = null,
            bankAccountType = accountTypeSelected,
            bankBranchNumber = null,
            beneficiaryType = null,
            documentNumber = null,
            ispb = bank?.ispb?.toInt()
        )
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().hideSoftKeyboard()
        return super.onBackButtonClicked()
    }
}