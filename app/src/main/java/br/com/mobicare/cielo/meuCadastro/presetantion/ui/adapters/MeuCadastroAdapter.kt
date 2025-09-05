//package br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters
//
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.FragmentStatePagerAdapter
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
//import br.com.mobicare.cielo.meuCadastro.presetantion.presenter.MeuCadastroPresenter
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.DadosBancarioFragment
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.MinhaEmpresaFragment
////import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.MinhaSolucoesFragment
//import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.DadosContaFragment
//import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.DadosUsuarioFragment
//
//
//class MeuCadastroAdapter(fm: FragmentManager,
//                         var activity: FragmentActivity,
//                         var meuCadastroObj: MeuCadastroObj?,
//                         var bandeiras: CardBrandFees?, var presenterCad: MeuCadastroPresenter) :
//        FragmentStatePagerAdapter(fm) {
//
//    override fun getItem(position: Int): Fragment {
//        //TODO remover repetição de return
//        return when (position) {
//            0 -> MinhaEmpresaFragment(meuCadastroObj, presenterCad)
//            1 -> DadosBancarioFragment(meuCadastroObj)
//            else -> DadosBancarioFragment(meuCadastroObj) //DadosContaFragment()
//        }
//    }
//
//    override fun getCount(): Int {
//        return 3
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        return when (position) {
//            0 -> activity.getString(R.string.title_minha_empresa)
//            1 -> activity.getString(R.string.title_dados_bancarios)
//            else -> activity.getString(R.string.title_minhas_solucoes)
//        }
//    }
//
//
//}