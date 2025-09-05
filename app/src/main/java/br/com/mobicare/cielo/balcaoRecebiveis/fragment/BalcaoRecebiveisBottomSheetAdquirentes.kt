package br.com.mobicare.cielo.balcaoRecebiveis.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.merchant.domain.entity.Adquirer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.item_rv_list_bc.view.*
import kotlinx.android.synthetic.main.layout_balcao_list_cd_bc.*

class BalcaoRecebiveisBottomSheetAdquirentes : BottomSheetDialogFragment() {

    lateinit var adquirentes: List<Adquirer>
    lateinit var nameList: String

    companion object {
        fun newInstance(
            adquirentes: List<Adquirer>,
            nameList: String
        ): BalcaoRecebiveisBottomSheetAdquirentes {
            return BalcaoRecebiveisBottomSheetAdquirentes().apply {
                this.adquirentes = adquirentes
                this.nameList = nameList
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =
            inflater.inflate(R.layout.layout_balcao_list_cd_bc, container, false)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState >= 4) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }
        populateView()
    }

    private fun populateView() {
        name_bt_items.text = nameList

        rv_list_itens.layoutManager = LinearLayoutManager(context)
        rv_list_itens?.setHasFixedSize(true)
        val adapter = DefaultViewListAdapter(adquirentes, R.layout.item_rv_list_bc)
        adapter.setBindViewHolderCallback(object: DefaultViewListAdapter.OnBindViewHolder<Adquirer> {
            var isClickSetinhaDown = false
            override fun onBind(item: Adquirer, holder: DefaultViewHolderKotlin) {
                holder.mView.item_name_bc.text = "${item.name}"

            }

        })
        adapter.onItemClickListener = object : DefaultViewListAdapter.OnItemClickListener<Adquirer> {
            override fun onItemClick(item: Adquirer) {

            }
        }
        rv_list_itens.adapter = adapter

    }
}