package br.com.mobicare.cielo.selfieChallange.utils

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.commons.utils.setFullHeight
import br.com.mobicare.cielo.databinding.BottomSheetSelfieBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelfieBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSelfieBinding
    private var imageDrawable: Int? = null
    private var title: String? = null
    private var subtitle: String? = null
    private var nameBtnConfirm: String? = null
    private var nameBtnCancel: String? = null
    private var statusBtnConfirm: Boolean = true
    private var statusBtnCancel: Boolean = true
    private var statusDrag: Boolean = true
    private var isFullScreen: Boolean = false

    var onClickListeners: OnClickListeners? = null

    interface OnClickListeners {
        fun onBtnConfirm(dialog: Dialog) {}
        fun onBtnCancel(dialog: Dialog) {}
        fun onSwipe() {}
    }

    companion object {
        fun newInstance(
            @DrawableRes image: Int,
            title: String,
            subtitle: String,
            nameBtnConfirm: String = "",
            nameBtnCancel: String,
            statusBtnConfirm: Boolean = true,
            statusBtnCancel: Boolean = true,
            statusDrag: Boolean = true,
            isFullScreen: Boolean = false,
            isCancelable: Boolean = true
        ): SelfieBottomSheet {
            return SelfieBottomSheet().apply {
                this.imageDrawable = image
                this.title = title
                this.subtitle = subtitle
                this.nameBtnConfirm = nameBtnConfirm
                this.nameBtnCancel = nameBtnCancel
                this.statusBtnConfirm = statusBtnConfirm
                this.statusBtnCancel = statusBtnCancel
                this.statusDrag = statusDrag
                this.isFullScreen = isFullScreen
                this.isCancelable = isCancelable
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSelfieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialog()
        setupView()
        setupViewVisibilities()
        setupClickListeners()

    }

    private fun setupView() {
        binding.apply {
            btnConfirm.text = nameBtnConfirm.toString()
            btnCancel.text = nameBtnCancel.toString()
            tvTitle.text = title
            tvSubTitle.text = subtitle
            bsImage.setImageDrawable(
                imageDrawable?.let { ContextCompat.getDrawable(requireContext(), it) }
            )
        }
    }

    private fun setupViewVisibilities() {
        binding.apply {
            btnCancel.visibility = if (statusBtnCancel) View.VISIBLE else View.INVISIBLE
            btnConfirm.visibility = if (statusBtnConfirm) View.VISIBLE else View.INVISIBLE
            dragIcon.visibility = if (statusDrag) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun initDialog() {
        dialog?.let { dialog ->
            dialog.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val bottomSheet =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                val behavior = BottomSheetBehavior.from(bottomSheet)
                if (isFullScreen) setFullHeight(bottomSheet)

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0

                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (isCancelable) {
                            if (newState >= 4) {
                                this@SelfieBottomSheet.onClickListeners?.onSwipe()
                                dismiss()
                            }
                        }
                        else {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnConfirm.setOnClickListener { dialog?.let { onClickListeners?.onBtnConfirm(it) } }
            btnCancel.setOnClickListener { dialog?.let { onClickListeners?.onBtnCancel(it) } }
        }
    }
}