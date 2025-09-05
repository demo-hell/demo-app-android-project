package br.com.mobicare.cielo.taxaPlanos.presentation.ui.taxasBandeiras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrands
import br.com.mobicare.cielo.meuCadastro.domains.entities.Products
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_bandeiras_habilitadas_fragment.*


/**
 * Created by benhur.souza on 26/04/2017.
 */

class BandeirasHabilitadasFragment : BaseFragment(), FeesAdapter.OnClickItemFee {

    private var bandeira: CardBrands = CardBrands()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bandeira = arguments?.getSerializable(BANDEIRA) as CardBrands
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.item_bandeiras_habilitadas_fragment, container, false)
    }

    private fun configureLayoutSeeMore(){
        layout_see_more.setOnClickListener{
            callDialog()
        }
    }
    private fun configureClickDetails() {
        textBrandsAndTaxesDetails.setOnClickListener {
            callDialog()
        }
    }

    private fun configureClickCard() {
        card_fees.setOnClickListener {
          callDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        setBandeira()
        configureClickDetails()
        configureLayoutSeeMore()
    }

    fun setBandeira(){
        recycler_view_fees.layoutManager = LinearLayoutManager(context)

        if (bandeira.products.size >= CARD_PRODUCTS_THRESHOLD) {
            layout_see_more.visibility = View.VISIBLE
            configureClickCard()
        }

        var account = 1
        var listProd = ArrayList<Products>()

       bandeira.products.forEach {
           if(account < CARD_PRODUCTS_THRESHOLD){
               listProd.add(it)
               account += 1
           }
       }


        recycler_view_fees.adapter = FeesAdapter(listProd, this)
        recycler_view_fees.isEnabled = false
        recycler_view_fees.setOnTouchListener(View.OnTouchListener { v, event -> true })

        //set imagem logo
        if (!bandeira.imageURL.isNullOrEmpty()){
            Picasso.get()
                    .load(bandeira.imageURL)
                    .placeholder(R.drawable.placeholder_card)
                    .into(imageview_domicilio_bancario_header)
        }
    }

    companion object {
        private const val BANDEIRA = "bandeira"

        const val CARD_PRODUCTS_THRESHOLD = 5

        fun newInstance(bandeira: CardBrands): BandeirasHabilitadasFragment {
            val fragmentFirst = BandeirasHabilitadasFragment()
            val args = Bundle()
            args.putSerializable(BANDEIRA, bandeira)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }

    override fun onClickItem() {
        callDialog()
    }

    fun callDialog(){
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
            action = listOf(fragmentCurrentName()),
            label = listOf("Ver Mais")
        )

        val dialog = CardBrandFeeDetailsDialogFragment.create(bandeira)
        val fm = requireActivity().supportFragmentManager

        dialog.setStyle(androidx.fragment.app.DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen)
        dialog.show(fm, "Cartões")
    }

    private fun fragmentCurrentName() = "Inicio/MeuCadastro"
}
