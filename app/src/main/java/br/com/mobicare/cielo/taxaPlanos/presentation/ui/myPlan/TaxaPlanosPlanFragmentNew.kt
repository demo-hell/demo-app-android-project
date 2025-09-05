package br.com.mobicare.cielo.taxaPlanos.presentation.ui.myPlan

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MODAL
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.component.feeandplans.model.ComponentLayoutFeeAndPlansItem
import br.com.mobicare.cielo.databinding.TaxaPlanosPlanFragmentNewBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUtils
import br.com.mobicare.cielo.newRecebaRapido.presentation.ReceiveAutomaticActivity
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.recebaRapido.cancellation.CancellationRecebaRapidoActivity
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_CONTROLE
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_LIVRE
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_PLAN
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4.Companion.AUTOMATIC_RECEIVE
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4.Companion.CANCEL_RA
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4.Companion.CONFIRM_CANCEL_RA
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4.Companion.CONTENT_INCOMING_SALES
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4.Companion.FEES_AND_PLANS
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4.Companion.SCREEN_VIEW_RA_CANCEL
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4.Companion.UPDATE_INCOMING_SALES
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4.Companion.YES_I_WANT_TO_CANCEL
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosOverviewResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.dialog.RACancelWhatsappDialog
import kotlinx.android.synthetic.main.item_taxa_planos_machine.view.componentItem
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class TaxaPlanosPlanFragmentNew : BaseFragment(),
    TaxaPlanosPlanContract.View {

    private var plan = ""

    val presenter: TaxaPlanosPlanPresenter by inject {
        parametersOf(this)
    }

    private val binding: TaxaPlanosPlanFragmentNewBinding by viewBinding()

    private val ga4: FeesPlansGA4 by inject()

    companion object {
        private const val MODEL_PLANO = "PLANO"

        fun create(planName: String): TaxaPlanosPlanFragmentNew {
            val fragment = TaxaPlanosPlanFragmentNew()
            val bundle = Bundle().apply {
                putString(TAXA_PLANOS_PLAN, planName)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        doWhenResumed {
            InteractBannersUtils.launchInteractBanner(
                bannerType = InteractBannerTypes.LEADERBOARD,
                shouldGetOffersFromApi = false,
                frame = R.id.frameInteractLeaderboardBannersOffers,
                fragmentActivity = requireActivity(),
                bannerControl = BannerControl.LeaderboardFeesAndPlans,
                onSuccess = {
                    doWhenResumed {
                        if (isAdded) {
                            binding.frameInteractLeaderboardBannersOffers.visible()
                        }
                    }
                }
            )
        }

        plan = this.arguments?.getString(TAXA_PLANOS_PLAN, "") ?: ""
        presenter.loadOverview(plan)
        presenter.loadMarchine()
    }

    override fun onResume() {
        super.onResume()
        presenter.getEligibleToOffer()
        ga4.logScreenView(
            screenName = FEES_AND_PLANS
        )
    }

    override fun onDestroy() {
        presenter.onClieared()
        super.onDestroy()
    }

    override fun showMachine(response: TaxaPlanosSolutionResponse) {
        if (isAttached()) {
            binding.recyclerViewMachines.visible()

            var reponseList = ArrayList<TaxaPlanosMachine>()
            response.pos.forEach {
                if (it.model != MODEL_PLANO)
                    reponseList.add(it)
            }

            binding.recyclerViewMachines.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = DefaultViewListAdapter(reponseList, R.layout.item_taxa_planos_machine)
            adapter.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<TaxaPlanosMachine> {
                override fun onBind(item: TaxaPlanosMachine, holder: DefaultViewHolderKotlin) {
                    val list = ArrayList<ComponentLayoutFeeAndPlansItem>()
                    var stringValue = ""
                    if (item.model != MODEL_PLANO)
                        stringValue = when (item.rentalAmount) {
                            0.0 -> getString(R.string.text_view_fee_plan_rent_value)
                            else -> item.rentalAmount?.toPtBrRealString() ?: EMPTY
                        }

                    list.add(
                        ComponentLayoutFeeAndPlansItem(
                            getString(R.string.text_view_fee_plan_rent),
                            stringValue
                        )
                    )

                    list.add(
                        ComponentLayoutFeeAndPlansItem(
                            getString(R.string.text_view_item_logical_number),
                            item.logicalNumber ?: ""
                        )
                    )

                    holder.mView.componentItem.clearContent()
                    holder.mView.componentItem.setContent(
                        item.name.toLowerCasePTBR().capitalizePTBR(), list
                    )
                }
            })
            binding.recyclerViewMachines.adapter = adapter
            binding.recyclerViewMachines.addOnItemTouchListener(object :
                RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    val action = e.action
                    when (action) {
                        MotionEvent.ACTION_MOVE -> rv.parent
                            .requestDisallowInterceptTouchEvent(true)
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
            })
        }
    }

    override fun showOverview(response: TaxaPlanosOverviewResponse) {

        if (isAttached()) {
            binding.layoutOverview.textViewFranchiseValue.text = response.value.toPtBrRealString()

            val listIncoming = ArrayList<ComponentLayoutFeeAndPlansItem>()
            listIncoming.add(
                ComponentLayoutFeeAndPlansItem(
                    getString(R.string.text_view_debit_label),
                    getDays(response.settlementTerm.debit)
                )
            )
            listIncoming.add(
                ComponentLayoutFeeAndPlansItem(
                    getString(R.string.text_view_credit_label),
                    getDays(response.settlementTerm.credit)
                )
            )
            listIncoming.add(
                ComponentLayoutFeeAndPlansItem(
                    getString(R.string.text_view_instalment_label),
                    getDays(response.settlementTerm.installment)
                )
            )
            binding.componentFeeAndPlanIncoming.setContent(
                getString(R.string.text_view_incoming_day_label),
                listIncoming
            )

            when (plan) {
                TAXA_PLANOS_CONTROLE -> Unit
                TAXA_PLANOS_LIVRE -> {
                    binding.layoutOverview.textViewFranchiseLabel.gone()
                    binding.componentTax.visible()
                    binding.componentFeeAndPlanIncoming.showFooter = false
                    binding.layoutOverview.textViewOverviewInfo.text =
                        getString(R.string.txp_livre_min_billing)
                    binding.layoutChangeIncomingSale.textViewFeeLabel.text =
                        getString(R.string.text_view_plan_name_label)
                    binding.layoutOverview.textViewFranchiseValue.text =
                        response.minimumRevenue.toPtBrRealString()
                    binding.layoutChangeIncomingSale.textViewFeeValue.text =
                        response.value.toPtBrRealString()

                    val listTax = ArrayList<ComponentLayoutFeeAndPlansItem>()
                    listTax.add(
                        ComponentLayoutFeeAndPlansItem(
                            getString(R.string.text_view_debit_label),
                            getPercent(response.mdr.debit)
                        )
                    )
                    listTax.add(
                        ComponentLayoutFeeAndPlansItem(
                            getString(R.string.text_view_credit_label),
                            getPercent(response.mdr.credit)
                        )
                    )
                    listTax.add(
                        ComponentLayoutFeeAndPlansItem(
                            getString(R.string.text_view_instalment_label),
                            getPercent(response.mdr.installment)
                        )
                    )
                    binding.componentTax.setContent(
                        getString(R.string.text_view_fee_plan_taxes_title),
                        listTax
                    )
                }
            }
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            binding.layoutErrorFeeAndPlan.constraintLayoutOverviewError.gone()
            binding.layoutChangeIncomingSale.linearLayoutIncomingFastMain.gone()
            binding.layoutOverview.linearLayoutOverView.gone()
            binding.componentFeeAndPlanIncoming.gone()
            binding.shimmerLayout.visible()
            binding.shimmerLayout.startShimmer()
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.gone()
            binding.layoutChangeIncomingSale.linearLayoutIncomingFastMain.visible()
            binding.layoutOverview.linearLayoutOverView.visible()
            binding.componentFeeAndPlanIncoming.visible()
        }
    }

    override fun showMachineError(error: ErrorMessage) {
        if (isAttached()) {
            binding.recyclerViewMachines.gone()
            binding.layoutErrorMachines.constraintLayoutMachinesError.visible()
            binding.layoutErrorMachines.buttonLoadAgainMachines.setOnClickListener {
                presenter.loadMarchine()
            }
        }
    }

    override fun showLoadMachines() {
        binding.layoutErrorMachines.constraintLayoutMachinesError.gone()
        binding.recyclerViewMachines.gone()
        binding.shimmerRecyclerView.startShimmer()
        binding.shimmerRecyclerView.visible()
    }

    override fun hideLoadMachines() {
        binding.shimmerRecyclerView.stopShimmer()
        binding.shimmerRecyclerView.gone()
        binding.recyclerViewMachines.visible()
    }

    override fun hideEmptyMachines() {
        binding.textViewPlanTitle.gone()
        binding.recyclerViewMachines.gone()
    }

    override fun showChangeIncomingButton() {
        binding.layoutChangeIncomingSale.textViewChangeIncoming.visible()
        binding.layoutChangeIncomingSale.textViewChangeIncoming.setOnClickListener {
            ga4.logClick(
                screenName = FEES_AND_PLANS,
                contentComponent = UPDATE_INCOMING_SALES,
                contentName = CONTENT_INCOMING_SALES
            )
            requireContext().startActivity<ReceiveAutomaticActivity>()
        }
    }

    override fun showIncomingWay(isEnabledCancelIncomingFastFT: Boolean) {
        when (plan) {
            TAXA_PLANOS_LIVRE -> {
                binding.layoutChangeIncomingSale.linearLayoutIncomingFast.visible()

                if (isEnabledCancelIncomingFastFT) {
                    val text = SpannableString(
                        HtmlCompat
                            .fromHtml(
                                getString(R.string.text_view_fee_plan_incoming_cancellation),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                    )
                    binding.textViewRRCancellation.setText(text, TextView.BufferType.SPANNABLE)
                    binding.textViewRRCancellation.visible()
                    binding.textViewRRCancellation.setOnClickListener {
                        gaSendButton()
                        val dialog = CieloDialog.create(
                            getString(R.string.receive_fast_cancel_dialog_title),
                            getString(R.string.receive_fast_cancel_dialog_text)
                        ).apply {
                            setTitleTextAlignment(View.TEXT_ALIGNMENT_VIEW_START)
                            setMessageTextAlignment(View.TEXT_ALIGNMENT_VIEW_START)
                            setPrimaryButton(this@TaxaPlanosPlanFragmentNew.getString(R.string.incomint_fast_cancellation_keep_rr))
                            setSecondaryButton(this@TaxaPlanosPlanFragmentNew.getString(R.string.incomint_fast_cancellation_yes_cancel))
                            setOnSecondaryButtonClickListener {
                                ga4.logClick(
                                    screenName = SCREEN_VIEW_RA_CANCEL,
                                    contentComponent = CANCEL_RA,
                                    contentName = YES_I_WANT_TO_CANCEL
                                )
                                dismiss()
                                presenter.confirmCancellation()
                            }

                        }
                        if (dialog.isResumed.not()) {
                            dialog.show(
                                childFragmentManager,
                                TaxaPlanosPlanFragmentNew::class.java.simpleName
                            ).also {
                                ga4.logScreenView(SCREEN_VIEW_RA_CANCEL)
                                ga4.logDisplayContent(
                                    screenName = SCREEN_VIEW_RA_CANCEL,
                                    contentComponent = CONFIRM_CANCEL_RA,
                                    contentType = MODAL
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun showWhatsAppCancellationDialog(whatsappLink: String?) {
        val wadialog = RACancelWhatsappDialog.newInstance(whatsappLink)
        wadialog.show(
            this@TaxaPlanosPlanFragmentNew.childFragmentManager,
            TaxaPlanosPlanFragmentNew::class.java.simpleName
        )
    }

    override fun showCancellationActivity() {
        val intent = Intent(
            this@TaxaPlanosPlanFragmentNew.context,
            CancellationRecebaRapidoActivity::class.java
        )
        intent.putExtra(TAXA_PLANOS_PLAN, plan)
        startActivity(intent)
    }

    override fun showOverviewError(error: ErrorMessage) {
        if (isAttached()) {
            binding.layoutChangeIncomingSale.linearLayoutIncomingFastMain.gone()
            binding.layoutOverview.linearLayoutOverView.gone()
            binding.componentFeeAndPlanIncoming.gone()
            binding.layoutErrorFeeAndPlan.constraintLayoutOverviewError.visible()
            binding.layoutErrorFeeAndPlan.buttonLoadAgain.setOnClickListener {
                presenter.loadOverview(plan)
            }
        }
    }

    private fun getDays(day: Int): String {
        return if (day > 1) {
            "${day} dias"
        } else {
            "${day} dia"
        }
    }

    private fun getPercent(percent: Double): String {
        return "${percent.toPtBrRealStringWithoutSymbol()}%"
    }

    private fun gaSendButton() {
        if (isAttached()) {
            ga4.logClick(
                screenName = FEES_AND_PLANS,
                contentComponent = AUTOMATIC_RECEIVE,
                contentName = CANCEL_RA
            )
        }
    }
}