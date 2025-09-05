package br.com.mobicare.cielo.commons.ui.update

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.AppHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_update_app.*

class UpdateAppBottomSheet(
        private val listener: OnUpdateAppBottomSheetListener?) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(listener: OnUpdateAppBottomSheetListener? = null)
                = UpdateAppBottomSheet(listener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        = inflater.inflate(R.layout.layout_update_app, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureListeners()
    }

    /**
     *onCreateDialog
     * @param savedInstanceState
     * @return dialog
     * */
    @SuppressLint("StringFormatMatches")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        changeDialog(dialog)
        dialog.setOnKeyListener { dialog, keyCode, _ ->
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                dialog.dismiss()
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }

        }
        return dialog
    }


    /**
     * método para vericar quando o dialog muda de estado
     * @param dialog
     * */
    private fun changeDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                    R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }

    private fun configureListeners() {
        this.updateButton?.setOnClickListener {
            this.listener?.let {
                it.onButtonClicked()
            }
            AppHelper.redirectToGooglePlay(requireContext())
            this.dismiss()
        }

        this.closeButton?.setOnClickListener {
            this.dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            manager.beginTransaction().let {
                it.add(this, tag)
                it.commitAllowingStateLoss()
            }
        }
        catch(ignored: IllegalStateException) {
            ignored.printStackTrace()
        }
        catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    interface OnUpdateAppBottomSheetListener {
        fun onButtonClicked()
    }

}