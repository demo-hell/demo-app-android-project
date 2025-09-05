package br.com.mobicare.cielo.commons.ui.widget.flui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.TextViewCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.utils.setFullHeight
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.custom_view_bottom_sheet_generic_flui_new.*


/**
 * @author Enzo Teles
 *  Wednesday, Set 16, 2020
 * */
class BottomSheetFluiGenericFragment : BottomSheetDialogFragment() {

    private var nameToolbar: String? = null
    private var drawable: Int? = null
    private var title: String? = null
    private var subtitle: String? = null
    private var nameBtn1Bottom: String? = null
    private var nameBtn2Bottom: String? = null
    private var statusNameTopBar: Boolean = true
    private var statusTitle: Boolean = true
    private var statusSubTitle: Boolean = true
    private var statusImage: Boolean = true
    private var statusBtnClose: Boolean = true
    private var statusBtnFirst: Boolean = true
    private var statusBtnSecond: Boolean = true
    private var statusView1Line: Boolean = true
    private var statusView2Line: Boolean = true
    private var txtToolbarNameStyle: TextToolbaNameStyle? = null
    private var txtTitleStyle: TxtTitleStyle? = null
    private var txtSubtitleStyle: TxtSubTitleStyle? = null
    private var btn1BottomStyle: ButtonBottomStyle? = null
    private var btn2BottomStyle: ButtonBottomStyle? = null
    private var isFullScreen: Boolean = false
    private var isPhone: Boolean = true

    companion object {
        fun newInstance(
            nameToolbar: String = "",
            image: Int,
            title: String,
            subtitle: String,
            nameBtn1Bottom: String = "",
            nameBtn2Bottom: String,
            statusNameTopBar: Boolean = true,
            statusTitle: Boolean = true,
            statusSubTitle: Boolean = true,
            statusImage: Boolean = true,
            statusBtnClose: Boolean = true,
            statusBtnFirst: Boolean = true,
            statusBtnSecond: Boolean = true,
            statusView1Line: Boolean = true,
            statusView2Line: Boolean = true,
            txtToolbarNameStyle: TextToolbaNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle: TxtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle: TxtSubTitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
            btn2BottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen: Boolean = false,
            isCancelable: Boolean = true,
            isPhone: Boolean = true
        ): BottomSheetFluiGenericFragment {
            val fragment = BottomSheetFluiGenericFragment()
            fragment.apply {
                this.nameToolbar = nameToolbar
                this.drawable = image
                this.title = title
                this.subtitle = subtitle
                this.nameBtn1Bottom = nameBtn1Bottom
                this.nameBtn2Bottom = nameBtn2Bottom
                this.statusNameTopBar = statusNameTopBar
                this.statusTitle = statusTitle
                this.statusSubTitle = statusSubTitle
                this.statusImage = statusImage
                this.statusBtnClose = statusBtnClose
                this.statusBtnFirst = statusBtnFirst
                this.statusBtnSecond = statusBtnSecond
                this.statusView1Line = statusView1Line
                this.statusView2Line = statusView2Line
                this.txtToolbarNameStyle = txtToolbarNameStyle
                this.txtTitleStyle = txtTitleStyle
                this.txtSubtitleStyle = txtSubtitleStyle
                this.btn1BottomStyle = btn1BottomStyle
                this.btn2BottomStyle = btn2BottomStyle
                this.isFullScreen = isFullScreen
                this.isCancelable = isCancelable
                this.isPhone = isPhone
            }
            return fragment
        }
    }

    var onClick: OnClickButtonsOptionsListener? = null

    interface OnClickButtonsOptionsListener {
        fun onBtnClose(dialog: Dialog) {}
        fun onBtnFirst(dialog: Dialog) {}
        fun onBtnSecond(dialog: Dialog) {}
        fun onLinkClick(dialog: Dialog) {}
        fun onSwipeClosed() {}
        fun onCancel() {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_flui_generic_layout, container, false)

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

        //color title
        setStyleTitle()

        //color subtitle
        setStyleSubTitle()

    }

