package br.com.mobicare.cielo.meuCadastroNovo.presetantion.userAdditionalInfoChange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DividerItemDecoration
import br.com.cielo.libflue.bottomsheet.adapter.CieloListBottomSheetRecyclerViewAdapter
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.BottomSheetSimpleFieldBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SimpleFieldBottomSheet<T> : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSimpleFieldBinding
    private var adapter: CieloListBottomSheetRecyclerViewAdapter<T>? = null
    private lateinit var screenTitle: String
    private var layoutItemRes: Int = R.layout.item_additional_simple_field
    private lateinit var data: List<T>
    private lateinit var buttonClick: (SimpleFieldBottomSheet<T>) -> Unit
    private var onViewBound: ((T, Boolean, View) -> Unit)? = null
    private var onItemClicked: ((T, Int, SimpleFieldBottomSheet<T>) -> Unit)? = null

    companion object {
        fun <T> create(
            screenTitle: String,
            @LayoutRes layoutItemRes: Int,
            data: List<T>,
            buttonClick: (SimpleFieldBottomSheet<T>) -> Unit,
            onViewBound: ((T, Boolean, View) -> Unit)? = null,
            onItemClicked: ((T, Int, SimpleFieldBottomSheet<T>) -> Unit)? = null
        ) = SimpleFieldBottomSheet<T>().apply {
            this.screenTitle = screenTitle
            this.layoutItemRes = layoutItemRes
            this.data = data
            this.buttonClick = buttonClick
            this.onViewBound = onViewBound
            this.onItemClicked = onItemClicked
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSimpleFieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureBottomSheet()
        setupView()
        configureAdapter()
    }

    private fun configureBottomSheet() {
        dialog?.let {dialog ->
            dialog.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                val behavior = BottomSheetBehavior.from(bottomSheet)
                val layoutParams = bottomSheet.layoutParams

                val displayMetrics = resources.displayMetrics
                layoutParams.height = (displayMetrics.heightPixels * 0.6).toInt()
                bottomSheet.layoutParams = layoutParams

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = false
            }
        }
    }

    private fun setupView() {
        binding.apply {
            title.text = screenTitle
            binding.btnContinue.setOnClickListener {
                buttonClick.invoke(this@SimpleFieldBottomSheet)
            }
        }
    }

    private fun configureAdapter() {
        adapter = CieloListBottomSheetRecyclerViewAdapter<T>(
            layoutRes = layoutItemRes,
            data = data,
            onViewBound = { data, itemView, isSelected ->
                onViewBound?.invoke(data, isSelected, itemView)
            },
            onItemClicked = { item, position ->
                binding.btnContinue.isEnabled = true
                onItemClicked?.invoke(item, position, this@SimpleFieldBottomSheet)
            }
        ).also { adapter ->
            binding.recyclerView.apply {
                this.adapter = adapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }

    fun updateSelectedPosition(position: Int) {
        adapter?.updateSelectedPosition(position)
        binding.recyclerView.invalidate()
    }
}