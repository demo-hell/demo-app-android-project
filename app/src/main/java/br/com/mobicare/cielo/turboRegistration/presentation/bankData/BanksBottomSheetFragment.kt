package br.com.mobicare.cielo.turboRegistration.presentation.bankData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.util.extensions.gone
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentBanksBottomSheetBinding
import br.com.mobicare.cielo.turboRegistration.domain.model.Bank
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class BanksBottomSheetFragment : BottomSheetDialogFragment() {

    private val binding: FragmentBanksBottomSheetBinding by viewBinding()
    private val viewModel: BankInfoViewModel by sharedViewModel()
    private lateinit var bankAdapter: BanksAdapter
    var onBankSelected: ((Bank) -> Unit)? = null
    private var lastSelectedBank: Bank? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fixedSizeDialog()
        setAdapter()
        viewModel.searchBanks()
        setListener()
        addObserver()
    }

    private fun setListener() {
        binding.svBanks.setOnSearchRealtime {
            viewModel.setName(it)
            viewModel.searchBanks()
        }

        binding.ibClose.setOnClickListener {
            dismiss()
        }

        binding.viewError.btRefresh.setOnClickListener {
            viewModel.setName(null)
            viewModel.searchBanks()
        }
    }

    private fun addObserver() {
        viewModel.banks.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is RegistrationResource.Loading -> {
                    binding.apply {
                        pbLoading.visible()
                        viewError.root.gone()
                    }
                }

                is RegistrationResource.Success -> {
                    binding.apply {
                        viewError.root.gone()
                        pbLoading.gone()
                        setupAdapterData(resource.data)
                    }
                }

                is RegistrationResource.Error -> {
                    binding.apply {
                        pbLoading.gone()
                        viewError.root.visible()
                    }
                }

                RegistrationResource.Empty -> {}
            }
        }
    }

    private fun setupAdapterData(list: List<Bank>) {
        if (list.isNotEmpty()) {
            binding.bankEmptyListMessage.gone()
            binding.rvBanks.visible()
            bankAdapter.setData(list)
            lastSelectedBank?.let { bankAdapter.setSelectedBank(it) }
        } else {
            binding.rvBanks.gone()
            binding.bankEmptyListMessage.visible()
            binding.bankEmptyListMessage.text = getString(R.string.label_message_bank_empty_list, binding.svBanks.getText())
        }
    }

    private fun setAdapter() {
        bankAdapter = BanksAdapter(
            { bank ->
                onBankSelected?.invoke(bank)
                lastSelectedBank = bank
                viewModel.setName(null)
                this@BanksBottomSheetFragment.dismiss()
            },
            lastSelectedBank
        )
        binding.rvBanks.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            val dividerDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerDecoration)
            adapter = bankAdapter
        }
    }

    private fun fixedSizeDialog() {
        dialog?.let { dialog ->
            dialog.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                val behavior = BottomSheetBehavior.from(bottomSheet)
                val layoutParams = bottomSheet.layoutParams

                val displayMetrics = resources.displayMetrics
                layoutParams.height = (displayMetrics.heightPixels * SEVENTY_PERCENT).toInt()
                bottomSheet.layoutParams = layoutParams

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = false
            }
        }
    }

    companion object {
        const val SEVENTY_PERCENT = 0.7
        fun newInstance(onBankSelected: (Bank) -> Unit): BanksBottomSheetFragment {
            return BanksBottomSheetFragment().apply {
                this.onBankSelected = onBankSelected
            }
        }
    }
}