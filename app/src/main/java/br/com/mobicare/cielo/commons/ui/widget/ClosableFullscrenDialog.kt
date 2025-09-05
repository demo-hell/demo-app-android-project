package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.login.domains.entities.CieloInfoDialogContent
import br.com.mobicare.cielo.login.presentation.ui.adapters.ClosableDialoglContentAdapter
import kotlinx.android.synthetic.main.closable_fullscreen_dialog.view.*
import kotlinx.android.synthetic.main.toolbar_common_dialogs.view.*

class ClosableFullscrenDialog : RelativeLayout {

    lateinit var listener: OnActionButtonClick

    var cieloInfoDialogContent: CieloInfoDialogContent? = null

    interface OnActionButtonClick {
        fun onButtonClick()
    }

    interface OnCloseActionButton {
        fun onCloseButtonClick()
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        inflate(context, R.layout.closable_fullscreen_dialog, this)
    }


    private fun configureBottomButton(label: String) {
        if (label.isEmpty()) {
            btn_bottom.visibility = View.GONE
        } else {
            btn_bottom.visibility = View.VISIBLE
            btn_bottom.text = label
        }
    }

    fun configElements(fragmentManager: FragmentManager,
                       cieloInfoDialogContent: CieloInfoDialogContent,
                       onCloseActionButtonListener: OnCloseActionButton? = null) {

        this.cieloInfoDialogContent = cieloInfoDialogContent

        txtTitle.text = cieloInfoDialogContent.windowTitle

        if (cieloInfoDialogContent.pageElements.isNotEmpty()) {
            configureBottomButton(cieloInfoDialogContent.pageElements[getCurrentPage()].buttonLabel)
        }

        view_pager_content.adapter = ClosableDialoglContentAdapter(fragmentManager,
                cieloInfoDialogContent)


        if (cieloInfoDialogContent.pageElements.size > 1) {
            circleInfoIndicator.visibility = View.VISIBLE
            circleInfoIndicator.setViewPager(view_pager_content)
        }

        btn_bottom.setOnClickListener {
            if (cieloInfoDialogContent.pageElements.isNotEmpty()) {
                cieloInfoDialogContent.pageElements[getCurrentPage()].listener?.onButtonClick()
            }
        }

        btnRight.setOnClickListener {
            onCloseActionButtonListener?.onCloseButtonClick()
        }
    }

    fun getCurrentPage(): Int = view_pager_content.currentItem

    fun getTotalPages(): Int = view_pager_content.adapter?.count ?: 0

    fun nextPage() {
        val cieloDialogContentAdapter: ClosableDialoglContentAdapter =
                view_pager_content.adapter as ClosableDialoglContentAdapter
        if (getCurrentPage() + 1 < cieloDialogContentAdapter.count) {
            view_pager_content.currentItem = getCurrentPage() + 1
            this.cieloInfoDialogContent?.let {
                configureBottomButton(it.pageElements[getCurrentPage()].buttonLabel)
            }
        }
    }

    fun previousPage() {

        if (getCurrentPage() - 1 > 0) {
            view_pager_content.currentItem = getCurrentPage() + 1
        }

    }

}
