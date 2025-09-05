package br.com.mobicare.cielo.commons.presentation.utils.custom

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class CenterCustomToolbar @JvmOverloads constructor(context: Context,
                                                    attrs: AttributeSet? = null,
                                                    defStyleAttr: Int = androidx.appcompat.R.attr.toolbarStyle) :
        Toolbar(context, attrs, defStyleAttr) {

    private val titleView: TextView
    private val subtitleView: TextView

    init {

        titleView = TextView(getContext())
        subtitleView = TextView(getContext())

        val textAppearanceStyleResId: Int
        val a = context.theme.obtainStyledAttributes(attrs,
                intArrayOf(androidx.appcompat.R.attr.titleTextAppearance), defStyleAttr, 0)
        try {
            textAppearanceStyleResId = a.getResourceId(0, 0)
        } finally {
            a.recycle()
        }
        if (textAppearanceStyleResId > 0) {
            titleView.setTextAppearance(context, textAppearanceStyleResId)
            subtitleView.setTextAppearance(context, textAppearanceStyleResId)
        }

        val LL = LinearLayout(context)
        LL.orientation = LinearLayout.VERTICAL
        val LLParams = Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT)
        LL.layoutParams = LLParams

        LL.addView(titleView)
        LL.addView(subtitleView)

        titleView.setSingleLine(true)
        titleView.ellipsize = TextUtils.TruncateAt.END

        subtitleView.visibility = View.GONE
        subtitleView.alpha = 0.87f

        addView(LL, Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT))

        subtitleView.typeface = Typeface.createFromAsset(context.assets, "fonts/MuseoSans-700.ttf")

        titleView.typeface = Typeface.createFromAsset(context.assets, "fonts/Museo700-Regular.ttf")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        //        titleView.setX((getWidth() - titleView.getWidth())/2);
    }

    override fun setTitle(title: CharSequence) {
        titleView.text = title
    }


    override fun setSubtitle(subtitle: CharSequence) {
        subtitleView.visibility = View.VISIBLE
        subtitleView.text = subtitle
    }

    fun setTextSize(size: Float?) {
        titleView.textSize = size!!
    }

    fun setSubtitleTextSize(size: Float?) {
        subtitleView.visibility = View.VISIBLE
        subtitleView.textSize = size!!
    }


}