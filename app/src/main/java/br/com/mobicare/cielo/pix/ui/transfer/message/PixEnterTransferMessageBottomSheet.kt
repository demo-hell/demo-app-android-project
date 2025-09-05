package br.com.mobicare.cielo.pix.ui.transfer.message

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.commons.utils.showKeyboard
import br.com.mobicare.cielo.databinding.PixEnterTransferMessageBottomSheetBinding
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PixEnterTransferMessageBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: PixEnterTransferMessageBottomSheetBinding
    private var listener: PixEnterTransferMessageContract? = null
    private var message: String = EMPTY
    private var isRefund: Boolean = false

    companion object {
        fun onCreate(
            listener: PixEnterTransferMessageContract,
            message: String,
            isRefund: Boolean = false
        ) =
            PixEnterTransferMessageBottomSheet().apply {
                this.listener = listener
                this.message = message
                this.isRefund = isRefund
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        binding = PixEnterTransferMessageBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    private fun setupView() {
        requireActivity().showKeyboard(binding.inputEnterMessagePix)
        if (message != getString(R.string.text_pix_summary_transfer_insert_msg_hint)
            && message != getString(R.string.text_pix_reversal_insert_reason_hint)
            && message.isNotEmpty()
        ) {
            binding.inputEnterMessagePix.setText(message)
            binding.inputEnterMessagePix.setSelection(message.length)
        }

        binding.btnSaveMessagePix.setOnClickListener {
            listener?.onMessage(binding.inputEnterMessagePix.text?.toString() ?: EMPTY)
            dismiss()
        }
        if (isRefund) {
            setupViewsForRefund()
        }
    }

    private fun setupViewsForRefund() {
        binding.titleEnterMessagePix.text = getString(R.string.text_pix_enter_message_title_reversal)
        binding.inputEnterMessagePix.hint = getString(R.string.text_pix_enter_message_hint_reversal)
        binding.inputEnterMessagePix.filters = arrayOf(InputFilter.LengthFilter(ONE_HUNDRED.toInt()))
        binding.btnSaveMessagePix.setText(getString(R.string.text_pix_enter_message_btn_reversal))
    }
}