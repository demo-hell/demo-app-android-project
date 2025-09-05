package br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.contractinstallments

import android.content.res.ColorStateList
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.size
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.SHORT_MONTH_DESCRIPTION
import br.com.mobicare.cielo.commons.utils.format
import br.com.mobicare.cielo.recebaMais.RM_HELP_ID
import br.com.mobicare.cielo.recebaMais.domains.entities.ContractDetailsResponse
import br.com.mobicare.cielo.recebaMais.domains.entities.InstallmentDetails
import br.com.mobicare.cielo.recebaMais.domains.entities.InstallmentStatus
import br.com.mobicare.cielo.recebaMais.presentation.ui.dialog.DetailsContractBottomSheetFragment
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.TAG
import kotlinx.android.synthetic.main.fragment_contract_installments.*
import kotlinx.android.synthetic.main.item_contract_installments.view.*

private const val SIZE_FOR_SHOW_MORE_ITEMS = 10
private const val START_INIT_SHOW_INDEX = 0
private const val END_INIT_SHOW_INDEX = 10
private const val SIZE_INIT_VIEWS = 11
private const val CONTRACT_DETAIL_ARGS = "CONTRACT_DETAIL_ARGS"

class ContractInstallmentsFragment : BaseFragment(), CieloNavigationListener {

    private var cieloNavigation: CieloNavigation? = null
    private var contractDetailsResponse: ContractDetailsResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contractDetailsResponse = it.getParcelable(CONTRACT_DETAIL_ARGS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contract_installments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureNavigation()
        init()
    }

    private fun init() {
        contractDetailsResponse?.contracts?.let { contracts ->
            val installments = contracts.toMutableList()[0].installments.toMutableList()
            setupAddItems(isInit = true, installments = installments)

            var isClickedShowMore = false
            if (installments.size <= SIZE_FOR_SHOW_MORE_ITEMS) {
                textViewShowMore.gone()
            } else {
                textViewShowMore.setOnClickListener {
                    if (isClickedShowMore) {
                        TransitionManager.beginDelayedTransition(linarLayoutIntallments, AutoTransition())
                        linarLayoutIntallments.removeViews(SIZE_INIT_VIEWS, linarLayoutIntallments.childCount - SIZE_INIT_VIEWS)
                        scrollContentLayout.scrollTo(0, 0)
                        setupAddItems(isInit = true, installments = installments)
                        isClickedShowMore = false
                        textViewShowMore.text = getString(R.string.text_receba_mais_contract_installment_show_more)
                    } else {
                        setupAddItems(isInit = false, installments = installments)
                        isClickedShowMore = true
                        textViewShowMore.text = getString(R.string.text_receba_mais_contract_installment_show_less)
                    }
                }
            }
        }

    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.text_receba_mais_contract_installment_toolbar_title))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.setNavigationListener(this)
            this.cieloNavigation?.showContent(true)
            this.cieloNavigation?.showHelpButton(true)
        }
    }

    private fun setupAddItems(isInit: Boolean, installments: MutableList<InstallmentDetails>) {
        val endIndex = if (installments.size < END_INIT_SHOW_INDEX) installments.size
        else END_INIT_SHOW_INDEX

        if (isInit) addItems(isInit, installments.subList(START_INIT_SHOW_INDEX, endIndex))
        else addItems(isInit, installments.subList(SIZE_FOR_SHOW_MORE_ITEMS, installments.size))
    }

    private fun addItems(isInit: Boolean, installments: MutableList<InstallmentDetails>) {
        if (isInit && linarLayoutIntallments.size > 1)
            linarLayoutIntallments.removeViewsInLayout(1, installments.size)

        else
            linarLayoutIntallments?.getChildAt(SIZE_FOR_SHOW_MORE_ITEMS)?.viewLineSeparator?.visible()

        TransitionManager.beginDelayedTransition(linarLayoutIntallments, AutoTransition())
        installments.forEachIndexed { index, installment ->

            val viewItem = LayoutInflater
                    .from(requireContext())
                    .inflate(R.layout.item_contract_installments, null)

            val shortMonth = DataCustomNew()
                    .setDateFromAPI(installment.dueDate)
                    .toCalendar()
                    .format(SHORT_MONTH_DESCRIPTION)
                    .capitalize()

            var text = String.format(getString(R.string.text_receba_mais_contract_installment_item),
                    installment.installmentNumber, shortMonth)

            if (installment.statusCode == InstallmentStatus.OPENED.code) {
                viewItem.imageViewDate
                        .setImageDrawable(ContextCompat
                                .getDrawable(requireContext(), R.drawable.ic_flui_calendar))
                viewItem.imageViewDate.supportImageTintList = ColorStateList
                        .valueOf(ContextCompat.getColor(requireContext(), R.color.brand_500))
                text = text.plus(" - ${InstallmentStatus.OPENED.text}")
            }
            viewItem.textViewInstalments.text = text

            if (index == (installments.size - 1)) {
                viewItem.viewLineSeparator.gone()
            }

            linarLayoutIntallments.addView(viewItem)
            viewItem.setOnClickListener {
                DetailsContractBottomSheetFragment.create(installment).show(childFragmentManager, TAG)
            }
        }
    }

    override fun onHelpButtonClicked() {
        super.onHelpButtonClicked()
        HelpMainActivity.create(requireActivity(), getString(R.string.text_rm_help_title), RM_HELP_ID)
    }
}