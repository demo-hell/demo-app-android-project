package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.message

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_item_selector_bottom_sheet.view.*
import kotlinx.android.synthetic.main.layout_selector_bottom_sheet.*

private const val SLIDE = -1.0f
private const val SIZE_SCREEN = 0.44f

class SelectorBottomSheet : BottomSheetDialogFragment() {

    private var items: List<String> = ArrayList()
    private var adapter: DefaultViewListAdapter<String>? = null
    private var textAlignment = View.TEXT_ALIGNMENT_TEXT_START
    private var isCollapsed = false

    private lateinit var callback: (String) -> (Unit)

    companion object {
        private const val ITEMS_PARAM = "ITEMS_PARAMS"
        private const val ALIGN_PARAM = "ALIGN_PARAM"
        private const val TITLE_PARAM = "TITLE_PARAM"
        private const val IS_COLLAPSED_PARAM = "IS_COLLAPSED_PARAM"

        fun create(
                items: ArrayList<String>,
                align: Int = View.TEXT_ALIGNMENT_TEXT_START,
                title: String? = null,
                isCollapsed: Boolean = false,
                result: (String) -> Unit
        ) =
                SelectorBottomSheet().apply {
                    this.arguments = Bundle().apply {
                        putSerializable(ITEMS_PARAM, items)
                        putInt(ALIGN_PARAM, align)
                        putString(TITLE_PARAM, title)
                        putBoolean(IS_COLLAPSED_PARAM, isCollapsed)
                    }
                    this.callback = result
                }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_selector_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        loadValues()
        fillSelector()
    }

    private fun configureRecyclerView() {
        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView?.addItemDecoration(
                DividerItemDecoration(
                        requireContext(),
                        RecyclerView.VERTICAL
                )
        )
    }

    private fun loadValues() {
        this.arguments?.getString(TITLE_PARAM)?.let { itTitle ->
            this.tvTitle?.visible()
            this.tvTitle?.text = itTitle
        }
        this.arguments?.getSerializable(ITEMS_PARAM)?.let {
            this.items = it as List<String>
        }
        this.arguments?.getInt(ALIGN_PARAM)?.let {
            this.textAlignment = it
        }
        this.arguments?.getBoolean(IS_COLLAPSED_PARAM, false)?.let {
            this.isCollapsed = it
        }
    }

    private fun fillSelector() {
        adapter =
                DefaultViewListAdapter(items, R.layout.layout_item_selector_bottom_sheet)
        adapter?.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<String> {
            override fun onBind(item: String, holder: DefaultViewHolderKotlin) {
                holder.mView.valueTextView?.text = item
                holder.mView.valueTextView?.textAlignment = this@SelectorBottomSheet.textAlignment
                holder.mView.contentLayout?.setOnClickListener {
                    this@SelectorBottomSheet.callback(item)
                    this@SelectorBottomSheet.dismiss()
                }
            }
        })
        recyclerView?.adapter = adapter
    }

    /**
     *onCreateDialog
     * @param savedInstanceState
     * @return dialog
     * */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        changeDialog(dialog)

        dialog.setOnKeyListener { itDialog, keyCode, _ ->
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                itDialog.dismiss()
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
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * SIZE_SCREEN).toInt()

            behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset == SLIDE) {
                        this@SelectorBottomSheet.dismiss()
                    }
                }
            })
        }
    }
}