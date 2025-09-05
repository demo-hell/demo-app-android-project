package br.com.mobicare.cielo.component.selectBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.CustomHeightBottomSheet
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.visible
import kotlinx.android.synthetic.main.layout_item_select_bottom_sheet.view.*
import kotlinx.android.synthetic.main.layout_select_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_select_bottom_sheet.errorLayout
import kotlinx.android.synthetic.main.self_service_supply_fragment.*

enum class SelectBottomSheetEnum(val size: Float) {
    FULLSCREEN(0f),
    MEDIUM(0.85f),
    SMALL(0.40f)
}

class SelectBottomSheet<T> private constructor(builder: Builder<T>) : CustomHeightBottomSheet() {

    private var title: String? = null
    private var list: ArrayList<SelectItem<T>>? = null
    private var isShowSearchBar: Boolean = false
    private var isShowSearchIcon: Boolean = false
    private var hintSearchBar: String? = null
    private var listener: OnItemListener? = null
    private var errorListener: OnErrorListener? = null
    private var bottomSheetHeight: SelectBottomSheetEnum = SelectBottomSheetEnum.FULLSCREEN

    init {
        this.title = builder.title
        this.list = builder.list
        this.hintSearchBar = builder.hintSearchBar
        this.isShowSearchIcon = builder.isShowSearchIcon
        this.isShowSearchBar = builder.isShowSearchBar
        this.listener = builder.listener
        this.errorListener = builder.errorListener
        this.bottomSheetHeight = builder.bottomSheetHeight
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.layout_select_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        configureViews()
        configureListeners()
        loadItems()
    }

    fun showBottomSheet(fragmentManager: FragmentManager) {
        if (!isAdded) {
            bottomSheetCustomHeight = this.bottomSheetHeight
            show(fragmentManager, "SelectBottomSheet")
        }
    }

    private fun loadItems(filterText: String = "") {
        this.list?.let { itList ->
            var filteredList = itList
            val filterTextLowerCase = filterText.toLowerCasePTBR()
            if (filterText.isNotEmpty()) {
                filteredList = ArrayList(itList.filter { it.label.toLowerCasePTBR().contains(filterTextLowerCase) })
            }
            val adapter = DefaultViewListAdapter<SelectItem<T>>(
                    filteredList,
                    R.layout.layout_item_select_bottom_sheet
            )
            adapter.setBindViewHolderCallback(object :
                    DefaultViewListAdapter.OnBindViewHolder<SelectItem<T>> {
                override fun onBind(item: SelectItem<T>, holder: DefaultViewHolderKotlin) {
                    holder.mView.tvText?.text = item.label
                    holder.mView.setOnClickListener {
                        this@SelectBottomSheet.listener?.onItemSelected(item.item as Any)
                        this@SelectBottomSheet.dismissAllowingStateLoss()
                    }
                }
            })
            this.rvList?.adapter = adapter
        }
    }

    private fun configureRecyclerView() {
        this.rvList?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun configureViews() {
        this.tvTitle?.text = this.title
        this.filtroInputView?.setHint(this.hintSearchBar ?: "")
        this.filtroInputView?.visible(isShowSearchBar)
        this.imageViewSearchIcon.visible(isShowSearchIcon)
    }

    private fun configureListeners() {
        this.filtroInputView?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                loadItems(s.toString())
            }
        })
        errorListener?.let {
            nestedScrollViewContent.gone()
            this.errorLayout.visible()
            this.errorLayout?.cieloErrorMessage = getString(R.string.text_message_generic_error)
            this.errorLayout?.configureButtonLabel(getString(R.string.text_try_again_label))
            this.errorLayout?.cieloErrorTitle = getString(R.string.text_title_generic_error)
            this.errorLayout?.errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
            this.errorLayout?.configureActionClickListener {
                this.errorListener?.onClickErrorCallback()
                dismiss()
            }
        }
    }

    override fun onBackClicked() {
        this.dismiss()
    }

    class Builder<T> {
        var title: String? = null
            private set

        var isShowSearchBar: Boolean = false
            private set

        var isShowSearchIcon: Boolean = false
            private set

        var list = ArrayList<SelectItem<T>>()
            private set

        var hintSearchBar: String? = null
            private set

        var listener: OnItemListener? = null
            private set

        var errorListener: OnErrorListener? = null

        var bottomSheetHeight: SelectBottomSheetEnum = SelectBottomSheetEnum.FULLSCREEN
            private set

        fun title(text: String) = apply { this.title = text }
        fun isShowSearchBar(isShow: Boolean) = apply { this.isShowSearchBar = isShow }
        fun isShowSearchIcon(isShow: Boolean) = apply { this.isShowSearchIcon = isShow }
        fun height(height: SelectBottomSheetEnum) = apply { this.bottomSheetHeight = height }
        fun list(values: ArrayList<SelectItem<T>>) = apply { this.list = values }
        fun hintSearchBar(text: String) = apply { this.hintSearchBar = text }
        fun listener(callback: OnItemListener) = apply { this.listener = callback }
        fun errorListener(errorCallback: OnErrorListener) =
            apply { this.errorListener = errorCallback }

        fun build() = SelectBottomSheet<T>(this)
    }

    interface OnItemListener {
        fun onItemSelected(item: Any)
    }

    interface OnErrorListener {
        fun onClickErrorCallback()
    }
}