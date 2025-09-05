package br.com.mobicare.cielo.taxaPlanos.componentes.taxas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_all_brands_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_brand_item.view.*

class CieloAllBrandsBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.layout_all_brands_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        loadBrands()
    }

    private fun configureRecyclerView() {
        this.rvBrands?.layoutManager = GridLayoutManager(requireContext(), 5)
        this.rvBrands?.setHasFixedSize(true)
    }

    private fun loadBrands() {
        this.arguments?.getSerializable(CieloAllBrandsBottomSheet.ARG_BRANDS_LIST)?.let {
            val brands = it as ArrayList<String>
            val adapter = DefaultViewListAdapter(brands, R.layout.layout_brand_item)
            adapter.setBindViewHolderCallback(object: DefaultViewListAdapter.OnBindViewHolder<String> {
                override fun onBind(url: String, holder: DefaultViewHolderKotlin) {
                    holder.mView.ivBrand?.let { itBrandView ->
                        ImageUtils.loadImage(itBrandView, url, R.drawable.ic_generic_brand)
                    }
                }
            })
            this.rvBrands?.adapter = adapter
        }
    }

    companion object {
        private const val ARG_BRANDS_LIST = "ARG_BRANDS_LIST"

        @JvmStatic
        fun create(brands: ArrayList<String>) =
            CieloAllBrandsBottomSheet().apply {
                this.arguments  = Bundle().apply {
                    this.putSerializable(ARG_BRANDS_LIST, brands)
                }
            }
    }


}