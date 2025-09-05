package br.com.mobicare.cielo.commons.ui.widget.flui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.TextViewCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.getEnum
import kotlinx.android.synthetic.main.custom_view_bottom_sheet_generic_flui.view.*

/**
 * @author Enzo Teles
 *  Wednesday, Sep 16, 2020
 * */

class CustomViewBottomSheetGenericFlui @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_view_bottom_sheet_generic_flui_new, this, true)

        attrs?.let {
            val typeArray = context.obtainStyledAttributes(
                it,
                R.styleable.bottom_sheet_generic_attributes_flui,
                0,
                0
            )

            val title = resources.getText(
                typeArray.getResourceId(
                    R.styleable.bottom_sheet_generic_attributes_flui_bs_title_flui,
                    R.string.txt_title_motoboy
                )
            )

            val subtitle = resources.getText(
                typeArray.getResourceId(
                    R.styleable.bottom_sheet_generic_attributes_flui_bs_subtitle_flui,
                    R.string.txt_subtitle_motoboy
                )
            )

            val btn1BottomName = resources.getText(
                typeArray.getResourceId(
                    R.styleable.bottom_sheet_generic_attributes_flui_bs_btn1_bottom_name_flui,
                    R.string.ok
                )
            )

            val btn2BottomName = resources.getText(
                typeArray.getResourceId(
                    R.styleable.bottom_sheet_generic_attributes_flui_bs_btn2_bottom_name_flui,
                    R.string.ok
                )
            )

            //status' widget
            val btnCloseStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_btn_close_status_flui,
                true
            )

            val btnBottomStatusFirst = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_btn_bottom_first_status_flui,
                true
            )

            val btnBottomStatusSecond = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_btn_bottom_second_status_flui,
                true
            )

            val titleBottomStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_title_status_flui,
                true
            )

            val titleTopBarBottomStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_topbar_title_status_flui,
                true
            )

            val subtitleBottomStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_subtitle_status_flui,
                true
            )

            val imageBottomStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_image_status_flui,
                true
            )

            val view1LineStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_view1_line_status_flui,
                true
            )

            val viewLineStatus = typeArray.getBoolean(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_view_line_status_flui,
                true
            )

            //style's button
            val btnBottomStyle1 = typeArray.getEnum(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_btn_bottom1_style_flui,
                ButtonBottomStyle.BNT_BOTTOM_WHITE
            )

            val btnBottomStyle2 = typeArray.getEnum(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_btn_bottom2_style_flui,
                ButtonBottomStyle.BNT_BOTTOM_WHITE
            )

            val txtTitleStyle = typeArray.getEnum(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_txt_title_style_flui,
                TxtTitleStyle.TXT_TITLE_BLUE
            )

            val txtSubTitleStyle = typeArray.getEnum(
                R.styleable.bottom_sheet_generic_attributes_flui_bs_txt_subtitle_style_flui,
                TxtSubTitleStyle.TXT_SUBTITLE_BLACK
            )

            //########################################
            // styles
            //########################################
            //button bottom

            when (btnBottomStyle1) {
                ButtonBottomStyle.BNT_BOTTOM_WHITE -> {
                    retryButton1.setBackgroundResource(R.drawable.btn_bottom_unselector)
                    retryButton1.setAppearance(context, R.style.BntBottomBackgroundWhite)
                }
                ButtonBottomStyle.BNT_BOTTOM_BLUE -> {
                    retryButton1.setBackgroundResource(R.drawable.btn_bottom_selector)
                    retryButton1.setAppearance(context, R.style.BntBottomBackgroundBlue)
                }
            }

            when (btnBottomStyle2) {
                ButtonBottomStyle.BNT_BOTTOM_WHITE -> {
                    retryButton2.setBackgroundResource(R.drawable.btn_bottom_unselector)
                    retryButton2.setAppearance(context, R.style.BntBottomBackgroundWhite)
                }
                ButtonBottomStyle.BNT_BOTTOM_BLUE -> {
                    retryButton2.setBackgroundResource(R.drawable.btn_bottom_selector)
                    retryButton2.setAppearance(context, R.style.BntBottomBackgroundBlue)
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
                TxtSubTitleStyle.TXT_SUBTITLE_BLACK_JUSTIFIED -> {
                    TextViewCompat.setTextAppearance(
                        txt_subtitle,
                        R.style.TxtSubtitleBlackNormalJustified
                    )
                    txt_subtitle.gravity = Gravity.START
                    txt_subtitle.invalidate()
                }
            }
            val img = typeArray.getDrawable(R.styleable.bottom_sheet_generic_attributes_flui_bs_image_flui)

            //########################################
            // widget
            //########################################
            txt_title.text = title
            txt_subtitle.text = subtitle
            retryButton1.setText(btn1BottomName.toString())
            retryButton2.setText(btn2BottomName.toString())
            img_bottom_sheet.background = img

            //########################################
            // regra de negócio
            //########################################
            retryButton1.visibility = if (btnBottomStatusFirst) View.VISIBLE else View.INVISIBLE
            retryButton2.visibility = if (btnBottomStatusSecond) View.VISIBLE else View.INVISIBLE
            view1.visibility = if (view1LineStatus) View.VISIBLE else View.INVISIBLE
            txt_title.visibility = if (titleBottomStatus) View.VISIBLE else View.INVISIBLE
            txt_subtitle.visibility = if (subtitleBottomStatus) View.VISIBLE else View.INVISIBLE
            img_bottom_sheet.visibility = if (imageBottomStatus) View.VISIBLE else View.GONE

            typeArray.recycle()
        }
    }
}

fun br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceButtonView.setAppearance(context: Context, res: Int) {
    if (Build.VERSION.SDK_INT < 23) {
        setTextAppearance(context, res)
    } else {
        setTextAppearance(res)
    }
}