    private fun verificationStatusDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                if (isFullScreen)
                    setFullHeight(bottomSheet)

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (isCancelable) {
                            if (newState >= 4) {
                                this@BottomSheetFluiGenericFragment.onClick?.onSwipeClosed()
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

    private fun setStyleSubTitle() {
        when (txtSubtitleStyle) {
            TxtSubTitleStyle.TXT_SUBTITLE_BLACK -> TextViewCompat.setTextAppearance(
                txt_subtitle,
                R.style.TxtSubtitleBlackNormal
            )
            TxtSubTitleStyle.TXT_SUBTITLE_BLACK_CENTER -> {
                TextViewCompat.setTextAppearance(
                    txt_subtitle,
                    R.style.TextBlack16spUbuntu_353A40
                )
                txt_subtitle.gravity = Gravity.CENTER
            }

            TxtSubTitleStyle.TXT_SUBTITLE_BLUE -> TextViewCompat.setTextAppearance(
                txt_subtitle,
                R.style.TxtSubtitleBlueNormal
            )
            TxtSubTitleStyle.TXT_SUBTITLE_GREEN -> {
                TextViewCompat.setTextAppearance(
                    txt_subtitle,
                    R.style.TxtSubtitleGreenBold
                )
            }
            TxtSubTitleStyle.TXT_SUBTITLE_BLACK_JUSTIFIED -> {
                TextViewCompat.setTextAppearance(
                    txt_subtitle,
                    R.style.TxtSubtitleBlackNormalJustified
                )
                txt_subtitle.gravity = Gravity.START
                txt_subtitle.invalidate()
            }
            TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET -> {
                TextViewCompat.setTextAppearance(
                    txt_subtitle,
                    R.style.TxtSubtitleFluiBottomSheet
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
            TxtTitleStyle.TXT_TITLE_DARK_BLACK -> {
                TextViewCompat.setTextAppearance(
                    txt_title,
                    R.style.TxtToobarNameBoldMontserrat
                )
            }
            TxtTitleStyle.TXT_TITLE_RED -> {
                TextViewCompat.setTextAppearance(
                    txt_title,
                    R.style.TxtTitleRed
                )
            }
            TxtTitleStyle.TXT_TITLE_DARK_BLUE -> {
                TextViewCompat.setTextAppearance(
                    txt_title,
                    R.style.TxtTitleDarkBlue
                )
            }

        }
    }

    @SuppressLint("NewApi")
    private fun setStyleButtonBottom() {

        when (btn1BottomStyle) {
            ButtonBottomStyle.BNT_BOTTOM_WHITE -> {
                retryButton1.setBackgroundResource(R.drawable.btn_bottom_unselector)
                retryButton1.setAppearance(requireContext(), R.style.BntBottomBackgroundWhite)
            }
            ButtonBottomStyle.BNT_BOTTOM_BLUE -> {
                retryButton1.setBackgroundResource(R.drawable.btn_bottom_selector)
                retryButton1.setAppearance(requireContext(), R.style.BntBottomBackgroundBlue)
            }
            ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE -> {
                retryButton1.setBackgroundResource(R.drawable.btn_bottom_unselector)
                retryButton1.setAppearance(requireContext(), R.style.BntBottomBackgroundWhiteTextBlue)
            }
        }

        when (btn2BottomStyle) {
            ButtonBottomStyle.BNT_BOTTOM_WHITE -> {
                retryButton2.setBackgroundResource(R.drawable.btn_bottom_unselector)
                retryButton2.setAppearance(requireContext(), R.style.BntBottomBackgroundWhite)
            }
            ButtonBottomStyle.BNT_BOTTOM_BLUE -> {
                retryButton2.setBackgroundResource(R.drawable.btn_bottom_selector)
                retryButton2.setAppearance(requireContext(), R.style.BntBottomBackgroundBlue)
            }
        }
    }

    private fun setWidge() {
        retryButton1?.text = nameBtn1Bottom
        retryButton2?.text = nameBtn2Bottom
        txt_title?.text = title
        img_bottom_sheet?.setImageDrawable(drawable?.let {
            ContextCompat.getDrawable(requireContext(),
                it
            )
        })
        subtitle?.let { spannableString(it, txt_subtitle) }
    }

    private fun spannableString(string: String, view: TypefaceTextView?) {
        val spannableString = SpannableString(
            HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_LEGACY)
        )

        view?.setText(spannableString, TextView.BufferType.SPANNABLE)

        if (isPhone)
            configPhone(view)
    }

    private fun configPhone(view: TypefaceTextView?) {
        view?.let {
            Linkify.addLinks(
                view,
                Patterns.PHONE,
                "tel:",
                Linkify.sPhoneNumberMatchFilter,
                Linkify.sPhoneNumberTransformFilter
            )
            view.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun clickViews() {
        retryButton1.setOnClickListener { dialog?.let { it1 -> onClick?.onBtnFirst(it1) } }
        retryButton2.setOnClickListener { dialog?.let { it1 -> onClick?.onBtnSecond(it1) } }
        txt_subtitle.setOnClickListener { dialog?.let { it1 -> onClick?.onLinkClick(it1) } }
    }

    private fun initDialogBS(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }
    }

    private fun statusViewInScreen() {
        txt_title.visibility = if (statusTitle) View.VISIBLE else View.INVISIBLE
        txt_subtitle.visibility = if (statusSubTitle) View.VISIBLE else View.INVISIBLE
        img_bottom_sheet.visibility = if (statusImage) View.VISIBLE else View.GONE
        retryButton1.visibility = if (statusBtnFirst) View.VISIBLE else View.INVISIBLE
        retryButton2.visibility = if (statusBtnSecond) View.VISIBLE else View.GONE
        view1.visibility = if (statusView1Line && isCancelable) View.VISIBLE else View.INVISIBLE
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        this@BottomSheetFluiGenericFragment.onClick?.onCancel()
    }
}