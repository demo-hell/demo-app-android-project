package br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.activity.BalcaoRecebiveisExtratoActivity
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.adapter.RecebiveisExtratoPagerAdapter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoFragment.Companion.CIELO
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoFragment.Companion.MARKET
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.CalendarCustom
import br.com.mobicare.cielo.commons.utils.CalendarDialogCustom
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.ComponentFilterListener
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.DEFAULT_DATE_RANGE_DAYS
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.FilterPresenterNew
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.FilterPresenterNewImpl
import br.com.mobicare.cielo.meusrecebimentosnew.models.DayType
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_balcao_recebiveis_extrato_detail.*
import kotlinx.android.synthetic.main.layout_filter_recebiveis_extrato.*

class BalcaoRecebiveisExtratoDetailFragment : BaseFragment(), CieloNavigationListener,
    ComponentFilterListener {

    private lateinit var presenterFilter: FilterPresenterNew
    private var cieloNavigation: CieloNavigation? = null

    private val negotiation: Negotiations? by lazy {
        arguments?.getParcelable(BalcaoRecebiveisExtratoActivity.NEGOTIATIONS_ARGS)
    }
    private val dateInit: String? by lazy {
        arguments?.getString(BalcaoRecebiveisExtratoActivity.DATE_INIT_ARGS)
    }
    private val dateEnd: String? by lazy {
        arguments?.getString(BalcaoRecebiveisExtratoActivity.DATE_END_ARGS)
    }

    private val adapter by lazy {
        RecebiveisExtratoPagerAdapter(
            childFragmentManager,
            negotiation,
            dateInit,
            dateEnd
        )
    }

    private var initialDate: DataCustomNew? = null
    private var finalDate: DataCustomNew? = null

    var type: Int? = 0

    private val daysReceivablesLast: Int
        get() = ConfigurationPreference.instance
            .getConfigurationValue(
                ConfigurationDef
                    .DAYS_RECEIVABLES_LAST, DEFAULT_DATE_RANGE_DAYS
            ).toInt() + 1


    private val daysReceivablesFuture: Int
        get() = ConfigurationPreference.instance
            .getConfigurationValue(
                ConfigurationDef
                    .DAYS_RECEIVABLES_FUTURE, DEFAULT_DATE_RANGE_DAYS
            ).toInt() + 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_balcao_recebiveis_extrato_detail,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureNavigation()
        setup()
    }

    private fun setup() {
        presenterFilter = FilterPresenterNewImpl(this)
        adapter.callBackSeerMore = { itNegotiation ->
            val direction =
                BalcaoRecebiveisExtratoDetailFragmentDirections.actionExtratoDetailToExtratoDetailNegotiations(
                    itNegotiation
                )
            initialDate?.let { direction.setINITIALDATEARGS(it.formatDateToAPI()) }
            finalDate?.let { direction.setFINALDATEARGS(it.formatDateToAPI()) }
            type?.let { direction.setTYPENEGOCIATION(if (it == 0) CIELO else MARKET) }
            findNavController().navigate(direction)
        }

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val dateInitParam = initialDate?.formatDateToAPI() ?: dateInit
                val dateEndParam = finalDate?.formatDateToAPI() ?: dateEnd

                if (dateInitParam == null || dateEndParam == null) {
                    type = tab.position
                    return
                }

                type = if (tab.position == 0) {
                    adapter.fragCielo?.changeNegotiations(dateInitParam, dateEndParam)
                    tab.position
                } else {
                    adapter.fragMarket?.changeNegotiations(dateInitParam, dateEndParam)
                    tab.position
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
        })

        et_date_init.setOnClickListener { onClickDataInicio() }
        et_date_end.setOnClickListener { onClickDataFinal() }

        if (initialDate == null) {
            dateInit?.let {
                initialDate = DataCustomNew()
                initialDate?.setDateFromAPI(it)
                tv_date_init.text = initialDate?.formatBRDate()
            }
        } else {
            tv_date_init.text = initialDate?.formatBRDate()
        }

        if (finalDate == null) {
            dateEnd?.let {
                finalDate = DataCustomNew()
                finalDate?.setDateFromAPI(it)
                tv_date_end.text = finalDate?.formatBRDate()
            }
        } else {
            tv_date_end.text = finalDate?.formatBRDate()
        }
    }

    private fun onClickDataInicio() {
        val cal = initialDate?.toCalendar()
        val dia = cal?.let { CalendarCustom.getDay(it) }
        val mes = cal?.let { CalendarCustom.getMonth(it) }
        val ano = cal?.let { CalendarCustom.getYear(it) }

        if (dia != null && mes != null && ano != null) {
            CalendarDialogCustom(
                -daysReceivablesLast, daysReceivablesFuture, -1, dia, mes, ano,
                getString(R.string.extrato_filtro_data_inicio), context as Context,
                { _, year, monthOfYear, dayOfMonth ->
                    initialDate?.setDate(year, monthOfYear, dayOfMonth)
                    tv_date_init.text = initialDate?.formatBRDate()
                    presenterFilter.callCalculationVision(initialDate, finalDate)
                }, R.style.DialogThemeMeusRecebimentos
            ).show()
        }
    }


    private fun onClickDataFinal() {
        val cal = finalDate?.toCalendar()
        val dia = cal?.let { CalendarCustom.getDay(it) }
        val mes = cal?.let { CalendarCustom.getMonth(it) }
        val ano = cal?.let { CalendarCustom.getYear(it) }
        if (dia != null && mes != null && ano != null) {
            CalendarDialogCustom(
                -daysReceivablesLast, daysReceivablesFuture, -1, dia, mes, ano,
                getString(R.string.extrato_filtro_data_fim), context as Context,
                { _, year, monthOfYear, dayOfMonth ->
                    finalDate?.setDate(year, monthOfYear, dayOfMonth)
                    tv_date_end.text = finalDate?.formatBRDate()
                    presenterFilter.callCalculationVision(initialDate, finalDate)
                }, R.style.DialogThemeMeusRecebimentos
            ).show()
        }
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.title_negociacoes_recebiveis))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(true)
            this.cieloNavigation?.setNavigationListener(this)
            this.cieloNavigation?.showContent(true)
        }
    }

    override fun onClickDate(
        initialDate: String,
        finalDate: String,
        isGraphSelection: Boolean,
        selectedDateType: DayType.Type?
    ) {
        if (viewPager.currentItem == 0) {
            adapter.fragCielo?.changeNegotiations(initialDate, finalDate)
        } else {
            adapter.fragMarket?.changeNegotiations(initialDate, finalDate)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_common_faq, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_help -> {

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showFilterErroAlert() {}
    override fun showGraph(mainDate: DataCustomNew) {}
    override fun hideGraph() {}

}