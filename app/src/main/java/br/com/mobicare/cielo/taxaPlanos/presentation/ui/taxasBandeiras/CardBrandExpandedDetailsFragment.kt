package br.com.mobicare.cielo.taxaPlanos.presentation.ui.taxasBandeiras

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.convertDpToPixel
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrands
import kotlinx.android.synthetic.main.fragment_card_brand_expanded_details.*


class CardBrandExpandedDetailsFragment : BaseFragment() {


    companion object {

        const val CARD_BRAND_DETAIL_KEY = "br.com.cielo.cieloAtendimento.cardBrand"

        fun create(cardBrand: CardBrands): androidx.fragment.app.Fragment {
            val fragmentToReturn = CardBrandExpandedDetailsFragment()

            fragmentToReturn.arguments = Bundle().apply {
                putSerializable(CARD_BRAND_DETAIL_KEY, cardBrand)
            }

            return fragmentToReturn
        }
    }

    interface OnCloseActionListener {
        fun close()
    }

    var closeActionListener: OnCloseActionListener? = null

    private val cardBrand: CardBrands
        get() = arguments?.getSerializable(CARD_BRAND_DETAIL_KEY) as CardBrands


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_card_brand_expanded_details,
                container, false)
    }


    override fun onStart() {
        super.onStart()

        Utils.addFontMuseoSans700(activity as Activity, textClose)
        configureTaxList()
        configureCloseTextButton()
        setupScrollListener()
        configureLogo()
    }

    private fun setupScrollListener() {
        recyclerBrandFeeDetails.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {


                val linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager = recyclerView.layoutManager
                        as androidx.recyclerview.widget.LinearLayoutManager

                val visibleElementsCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()

                if (visibleElementsCount + pastVisibleItems >= totalItemCount) {
                    frameBottomTransparency.setPadding(0, 0, 0, 0)
                } else {
                    frameBottomTransparency.setPadding(0,
                            activity!!.convertDpToPixel(20f), 0, 0)
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }


    private fun configureCloseTextButton() {

        textClose.setOnClickListener {
            closeActionListener?.close()
        }
    }


    private fun configureTaxList() {
        recyclerBrandFeeDetails.setHasFixedSize(true)
        recyclerBrandFeeDetails.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        recyclerBrandFeeDetails.adapter = FeesAdapter(cardBrand.products)
    }

    private fun configureLogo(){
        ImageUtils.loadImage(cardBrandLogo, cardBrand.imageURL)
    }

}