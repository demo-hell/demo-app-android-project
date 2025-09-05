package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.doSeuJeito.taxas

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
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.component.feeandplans.model.ComponentLayoutFeeAndPlansItem
import br.com.mobicare.cielo.databinding.FragmentTaxasPorBandeiraBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUtils
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import br.com.mobicare.cielo.newRecebaRapido.presentation.ReceiveAutomaticActivity
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.recebaRapido.cancellation.CancellationRecebaRapidoActivity
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_PLAN
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeiraModelView
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeirasFragment
import br.com.mobicare.cielo.taxaPlanos.doSeuJeito.DoSeuJeitoTaxasPlanosPresenter
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.dialog.RACancelWhatsappDialog
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.myPlan.TaxaPlanosPlanFragmentNew
import kotlinx.android.synthetic.main.item_taxa_planos_machine.view.componentItem
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

private const val FREE = 0.0
private const val MODEL_PLANO = "PLANO"
const val ARG_MACHINE_FEE_PLAN = "ARG_MACHINE_FEE_PLAN"

class DoSeuJeitoTaxasPlanosFragment : BaseFragment(), DoSeuJeitoTaxasPlanosContract.View {

    val machine: TaxaPlanosSolutionResponse? by lazy {
        arguments?.getParcelable(ARG_MACHINE_FEE_PLAN)
    }
    val planName: String? by lazy {
        arguments?.getString(TAXA_PLANOS_PLAN)
    }
    val presenter: DoSeuJeitoTaxasPlanosPresenter by inject {
        parametersOf(this)
    }

    private lateinit var binding: FragmentTaxasPorBandeiraBinding

    private val ga4: RAGA4 by inject()

