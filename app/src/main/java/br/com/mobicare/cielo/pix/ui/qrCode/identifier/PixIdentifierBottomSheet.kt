package br.com.mobicare.cielo.pix.ui.qrCode.identifier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.commons.utils.showKeyboard
import br.com.mobicare.cielo.databinding.PixIdentifierBottomSheetBinding
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_IDENTIFIER_QR_CODE_ARGS
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PixIdentifierBottomSheet : BottomSheetDialogFragment() {

    private var listener: PixIdentifierContract? = null

    private var _binding: PixIdentifierBottomSheetBinding? = null
    private val binding get() = _binding

    companion object {
        fun onCreate(listener: PixIdentifierContract, identifier: String) =
            PixIdentifierBottomSheet().apply {
                this.listener = listener
                this.arguments = Bundle().apply {
                    this.putString(PIX_IDENTIFIER_QR_CODE_ARGS, identifier)
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
        _binding = PixIdentifierBottomSheetBinding.inflate(inflater, container, false)
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
    }

    private fun identifier(): String = (arguments
        ?.getString(PIX_IDENTIFIER_QR_CODE_ARGS)) ?: EMPTY

    private fun setupView() {
        requireActivity().showKeyboard(binding?.inputEnterIdentifierPix)
        if (identifier() != getString(R.string.screen_text_generate_qr_code_billing_data_identifier) && identifier().isNotEmpty()) {
            binding?.inputEnterIdentifierPix?.setText(identifier())
            binding?.inputEnterIdentifierPix?.setSelection(identifier().length)
        }

        binding?.btnSaveIdentifier?.setOnClickListener {
            listener?.onIdentifier(binding?.inputEnterIdentifierPix?.text?.toString() ?: EMPTY)
            dismiss()
        }
    }
}