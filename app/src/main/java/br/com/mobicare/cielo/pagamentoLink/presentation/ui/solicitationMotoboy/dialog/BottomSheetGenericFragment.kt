package br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.custom_view_bottom_sheet_generic.*

/**
 * @author Enzo Teles
 *  Monday, May 11, 2020
 * */
class BottomSheetGenericFragment: BottomSheetDialogFragment(){

    private var nameToolbar: String? = null
    private var drawable: Int? = null
    private var title: String? = null
    private var subtitle: String? = null
    private var nameBtnBottom: String? = null
    private var statusBtnClose: Boolean = true
    private var statusBtnOk: Boolean = true
    private var statusViewLine: Boolean = true
    private var txtToolbarNameStyle: TextToolbaNameStyle? = null
    private var txtTitleStyle: TxtTitleStyle? = null
    private var txtSubtitleStyle: TxtSubTitleStyle? = null
    private var btnBottomStyle: ButtonBottomStyle? = null
    private var isResizeToolbar: Boolean = false

    companion object {
        fun newInstance(
            nameToolbar: String,
            image: Int,
            title: String,
            subtitle: String,
            nameBtnBottom: String,
            statusBtnClose: Boolean,
            statusBtnOk: Boolean,
            statusViewLine: Boolean,
            txtToolbarNameStyle: TextToolbaNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle: TxtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle: TxtSubTitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btnBottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isResizeToolbar: Boolean = false
        ): BottomSheetGenericFragment = BottomSheetGenericFragment().apply {
            this.nameToolbar = nameToolbar
            this.drawable = image
            this.title = title
            this.subtitle = subtitle
            this.nameBtnBottom = nameBtnBottom
            this.statusBtnClose = statusBtnClose
            this.statusBtnOk = statusBtnOk
            this.statusViewLine = statusViewLine
            this.txtToolbarNameStyle = txtToolbarNameStyle
            this.txtTitleStyle = txtTitleStyle
            this.txtSubtitleStyle = txtSubtitleStyle
            this.btnBottomStyle = btnBottomStyle
            this.isResizeToolbar = isResizeToolbar
        }
    }

    //variable
    private var initialTime: Long = 0
    private var MILLIS_IN_SEC = 1000L
    private var SECS_IN_MIN = 60

    var onClick: OnClickButtonsOptionsListener? = null

    interface OnClickButtonsOptionsListener {
        fun onBtnClose(dialog: Dialog){}
        fun onBtnOk(dialog: Dialog){}
        fun onLinkClick(dialog: Dialog){}
        fun onSwipeClosed() {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            = inflater.inflate(R.layout.bottom_sheet_generic_layout, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)
        verificationStatusDialog(dialog)
        return dialog
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // status buttons
        statusViewInScreen()
        //init bottom sheet
        initDialogBS(view)
        //click views
        clickViews()
        //set widge
        setWidge()
        //btn bottom style
        setStyleButtonBottom()

        //toolbar title
        setSytleToolbarName()

        //color title
        setStyleTitle()

        //color subtitle
        setStyleSubTitle()

    }

    private fun verificationStatusDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            // For AndroidX use: com.google.android.material.R.id.design_bottom_sheet
            val behavior = setBottomSheetBehavior(dialog)


            behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState >= 4) {
                        this@BottomSheetGenericFragment.onClick?.onSwipeClosed()
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }

    private fun setBottomSheetBehavior(dialog: Dialog): BottomSheetBehavior<FrameLayout?> {
        val bottomSheet = dialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) as FrameLayout

        val behavior = BottomSheetBehavior.from(bottomSheet)

