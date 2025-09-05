package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.AnimateProgressBarHelper
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.addArgument
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.component.CieloCardBrandsView
import br.com.mobicare.cielo.component.feeandplans.model.ComponentLayoutFeeAndPlansItem
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.CieloAllBrandsBottomSheet
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosDetailsResponse
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.fragment_taxas_planos_cielo.*
import kotlinx.android.synthetic.main.layout_error_link_list_order.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class TaxasPlanosCieloFragment : BaseFragment(), TaxasPlanosCieloContract.View {

    companion object {

        const val PLAN_TYPE_ARG = "br.com.cielo.taxasPlanos.planType"

        fun create(planName: String): TaxasPlanosCieloFragment {
            return TaxasPlanosCieloFragment().apply {
                this.addArgument(PLAN_TYPE_ARG, planName)
            }
        }
    }

    val planName: String? by lazy {
        arguments?.getString(PLAN_TYPE_ARG)
    }

    val presenter: TaxasPlanosCieloPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_taxas_planos_cielo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reloadPlan()
    }

    private fun reloadPlan() {
        planName?.let {
            this.presenter.load(it)
        } ?: run {
            showError(ErrorMessage())
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.presenter.onDestroy()
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            nestedCieloTaxAndPlansFree.visibility = View.GONE
            constraintLayoutError.visible()
            buttonLoadAgain.setOnClickListener {
                reloadPlan()
            }
            linearShimmerLoading.visibility = View.GONE
        }
    }

    private fun configureViews(@ColorRes colorRes: Int) {
        if (isAttached()) {
            changeLabelAndProgressColorTo(colorRes)
            this.pbView.progress = 0.toFloat()
            this.pbView.max = 100.toFloat()
            this.valueTextView.text = 0.toDouble().toPtBrRealString()
        }
    }

    private fun configureListeners() {
        this.buttonUpdate?.setOnClickListener {
            reloadPlan()
        }
    }


    override fun showLoading() {
        if (isAttached()) {
            constraintLayoutError.gone()
            cardFreeCieloPlan.visibility = View.GONE
            linearShimmerLoading.visibility = View.VISIBLE
            shimmerLoading.startShimmer()
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            shimmerLoading.stopShimmer()
            linearShimmerLoading.visibility = View.GONE
            constraintLayoutError.gone()
            nestedCieloTaxAndPlansFree.visibility = View.VISIBLE
            cardFreeCieloPlan.visibility = View.VISIBLE
        }
    }

    override fun onError(error: ErrorMessage) {
        showError(error)
    }

    override fun showFreeCieloData(taxaPlanosDetail: TaxaPlanosDetailsResponse) {
        configureViews(R.color.color_f98f25)
        configureListeners()

        setupEarningsDetail(taxaPlanosDetail)

        setupProgressBarAnimationFree(taxaPlanosDetail)
        hideCieloControlCardBrands()
    }

    private fun hideCieloControlCardBrands() {
        if (isAttached()) {
            cardShowControlAllBrands.visibility = View.GONE
        }
    }

    private fun setupProgressBarAnimationFree(taxaPlanosDetail: TaxaPlanosDetailsResponse) {
        if (isAttached()) {
            AnimateProgressBarHelper.setProgressValue(this.pbView,
                    taxaPlanosDetail.totalRevenue,
                    taxaPlanosDetail.minimumRequiredRevenue.toInt(),
                    {
                        this.valueTextView?.text = it.toDouble().toPtBrRealString()
                    },
                    {
                        this.valueTextView?.text = taxaPlanosDetail.totalRevenue.toPtBrRealString()
                        if (taxaPlanosDetail.totalRevenue >= taxaPlanosDetail.minimumRequiredRevenue) {
                            changeLabelAndProgressColorTo(R.color.color_009e55)
                        } else {
                            changeLabelAndProgressColorTo(R.color.color_f98f25)
                        }
                    })
        }
    }

    private fun setupEarningsDetail(taxaPlanosDetail: TaxaPlanosDetailsResponse) {
        this.componentEarningsDetail.setContent(
                getString(R.string.text_earnings_detail_plan_label),
                ArrayList<ComponentLayoutFeeAndPlansItem>().apply {
                    this.add(
                            ComponentLayoutFeeAndPlansItem(
                                    getString(R.string.text_earnings_detail_plan_minimum),
                                    taxaPlanosDetail.minimumRequiredRevenue.toPtBrRealString()
                            )
                    )
                    this.add(
                            ComponentLayoutFeeAndPlansItem(
                                    getString(R.string.text_earnings_detail_plan_difference_to_absent),
                                    taxaPlanosDetail.remainingRevenueToExemption.toPtBrRealString()
                            )
                    )
                }
        )
    }

    private fun setupEarningsControlDetail(taxaPlanosDetail: TaxaPlanosDetailsResponse) {
        this.componentEarningsDetail.setContent(
                getString(R.string.text_earnings_detail_plan_label),
                ArrayList<ComponentLayoutFeeAndPlansItem>().apply {
                    this.add(
                            ComponentLayoutFeeAndPlansItem(
                                    getString(R.string.text_cielo_control_plan_label),
                                    taxaPlanosDetail.maximumAllowedRevenue.toPtBrRealString()
                            )
                    )
                    this.add(
                            ComponentLayoutFeeAndPlansItem(
                                    getString(R.string.text_cielo_control_excedent_label),
                                    taxaPlanosDetail.exceedingRevenue.toPtBrRealString()
                            )
                    )
                    this.add(
                            ComponentLayoutFeeAndPlansItem(
                                    getString(R.string.text_cielo_control_tax_excedent_label),
                                    taxaPlanosDetail.exceedingRevenueFee.toPtBrRealString()
                            )
                    )
                }
        )
    }


    override fun showControlCieloData(taxaPlanosDetail: TaxaPlanosDetailsResponse) {
        this.textPlanControlInfo.visibility = View.VISIBLE
        configureViews(R.color.color_009e55)
        configureListeners()

        setupEarningsControlDetail(taxaPlanosDetail)

        setupProgressBarAnimationControl(taxaPlanosDetail)
        showControlCardBrands()
    }

    private fun showControlCardBrands() {
        if (isAttached()) {
            cardShowControlAllBrands?.visibility = View.VISIBLE
            cardShowControlAllBrands?.setListener(object :
                    CieloCardBrandsView.OnButtonClickListener {
                override fun onButtonClicked() {

                    CieloAllBrandsBottomSheet.create(
                            ArrayList(ConfigurationPreference.instance
                                    .allSupportedBrandsImageUrls(requireContext()))
                    ).show(childFragmentManager,
                            TaxasPlanosCieloFragment::class.java.simpleName)

                }
            })
        }
    }

    private fun setupProgressBarAnimationControl(taxaPlanosDetail: TaxaPlanosDetailsResponse) {
        if (isAttached()) {
            AnimateProgressBarHelper.setProgressValue(this.pbView,
                    taxaPlanosDetail.totalRevenue,
                    taxaPlanosDetail.maximumAllowedRevenue.toInt(),
                    {
                        this.valueTextView?.text = it.toDouble().toPtBrRealString()
                    },
                    {
                        this.valueTextView?.text = taxaPlanosDetail.totalRevenue.toPtBrRealString()
                        if (taxaPlanosDetail.totalRevenue >= taxaPlanosDetail.maximumAllowedRevenue) {
                            changeLabelAndProgressColorTo(R.color.color_f98f25)
                        } else {
                            changeLabelAndProgressColorTo(R.color.color_009e55)
                        }
                    })
        }
    }

    private fun changeLabelAndProgressColorTo(
            @ColorRes colorRes: Int
    ): Unit? {
        if (isAttached()){
            this.pbView.progressColor =
                    ContextCompat.getColor(requireContext(), colorRes)
        }
        return this.valueTextView?.setTextColor(
                ContextCompat.getColor(
                        requireContext(),
                        colorRes))
    }
}