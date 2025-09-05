package br.com.mobicare.cielo.tapOnPhone.presentation.sale.type.installment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.databinding.LayoutTapOnPhoneChooseInstallmentsBottomSheetBinding
import br.com.mobicare.cielo.tapOnPhone.constants.TOP_CURRENT_SELECTED_INSTALLMENT_ARGS
import br.com.mobicare.cielo.tapOnPhone.constants.TOP_INSTALLMENTS_QUANTITY_ARGS
import br.com.mobicare.cielo.tapOnPhone.presentation.sale.type.installment.adapter.TapOnPhoneChooseInstallmentsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TapOnPhoneChooseInstallmentsBottomSheet : BottomSheetDialogFragment() {

    private var listener: ((installment: String?) -> Unit)? = null
    private var binding: LayoutTapOnPhoneChooseInstallmentsBottomSheetBinding? = null
    private var selectedInstallment: String? = null

    private val installments by lazy {
        arguments?.getIntegerArrayList(TOP_INSTALLMENTS_QUANTITY_ARGS)
    }

    private val currentSelectedInstallment: String? by lazy {
        arguments?.getString(TOP_CURRENT_SELECTED_INSTALLMENT_ARGS)
    }

    companion object {
        fun onCreate(
            allInstallments: ArrayList<Int>,
            currentSelectedInstallment: String?,
            listener: ((installment: String?) -> Unit)
        ) = TapOnPhoneChooseInstallmentsBottomSheet().apply {
            this.listener = listener
            this.arguments = Bundle().apply {
                this.putIntegerArrayList(TOP_INSTALLMENTS_QUANTITY_ARGS, allInstallments)
                this.putString(TOP_CURRENT_SELECTED_INSTALLMENT_ARGS, currentSelectedInstallment)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBottomSheet(
            dialog = dialog,
            action = {
                dismiss()
                listener?.invoke(currentSelectedInstallment)
            }
        )

        binding =
            LayoutTapOnPhoneChooseInstallmentsBottomSheetBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupView()
    }

    private fun setupListeners() {
        binding?.btConfirm?.setOnClickListener {
            listener?.invoke(selectedInstallment)
            dismiss()
        }
    }

    private fun setupView() {
        installments?.let { itInstallments ->
            binding?.rvInstallmentQuantity?.apply {
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL, false
                )

                setHasFixedSize(true)

                adapter = TapOnPhoneChooseInstallmentsAdapter(
                    itInstallments,
                    currentSelectedInstallment,
                    requireContext()
                ) { itSelectedInstallment ->
                    selectedInstallment = itSelectedInstallment
                    binding?.btConfirm?.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}