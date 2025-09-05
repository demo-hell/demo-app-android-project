package br.com.mobicare.cielo.commons.bottomsheet.selectItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.commons.bottomsheet.selectItem.adapter.SelectItemBottomSheetAdapter
import br.com.mobicare.cielo.commons.bottomsheet.selectItem.model.RowSelectItemModel
import br.com.mobicare.cielo.databinding.SelectItemBottomSheetBinding
import br.com.mobicare.cielo.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectItemBottomSheet : BottomSheetDialogFragment() {

    private var binding: SelectItemBottomSheetBinding? = null

    private var title: String? = null
    private lateinit var rowSelectItems: List<RowSelectItemModel>

    companion object {
        fun onCreate(
            rowSelectItems: List<RowSelectItemModel>,
            title: String? = null
        ) = SelectItemBottomSheet().apply {
            this.rowSelectItems = rowSelectItems
            this.title = title
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return SelectItemBottomSheetBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        binding?.apply {
            title?.let {
                txtTitle.visible()
                txtTitle.text = it
            }

            rows.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            rows.adapter = SelectItemBottomSheetAdapter(
                rowSelectItems,
                closeBottomSheet = { dismiss() }
            )
        }
    }

}