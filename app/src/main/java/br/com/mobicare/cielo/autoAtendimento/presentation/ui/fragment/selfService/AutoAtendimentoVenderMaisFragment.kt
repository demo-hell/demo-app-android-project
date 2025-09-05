package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.presentation.presenter.AutoAtendimentoContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.adapter.PraVenderMaisAdapter
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.Text.AUTOATENDIMENTO
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.analytics.pipeJoin
import br.com.mobicare.cielo.lighthouse.ui.activities.LightHouseActivity
import br.com.mobicare.cielo.migration.presentation.presenter.ItemPraVenderMais
import kotlinx.android.synthetic.main.auto_atendimento_para_vender_mais_fragment.*
import org.jetbrains.anko.startActivity


class AutoAtendimentoVenderMaisFragment : BaseFragment(), AutoAtendimentoContract.View {

    lateinit var adapter: PraVenderMaisAdapter

    companion object {
        val title: String = "Cielo Farol"
        val subtitle: String = "Mantenha-se atualizado sobre o mercado e sobre seu negócio"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.auto_atendimento_para_vender_mais_fragment, container, false)

    @SuppressLint("NewApi")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //if (UserPreferences.getInstance().statusFarolNew) {
        cl_vendermais_error.gone()
        cl_vendermais_sucess.visible()
        /*} else {
            cl_vendermais_error.visible()
            cl_vendermais_sucess.gone()
        }*/

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.farol)
        drawable?.run {
            val item = ItemPraVenderMais(title, subtitle, this, 1)
            val listPraVenderMais = ArrayList<ItemPraVenderMais>()
            listPraVenderMais.add(item)
            adapter = PraVenderMaisAdapter(listPraVenderMais, requireActivity(), this@AutoAtendimentoVenderMaisFragment)
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerPraVenderMais.layoutManager = linearLayoutManager
            recyclerPraVenderMais.adapter = adapter
            LinearSnapHelper().attachToRecyclerView(recyclerPraVenderMais)
        }


    }

    override fun onResume() {
        super.onResume()
        Analytics.trackScreenView(
            screenName = pipeJoin(Category.APP_CIELO, AUTOATENDIMENTO),
            screenClass = this.javaClass
        )
    }

    override fun selectItemPraVenderMais(itemObj: ItemPraVenderMais) {
        super.selectItemPraVenderMais(itemObj)
        when (itemObj.id) {
            1 -> {
                requireActivity().startActivity<LightHouseActivity>()
            }
        }
    }

}