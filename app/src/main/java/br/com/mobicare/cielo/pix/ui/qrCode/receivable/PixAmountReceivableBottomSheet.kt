package br.com.mobicare.cielo.pix.ui.qrCode.receivable

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.SEVEN
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.moneyToDoubleValue
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.commons.utils.showKeyboard
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.PixAmountReceivableBottomSheetBinding
import br.com.mobicare.cielo.pix.constants.PIX_AMOUNT_QR_CODE_ARGS
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PixAmountReceivableBottomSheet : BottomSheetDialogFragment() {

    private var listener: PixAmountReceivableContract? = null

    private var _binding: PixAmountReceivableBottomSheetBinding? = null
    private val binding get() = _binding
    private var newAmount: Double? = null

    companion object {
        fun onCreate(listener: PixAmountReceivableContract, amount: Double) =
            PixAmountReceivableBottomSheet().apply {
                this.listener = listener
                this.arguments = Bundle().apply {
                    this.putDouble(PIX_AMOUNT_QR_CODE_ARGS, amount)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        _binding = PixAmountReceivableBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupListeners()
    }

    private fun amount(): Double = (arguments
        ?.getDouble(PIX_AMOUNT_QR_CODE_ARGS)) ?: ZERO_DOUBLE

    private fun setupView() {
        requireActivity().showKeyboard(binding?.inputAmountPix)
        binding?.inputAmountPix?.setMaskMoney()
        binding?.inputAmountPix?.setText(amount().toPtBrRealString(isPrefix = false))
        val amountLength = binding?.inputAmountPix?.getText()?.length ?: SEVEN
        binding?.inputAmountPix?.setSelection(amountLength)
    }

    private fun setupListeners() {
        binding?.btnSaveAmountReceivable?.setOnClickListener {
            newAmount = binding?.inputAmountPix?.getText().toString().moneyToDoubleValue()
            dismiss()
        }

        binding?.btnValueless?.setOnClickListener {
            newAmount = ZERO_DOUBLE
            dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener?.onAmount(amount())
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onAmount(newAmount ?: amount())
    }
}