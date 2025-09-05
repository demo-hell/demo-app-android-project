package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.TextViewCompat
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.custom_view_bottom_sheet_generic.view.*

/**
 * @author Enzo Teles
 *  Monday, May 11, 2020
 * */

class CustomViewBottomSheetGeneric @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr) {


    init {
        LayoutInflater.from(context).inflate(R.layout.custom_view_bottom_sheet_generic, this, true)

        attrs?.let {
            val typeArray = context.obtainStyledAttributes(
                it,
                R.styleable.bottom_sheet_generic_attributes,
                0,
                0
            )

            // name's button
            val toolbarName = resources.getText(
                typeArray.getResourceId(
                    R.styleable.bottom_sheet_generic_attributes_bs_toolbar_name,
                    R.string.txt_aguardando_motoboy
                )
            )
            val title = resources.getText(
                typeArray.getResourceId(
                    R.styleable.bottom_sheet_generic_attributes_bs_title,
                    R.string.txt_title_motoboy
                )
            )
            val subtitle = resources.getText(
                typeArray.getResourceId(
                    R.styleable.bottom_sheet_generic_attributes_bs_subtitle,
                    R.string.txt_subtitle_motoboy
                )
            )
            val btnBottomName = resources.getText(
                typeArray.getResourceId(
                    R.styleable.bottom_sheet_generic_attributes_bs_btn_bottom_name,
                    R.string.ok
                )
            )

            //status' button
            val btnCloseStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_bs_btn_close_status,
                true
            )

            val btnBottomStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_bs_btn_bottom_status,
                true

            )

            val viewLineStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_bs_view_line_status,
                true

            )

            //style's button
            val btnBottomStyle = typeArray.getEnum(
                R.styleable.bottom_sheet_generic_attributes_bs_btn_bottom_style,
                ButtonBottomStyle.BNT_BOTTOM_WHITE
            )

            val txtToolbarNameStyle = typeArray.getEnum(
                R.styleable.bottom_sheet_generic_attributes_bs_txt_toolbar_name_style,
                TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL
            )

            val txtTitleStyle = typeArray.getEnum(
                R.styleable.bottom_sheet_generic_attributes_bs_txt_title_style,
                TxtTitleStyle.TXT_TITLE_BLUE
            )

            val txtSubTitleStyle = typeArray.getEnum(
                R.styleable.bottom_sheet_generic_attributes_bs_txt_subtitle_style,
                TxtSubTitleStyle.TXT_SUBTITLE_BLACK
            )

            //########################################
            // styles
            //########################################
            //button bottom
            when (btnBottomStyle) {
                ButtonBottomStyle.BNT_BOTTOM_WHITE -> {
                    retryButton.setBackgroundResource(R.drawable.btn_bottom_unselector)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        retryButton.setTextAppearance(R.style.BntBottomBackgroundWhite)
                    }
                }
                ButtonBottomStyle.BNT_BOTTOM_BLUE -> {
                    retryButton.setBackgroundResource(R.drawable.btn_bottom_selector)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        retryButton.setTextAppearance(R.style.BntBottomBackgroundBlue)
                    }
                }
            }

            //toolbar title
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

            //color title
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

            //color subtitle
            when (txtSubTitleStyle) {
                TxtSubTitleStyle.TXT_SUBTITLE_BLACK -> TextViewCompat.setTextAppearance(
                    txt_subtitle,
                    R.style.TxtSubtitleBlackNormal
                )
                TxtSubTitleStyle.TXT_SUBTITLE_BLUE -> TextViewCompat.setTextAppearance(
                        txt_subtitle,
                        R.style.TxtSubtitleBlueNormal
                    )
                TxtSubTitleStyle.TXT_SUBTITLE_GREEN -> TextViewCompat.setTextAppearance(
                    txt_subtitle,
                    R.style.TxtSubtitleGreenBold
                )
                TxtSubTitleStyle.TXT_SUBTITLE_BLACK_JUSTIFIED -> TextViewCompat.setTextAppearance(
                        txt_subtitle,
                        R.style.TxtSubtitleBlackNormalJustified
                )
            }
            val img = typeArray.getDrawable(R.styleable.bottom_sheet_generic_attributes_bs_image)

            //########################################
            // widget
            //########################################
            txt_toobar_name.text = toolbarName
            txt_title.text = title
            txt_subtitle.text = subtitle
            retryButton.setText(btnBottomName.toString())
            img_bottom_sheet.background = img

            //########################################
            // regra de negócio
            //########################################
            btn_close.visibility = if (btnCloseStatus) View.VISIBLE else View.GONE
            retryButton.visibility = if (btnBottomStatus) View.VISIBLE else View.INVISIBLE
            view2.visibility = if (viewLineStatus) View.VISIBLE else View.INVISIBLE

            typeArray.recycle()
        }

    }

    private fun funTxtToolbarMargins() {
        val params =
            LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
        params.setMargins(250, 70, 0, 0)
        txt_toobar_name.gravity = Gravity.CENTER
        txt_toobar_name.setLayoutParams(params)
    }

}

enum class ButtonBottomStyle {
    BNT_BOTTOM_WHITE, BNT_BOTTOM_BLUE,BNT_BOTTOM_WHITE_TXT_BLUE
}

enum class TextToolbaNameStyle {
    TXT_TOOlBAR_NAME_NORMAL, TXT_TOOlBAR_NAME_BOLD
}

enum class TxtTitleStyle {
    TXT_TITLE_BLUE, TXT_TITLE_BLACK, TXT_TITLE_DARK_BLACK, TXT_TITLE_RED, TXT_TITLE_DARK_BLUE
}

enum class TxtSubTitleStyle {
    TXT_SUBTITLE_BLUE, TXT_SUBTITLE_BLACK, TXT_SUBTITLE_GREEN, TXT_SUBTITLE_BLACK_JUSTIFIED,
    TXT_SUBTITLE_FLUI_BOTTOM_SHEET, TXT_SUBTITLE_BLACK_CENTER
}

inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T) =
    getInt(index, -1).let {
        if (it >= 0) enumValues<T>()[it] else default
    }