    companion object {
        fun create(planName: String, machine: TaxaPlanosSolutionResponse?) =
            DoSeuJeitoTaxasPlanosFragment().apply {
                arguments = Bundle().apply {
                    putString(TAXA_PLANOS_PLAN, planName)
                    putParcelable(ARG_MACHINE_FEE_PLAN, machine)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTaxasPorBandeiraBinding.inflate(inflater, container, false)
        .also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureListeners()
        setupMachine()

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
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
        ga4.logScreenView(
            screenName = RAGA4.SCREEN_VIEW_FEES_PLANS
        )
    }

    private fun configureListeners() {
        binding.layoutCardErrorMachines.buttonLoadAgain.setOnClickListener {
            presenter.loadMachines()
        }

        binding.layoutCardErrorIncomingFast.buttonLoadAgain.setOnClickListener {
            presenter.loadEligibleIncomingFastOffer()
        }

        binding.layoutCardErrorTaxes.buttonLoadAgain.setOnClickListener {
            presenter.loadTaxes()
        }

        binding.textViewChangeIncoming.setOnClickListener {
            requireContext().startActivity<ReceiveAutomaticActivity>()
        }
    }

    private fun setupMachine() {
        showMachinesLoading(true)
        machine?.let {
            showMachine(it)

        } ?: run {
            presenter.loadMachines()
        }
    }

    override fun showMachinesLoading(isShow: Boolean) {
        if (isShow) {
            binding.layoutCardErrorMachines.root.gone()
            binding.recyclerViewMachines.gone()
            binding.shimmerMachines.visible()
            binding.shimmerMachines.startShimmer()
        } else {
            binding.shimmerMachines.stopShimmer()
            binding.shimmerMachines.gone()
        }
    }

    override fun showMachine(response: TaxaPlanosSolutionResponse) {
        showMachinesLoading(false)

        if (isAttached()) {
            binding.recyclerViewMachines.visible()

            binding.recyclerViewMachines.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = DefaultViewListAdapter(
                response.pos,
                R.layout.item_taxa_planos_machine
            )
            adapter.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<TaxaPlanosMachine> {
                override fun onBind(item: TaxaPlanosMachine, holder: DefaultViewHolderKotlin) {
                    val list = ArrayList<ComponentLayoutFeeAndPlansItem>()
                    holder.mView.componentItem.requestLayout()

                    val stringValue = when (item.rentalAmount) {
                        FREE -> getString(R.string.text_view_fee_plan_rent_value)
                        else -> item.rentalAmount?.toPtBrRealString()
                    }
                    list.add(
                        ComponentLayoutFeeAndPlansItem(
                            getString(R.string.text_view_value_label),
                            stringValue
                        )
                    )

                    if (item.model != MODEL_PLANO)
                        list.add(
                            ComponentLayoutFeeAndPlansItem(
                                getString(R.string.text_view_item_logical_number),
                                item.logicalNumber ?: EMPTY
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
                    when (e.action) {
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

    override fun showMachineError(error: ErrorMessage) {
        if (isAttached()) {
            binding.recyclerViewMachines.gone()
            binding.layoutCardErrorMachines.root.visible()
        }
    }

    override fun hideMachinesCard() {
        binding.layoutCardErrorMachines.root.gone()
        binding.recyclerViewMachines.gone()
        binding.shimmerMachines.gone()
        binding.tvLabelMachines.gone()
    }

    override fun showChangeIncomingButton(isShow: Boolean) {
        if (isShow) {
            binding.layoutIncomingFast.visible()
            binding.textViewChangeIncoming.visible()
            binding.linearLayoutIncomingFast.gone()
            binding.textViewRRCancellation.gone()
        } else {
            binding.layoutIncomingFast.gone()
        }
    }

    override fun showIncomingFastLoading(isShow: Boolean) {
        if (isShow) {
            binding.layoutIncomingFast.gone()
            binding.shimmerIncoming.visible()
            binding.shimmerIncoming.startShimmer()
        } else {
            binding.shimmerIncoming.stopShimmer()
            binding.shimmerIncoming.gone()
        }
    }

    override fun showIncomingFastError(error: ErrorMessage) {
        binding.shimmerIncoming.gone()
        binding.layoutIncomingFast.gone()
        binding.layoutCardErrorIncomingFast.root.visible()
    }

    override fun showTaxesLoading(isShow: Boolean) {
        if (isShow) {
            binding.layoutCardErrorTaxes.root.gone()
            binding.containerView.gone()
            binding.shimmerTaxes.startShimmer()
            binding.shimmerTaxes.visible()
        } else {
            binding.shimmerTaxes.stopShimmer()
            binding.shimmerTaxes.gone()
        }
    }

    override fun showTaxes(taxes: ArrayList<BandeiraModelView>) {
        binding.containerView.visible()
        if (isAttached()) {
            childFragmentManager
                .beginTransaction()
                .add(R.id.containerView, BandeirasFragment.newInstance(taxes))
                .commit()
        }
    }

    override fun showTaxesError(error: ErrorMessage) {
        binding.containerView.gone()
        binding.shimmerTaxes.gone()
        binding.layoutCardErrorTaxes.root.visible()
    }

    override fun showIncomingWay(isEnabledCancelIncomingFastFT: Boolean) {
        binding.layoutIncomingFast.visible()
        binding.textViewChangeIncoming.gone()
        binding.linearLayoutIncomingFast.visible()

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
                    setPrimaryButton(this@DoSeuJeitoTaxasPlanosFragment.getString(R.string.incomint_fast_cancellation_keep_rr))
                    setSecondaryButton(this@DoSeuJeitoTaxasPlanosFragment.getString(R.string.incomint_fast_cancellation_yes_cancel))
                    setOnSecondaryButtonClickListener {
                        ga4.logClick(
                            screenName = RAGA4.SCREEN_VIEW_RA_CANCEL,
                            contentComponent = RAGA4.CANCEL_RA,
                            contentName = RAGA4.YES_I_WANT_TO_CANCEL
                        )
                        dismiss()
                        presenter.confirmCancellation()
                    }
                }
                if (dialog.isResumed.not()) {
                    dialog.show(
                        childFragmentManager,
                        DoSeuJeitoTaxasPlanosFragment::class.java.simpleName
                    ).also {
                        ga4.logScreenView(RAGA4.SCREEN_VIEW_RA_CANCEL)
                        ga4.logDisplayContent(
                            screenName = RAGA4.SCREEN_VIEW_RA_CANCEL,
                            contentComponent = RAGA4.CONFIRM_CANCEL_RA,
                            contentType = GoogleAnalytics4Values.MODAL
                        )
                    }
                }
            }
        }
    }

    override fun showWhatsAppCancellationDialog(whatsappLink: String?) {
        val wadialog = RACancelWhatsappDialog.newInstance(whatsappLink)
        wadialog.show(
            this@DoSeuJeitoTaxasPlanosFragment.childFragmentManager,
            TaxaPlanosPlanFragmentNew::class.java.simpleName
        )
    }

    override fun showCancellationActivity() {
        val intent = Intent(
            this@DoSeuJeitoTaxasPlanosFragment.context,
            CancellationRecebaRapidoActivity::class.java
        )
        intent.putExtra(TAXA_PLANOS_PLAN, planName)
        startActivity(intent)
    }

    private fun gaSendButton() {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.CANCELAR_RR),
                action = listOf(Action.CLICK, Label.LINK),
                label = listOf(Category.CANCELAR_RR, planName ?: "")
            )

            ga4.logClick(
                screenName = RAGA4.SCREEN_VIEW_FEES_PLANS,
                contentComponent = RAGA4.AUTOMATIC_RECEIVE,
                contentName = RAGA4.CANCEL_RA
            )
        }
    }
}