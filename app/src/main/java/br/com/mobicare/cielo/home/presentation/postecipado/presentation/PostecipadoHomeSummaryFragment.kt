package br.com.mobicare.cielo.home.presentation.postecipado.presentation

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.constants.ZERO_TEXT
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.commons.utils.parseToLocalDate
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.databinding.PostecipadoSummaryHomeFragmentBinding
import br.com.mobicare.cielo.extensions.goToFlowUsingRouter
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4.Companion.BILLING_POSTPONED_PLAN
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4.Companion.FOLLOW_PLAN
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoConstants.GO_DIRECTLY_TO_POSTECIPADO
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState.Empty
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState.Error
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState.HideLoading
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState.Success
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PostecipadoRentInformationResponse
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostecipadoHomeSummaryFragment : BaseFragment() {

    private var binding: PostecipadoSummaryHomeFragmentBinding? = null
    private val viewModel: PostecipadoHomeSummaryViewModel by viewModel()
    private val ga4: HomeGA4 by inject()

    companion object {
        fun newInstance(): PostecipadoHomeSummaryFragment {
            return PostecipadoHomeSummaryFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PostecipadoSummaryHomeFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
        getMyPlanInformation()
    }

    private fun setupListeners() {
        binding?.btGoToMyPlan?.setOnClickListener {
            requireActivity().goToFlowUsingRouter(
                flowName = Router.APP_ANDROID_RATES,
                toolbarTitle = getString(R.string.toolbar_plan_tax),
                bundleParams = Bundle().apply {
                    putBoolean(GO_DIRECTLY_TO_POSTECIPADO, true)
                }
            )
            ga4.logServiceHomeClick(
                contentComponent = BILLING_POSTPONED_PLAN,
                contentName = FOLLOW_PLAN
            )
        }
    }

    private fun setupObservers() {
        viewModel.postecipadoUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HideLoading -> hideLoadingShimmer()
                is Success -> showPlanInformation(state.data)
                is Empty, Error() -> destroyFragment()
                else -> destroyFragment()
            }
        }
    }

    private fun hideLoadingShimmer() {
        binding?.apply {
            loadingShimmerInclude.root.gone()
            title.visible()
            goalCurrentInformation.visible()
            valueProgressView.visible()
            progressViewInformation.visible()
            btGoToMyPlan.visible()
            dividerView.visible()
            dividerViewBottom.visible()
        }
    }

    private fun showPlanInformation(planInfo: PostecipadoRentInformationResponse?) {
        val isUserReachedTheGoal = planInfo?.missingValue?.toInt() == ZERO

        binding?.apply {
            val responseReferenceMonth = planInfo?.referenceMonth?.parseToLocalDate()
            val finalPeriod =
                responseReferenceMonth?.withDayOfMonth(responseReferenceMonth.lengthOfMonth())

            progressViewInformation.text = getString(
                R.string.period_to_reach_the_goal,
                responseReferenceMonth?.convertToBrDateFormat(),
                finalPeriod?.convertToBrDateFormat()
            )

            planInfo?.billingPerformed?.let { itBillingPerformed ->
                planInfo.valueContract?.let { itValueContract ->
                    valueProgressView.setup(itBillingPerformed, itValueContract).showProgress()
                }
            }
        }

        if (isUserReachedTheGoal) {
            setupGoalReached(planInfo)
        } else {
            setupGoalNotReachedYet(planInfo)
        }
    }

    private fun setupGoalReached(planInfo: PostecipadoRentInformationResponse?) {
        binding?.goalCurrentInformation?.apply {
            val terminal = planInfo?.terminals?.firstOrNull()
            val isExempted = planInfo?.isExempted == true
            val isSingleTerminal = planInfo?.terminals?.size == ONE
            val hasMoreThanOneTerminal = (planInfo?.terminals?.size ?: ZERO) > ONE
            val hasPercentageDiscount = terminal?.percentageDiscountNegotiated != null && terminal?.percentageDiscountNegotiated != ZERO_TEXT
            val hasValueDiscount = terminal?.valueDiscountNegotiated != null && terminal?.valueDiscountNegotiated > ZERO_DOUBLE

            val messageToShow = when {
                !isExempted && isSingleTerminal && hasValueDiscount -> getString(
                    R.string.goal_100_percent_achieved_completed_message,
                    terminal?.valueDiscountNegotiated?.toPtBrRealStringWithoutSymbol()
                )

                !isExempted && isSingleTerminal && hasPercentageDiscount -> getString(
                    R.string.goal_50_percent_achieved_message,
                    terminal?.percentageDiscountNegotiated
                )

                !isExempted && hasMoreThanOneTerminal -> getString(R.string.goal_100_percent_achieved_discount_message)
                isExempted -> getString(R.string.goal_100_percent_achieved_partial_message)
                else -> EMPTY
            }

            text = Html.fromHtml(messageToShow)

            setCustomDrawable {
                solidColor = R.color.green_100
                radius = R.dimen.dimen_8dp
            }
        }
    }

    private fun setupGoalNotReachedYet(planInfo: PostecipadoRentInformationResponse?) {
        binding?.goalCurrentInformation?.apply {
            val date = planInfo?.dateUpdate?.convertToBrDateFormat()
            val days = planInfo?.daysToEndTheMonth

            val message = resources.getQuantityString(
                R.plurals.current_goal_status_message,
                days ?: ZERO,
                date,
                days
            )

            text = message

            setCustomDrawable {
                solidColor = R.color.accent_100
                radius = R.dimen.dimen_8dp
            }
        }
    }

    private fun destroyFragment() {
        fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
    }

    private fun getMyPlanInformation() {
        viewModel.getPlanInformation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}