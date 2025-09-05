//package br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.viewpager.widget.ViewPager
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.commons.ui.BaseFragment
//import br.com.mobicare.cielo.commons.analytics.GaLabel
//import br.com.mobicare.cielo.commons.utils.convertDpToPixel
//import br.com.mobicare.cielo.commons.utils.swipeCard
//import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
////import br.com.mobicare.cielo.taxaPlanos.presentation.ui.taxasBandeiras.BandeirasHabilitadasAdapter
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters.MSBandeirasHabilitadasAdapter
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters.MinhasSolucoesAdapter
//import kotlinx.android.synthetic.main.content_bandeiras_habilitadas.*
//import kotlinx.android.synthetic.main.minhas_solucoes.*
//
///**
// * Created by Enzo Teles on 11/03/19
// * email: enzo.carvalho.teles@gmail.com
// * Software Developer Sr.
// */
//
//@SuppressLint("ValidFragment")
//class MinhaSolucoesFragment(var meuCadastroObj: MeuCadastroObj?, var bandeiras: CardBrandFees?) : BaseFragment(){
//
//    lateinit var adapter: MinhasSolucoesAdapter
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
//            inflater.inflate(R.layout.minhas_solucoes, container, false)
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        ms_tv_title_bandeiras_taxas.visibility = if(bandeiras != null) View.VISIBLE else View.GONE
//
//        meuCadastroObj?.apply {
//            layout_solucoes_view.visibility = View.VISIBLE
//            linearBrandsAndTaxes.visibility = View.VISIBLE
//
//            adapter = MinhasSolucoesAdapter(hiredSolutions, requireContext(), requireActivity())
//
//            list_view_solucoes_contratadas.setHasFixedSize(true)
//            list_view_solucoes_contratadas.layoutManager = LinearLayoutManager(view.context)
//            list_view_solucoes_contratadas.adapter = adapter
//
//        } ?:run {
//            layout_solucoes_view.visibility = View.GONE
//            linearBrandsAndTaxes.visibility = View.GONE
//        }
//
//
////        bandeiras?.apply {
////
////            if (isAdded) {
////                if (this.cardBrands.isNotEmpty()) {
////
////                    vp_bandeiras_habilitadas.adapter = BandeirasHabilitadasAdapter(childFragmentManager, *this.cardBrands.toTypedArray())
////                    //vp_bandeiras_habilitadas.offscreenPageLimit = 99
////
////                    vp_bandeiras_habilitadas.pageMargin = requireActivity().convertDpToPixel(4f)
////
////                    indicator_band_hab.setViewPager(vp_bandeiras_habilitadas)
////
////                    vp_bandeiras_habilitadas.addOnPageChangeListener(object :
////                            ViewPager.SimpleOnPageChangeListener() {
////
////                        var lastPosition = 0
////
////                        override fun onPageSelected(position: Int) {
////                            lastPosition = position
////                        }
////
////                    })
////                }
////            }
////        }
//
//
//
//
//
//    }
//
//}