        if (isResizeToolbar) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            val layoutParams = bottomSheet.layoutParams
            layoutParams?.height = getBottomSheetDialogDefaultHeight()
            bottomSheet.layoutParams = layoutParams
            viewIndicateClose.visibility = View.VISIBLE
        } else {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            viewIndicateClose.visibility = View.GONE
        }
        return behavior
    }

    private fun setStyleSubTitle() {
        when (txtSubtitleStyle) {
            TxtSubTitleStyle.TXT_SUBTITLE_BLACK -> TextViewCompat.setTextAppearance(
                txt_subtitle,
                R.style.TxtSubtitleBlackNormal
            )
            TxtSubTitleStyle.TXT_SUBTITLE_BLUE -> TextViewCompat.setTextAppearance(
                txt_subtitle,
                R.style.TxtSubtitleBlueNormal
            )
            TxtSubTitleStyle.TXT_SUBTITLE_GREEN -> {
                TextViewCompat.setTextAppearance(
                    txt_subtitle,
                    R.style.TxtSubtitleGreenBold
                )
                chronometerStart()
            }
            TxtSubTitleStyle.TXT_SUBTITLE_BLACK_JUSTIFIED -> {
                TextViewCompat.setTextAppearance(
                        txt_subtitle,
                        R.style.TxtSubtitleBlackNormalJustified
                )
            }
        }
    }

    private fun setStyleTitle() {
        when (txtTitleStyle) {
            TxtTitleStyle.TXT_TITLE_BLUE -> TextViewCompat.setTextAppearance(
                txt_title,
                R.style.TxtTitleBlueBold
            )
            TxtTitleStyle.TXT_TITLE_BLACK -> {
                TextViewCompat.setTextAppearance(
                    txt_title,
                    R.style.TxtSubtitleBlackNormal
                )
            }
        }
    }

    private fun setSytleToolbarName() {
        when (txtToolbarNameStyle) {
            TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL -> TextViewCompat.setTextAppearance(
                txt_toobar_name,
                R.style.TxtToobarNameNormal
            )
            TextToolbaNameStyle.TXT_TOOlBAR_NAME_BOLD -> {
                TextViewCompat.setTextAppearance(txt_toobar_name, R.style.TxtToobarNameBold)
                funTxtToolbarMargins()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun setStyleButtonBottom() {
        when (btnBottomStyle) {
            ButtonBottomStyle.BNT_BOTTOM_WHITE -> {
                retryButton.setBackgroundResource(R.drawable.btn_bottom_unselector)
                retryButton.setTextAppearance(R.style.BntBottomBackgroundWhite)
            }
            ButtonBottomStyle.BNT_BOTTOM_BLUE -> {
                retryButton.setBackgroundResource(R.drawable.btn_bottom_selector)
                retryButton.setTextAppearance(R.style.BntBottomBackgroundBlue)
            }
        }
    }

    private fun setWidge() {
        retryButton.setText(nameBtnBottom)
        txt_title.text = title
        txt_subtitle.text = subtitle
        txt_toobar_name.text = nameToolbar
        img_bottom_sheet.background = drawable?.let {
            ContextCompat.getDrawable(requireContext(),
                it
            )
        }
    }

    private fun clickViews() {
        btn_close.setOnClickListener { dialog?.let { it1 -> onClick?.onBtnClose(it1) } }
        retryButton.setOnClickListener { dialog?.let { it1 -> onClick?.onBtnOk(it1) } }
        txt_subtitle.setOnClickListener { dialog?.let { it1 -> onClick?.onLinkClick(it1) } }
    }

    private fun initDialogBS(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }
    }

    private fun statusViewInScreen() {
        btn_close.visibility = if (statusBtnClose) View.VISIBLE else View.INVISIBLE
        retryButton.visibility = if (statusBtnOk) View.VISIBLE else View.INVISIBLE
        view2.visibility = if (statusViewLine) View.VISIBLE else View.INVISIBLE
    }


    private fun funTxtToolbarMargins() {
        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        params.setMargins(250, 70, 0, 0)
        txt_toobar_name.gravity = Gravity.CENTER
        txt_toobar_name.setLayoutParams(params)
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 85 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = requireContext().resources.displayMetrics
        return displayMetrics.heightPixels
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
                val seconds: Long = (System.currentTimeMillis() - initialTime) / MILLIS_IN_SEC
                if(txt_subtitle != null){
                    txt_subtitle.setText(
                        String.format(
                            "%02d:%02d",
                            seconds / SECS_IN_MIN,
                            seconds % SECS_IN_MIN
                        )
                    )
                }
                Handler().postDelayed(
                    this,
                    MILLIS_IN_SEC
                )
        }
    }

    private fun chronometerStart(){
        initialTime = System.currentTimeMillis()
        Handler().postDelayed(
            runnable,
            MILLIS_IN_SEC
        )
    }

    private fun chronometerStop(){
        Handler().removeCallbacks(
            runnable
        )
    }

    internal fun closeDialog(){
        this.dialog?.dismiss()
    }
}