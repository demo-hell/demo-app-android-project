package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.accountSelector

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.LayoutBankListBinding
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneAccount
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.accountSelector.adapter.AccountSelectorAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AccountSelectorBottomSheet : BottomSheetDialogFragment(), AccountSelectorContract.View {

    private var items: List<TapOnPhoneAccount>? = null
    private var binding: LayoutBankListBinding? = null
    private var listener: AccountSelectorContract.Result? = null

    private var selectedAccount: TapOnPhoneAccount? = null

    companion object {
        fun create(
            items: List<TapOnPhoneAccount>,
            selectedAccount: TapOnPhoneAccount? = null,
            listener: AccountSelectorContract.Result
        ) =
            AccountSelectorBottomSheet().apply {
                this.listener = listener
                this.items = items
                this.selectedAccount = selectedAccount
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutBankListBinding.inflate(layoutInflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog(dialog)
        setupView()
    }

    private fun setupView() {
        binding?.apply {
            rvAccounts.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL, false
            )

            items?.let {
                rvAccounts.adapter =
                    AccountSelectorAdapter(it, selectedAccount, this@AccountSelectorBottomSheet)
                rvAccounts.scrollToPosition(it.indexOf(selectedAccount))
            }

            btnNext.setOnClickListener {
                selectedAccount?.let {
                    listener?.onAccountConfirm(it)
                    dismiss()
                }
            }
            selectedAccount?.let {
                btnNext.isEnabled = true
            }
        }
    }

    private fun setupDialog(dialog: Dialog?) {
        dialog?.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = ZERO

                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState >= FOUR) dismiss()
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
    }

    override fun onAccountSelected(account: TapOnPhoneAccount) {
        selectedAccount = account
        binding?.btnNext?.isEnabled = true
    }
}