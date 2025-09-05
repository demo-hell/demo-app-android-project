//package br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.commons.ui.BaseFragment
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters.DadosBancarioAdapter
//import kotlinx.android.synthetic.main.dados_bancarios.*
//
//
//@SuppressLint("ValidFragment")
//class DadosBancarioFragment(var meuCadastroObj: MeuCadastroObj?) : BaseFragment() {
//
//    lateinit var adapter: DadosBancarioAdapter
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
//            inflater.inflate(R.layout.dados_bancarios, container, false)
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//        meuCadastroObj?.apply {
//            typefaceTextView22.visibility = View.VISIBLE
//
//            adapter = DadosBancarioAdapter(meuCadastroObj!!.bankDatas, requireContext(), requireActivity())
//            val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val inflatedView = inflater.inflate(R.layout.dados_bancarios_footer, null)
//            lv_meus_bancos.addFooterView(inflatedView)
//            lv_meus_bancos.adapter = adapter
//
//        } ?: run {
//            typefaceTextView22.visibility = View.GONE
//        }
//
//
//    }
//
//}