package br.com.mobicare.cielo.pixMVVM.presentation.key.ui.bankAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.button.CieloBaseRadioButton
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.databinding.FragmentPixBankAccountTypeBinding
import br.com.mobicare.cielo.databinding.LayoutPixFooterRoundedButtonBinding
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixBankAccountType
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixBankAccountKeyViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixBankAccountTypeFragment : PixBankAccountBaseFragment(), CieloNavigationListener {

    override val viewModel: PixBankAccountKeyViewModel by sharedViewModel()

    private var _binding: FragmentPixBankAccountTypeBinding? = null
    private val binding get() = _binding!!

    private var _bindingFooter: LayoutPixFooterRoundedButtonBinding? = null
    private val bindingFooter get() = _bindingFooter!!

    override val toolbarConfigurator get() = buildCollapsingToolbar(
        layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
        title = getString(R.string.pix_key_bank_account_type_title),
        footerView = bindingFooter.root
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixBankAccountTypeBinding.inflate(inflater, container,false).also {
        _binding = it
        _bindingFooter = LayoutPixFooterRoundedButtonBinding.inflate(inflater, container, false)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadioGroupView()
        setupBankTextView()
        setupNextButtonView()
    }

    override fun onDestroyView() {
        _binding = null
        _bindingFooter = null
        super.onDestroyView()
    }

    private fun setupRadioGroupView() {
        binding.radioGroup.apply {
            setOnItemChecked(::onItemChecked)
            setCheckedByTag(viewModel.bankAccount.bankAccountType?.key ?: PixBankAccountType.CHECKING_ACCOUNT.key)
        }
    }

    private fun setupBankTextView() {
        binding.tvBank.text = information.bankName
    }

    private fun setupNextButtonView() {
        bindingFooter.button.setOnClickListener(::onNextTap)
    }

    private fun onItemChecked(radioButton: CieloBaseRadioButton) {
        viewModel.setSelectedAccountType(
            PixBankAccountType.findByKey(radioButton.tag.toString())
        )
    }

    private fun onNextTap(v: View) {
        if (viewModel.bankAccount.validateAccountType) {
            findNavController().navigate(
                PixBankAccountTypeFragmentDirections
                    .actionPixBankAccountTypeFragmentToPixBankAccountDataFragment()
            )
        }
    }

}