package br.com.mobicare.cielo.arv.presentation.historic.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_HISTORIC_DETAILS
import br.com.mobicare.cielo.arv.data.model.response.Item
import br.com.mobicare.cielo.arv.presentation.historic.details.adapter.ArvHistoricDetailsCardAdapter
import br.com.mobicare.cielo.arv.presentation.historic.details.model.ArvHistoricDetailsCardModel
import br.com.mobicare.cielo.arv.presentation.model.enum.ReceivableStatusEnum
import br.com.mobicare.cielo.arv.utils.ArvConstants.ARV_HISTORIC_DETAILS_NEGOTIATION_ARGS
import br.com.mobicare.cielo.commons.constants.Text.PERCENT
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.recycler.CircleIndicatorItemDecoration
import br.com.mobicare.cielo.databinding.FragmentArvHistoricDetailsBinding
import org.koin.android.ext.android.inject

class ArvHistoricDetailsFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentArvHistoricDetailsBinding? = null
    private var navigation: CieloNavigation? = null
    private var generalInformationAdapter: ArvHistoricDetailsCardAdapter? = null
    private val arvAnalytics: ArvAnalyticsGA4 by inject()

    private val negotiation: Item? by lazy {
        arguments?.getParcelable(ARV_HISTORIC_DETAILS_NEGOTIATION_ARGS)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = FragmentArvHistoricDetailsBinding.inflate(inflater, container, false)
            .also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupView()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        arvAnalytics.logScreenView(SCREEN_VIEW_ARV_HISTORIC_DETAILS)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(true)
            navigation?.setupToolbar(
                    title = getString(R.string.txt_arv_historic_details_title),
                    isCollapsed = false
            )
        }
    }

    private fun setupView() {
        negotiation?.let {
            binding?.apply {
                tvValueNetAmount.text = it.netAmount?.toPtBrRealString()
                tvValueNetAmount.contentDescription = AccessibilityUtils.convertAmount(it.netAmount
                        ?: ZERO_DOUBLE, requireContext())
                tvValueGrossAmount.text = it.grossAmount?.toPtBrRealString()
                tvValueGrossAmount.contentDescription = AccessibilityUtils.convertAmount(it.grossAmount
                        ?: ZERO_DOUBLE, requireContext())
                tvValueDiscountAmount.text = it.discountAmount?.toPtBrWithNegativeRealString()
                tvValueDiscountAmount.contentDescription = AccessibilityUtils.convertAmount(it.discountAmount
                        ?: ZERO_DOUBLE, requireContext())
                tvValueStatus.text = it.status

                ReceivableStatusEnum.values().forEach { status ->
                    if (it.status == status.status) {
                        ivIconStatus.setImageResource(status.getIcon())
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        negotiation?.let {
            generalInformationAdapter = ArvHistoricDetailsCardAdapter()
            generalInformationAdapter?.setItems(listOf(
                    ArvHistoricDetailsCardModel(
                            labelOne = getString(R.string.txt_arv_historic_details_label_type_anticipation),
                            valueOne = it.modality,
                            iconOne = R.drawable.ic_money_coin_down_cloud_300_16dp,
                            labelTwo = getString(R.string.txt_arv_historic_details_label_agenda),
                            valueTwo = it.negotiationType,
                            iconTwo = R.drawable.ic_cielo_machine_cloud_300_16dp
                    ),
                    ArvHistoricDetailsCardModel(
                            labelOne = getString(R.string.txt_arv_historic_details_label_date_hiring),
                            valueOne = it.negotiationDate.dateFormatToBr(),
                            iconOne = R.drawable.ic_date_time_calendar_cloud_300_16dp,
                            labelTwo = getString(R.string.txt_arv_historic_details_label_transaction_fee),
                            valueTwo = it.negotiationFee?.toPtBrRealStringWithoutSymbol() + PERCENT,
                            iconTwo = R.drawable.ic_chart_percent_cloud_300_16dp,
                    ),
                    ArvHistoricDetailsCardModel(
                            labelOne = getString(R.string.txt_arv_historic_details_label_operation_number),
                            valueOne = it.operationNumber,
                            iconOne = R.drawable.ic_symbol_info_cloud_300_16dp
                    )
            ))
            binding?.rvGeneralInformation?.apply {
                layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
                adapter = generalInformationAdapter
                addItemDecoration(CircleIndicatorItemDecoration(context))
            }
        }
    }

}