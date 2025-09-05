package br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel

import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.Postecipate.ARG_PARAM_EQUIPMENT_DETAILS_TERMINAL
import br.com.mobicare.cielo.commons.constants.Postecipate.FULL_PROGRESS_BAR
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.LocaleUtil
import br.com.mobicare.cielo.commons.utils.convertTimeStampToDate
import br.com.mobicare.cielo.commons.utils.isoDateToBrHourAndMinute
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.taxaPlanos.analytics.FeeAndPlansGA4.ScreenView.SCREEN_VIEW_FEE_AND_PLANS_MY_RENT
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.doSeuJeito.taxas.ARG_MACHINE_FEE_PLAN
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.billingHistoryTitle
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.calendarIcon
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.container_rental
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.errorView
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.goalStatus
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.ivEquipmentIcon
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.lastUpdate
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.loading
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.rvEquipments
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.rvHistory
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.tvEquipmentTraded
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.tvSeeAllEquipments
import kotlinx.android.synthetic.main.fragment_postecipado_seu_aluguel.valueProgressView
import kotlinx.android.synthetic.main.layout_billing_details_postecipado.currentDate
import kotlinx.android.synthetic.main.layout_billing_details_postecipado.tvEndOfTermDate
import kotlinx.android.synthetic.main.layout_billing_details_postecipado.tvMonth
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.taxaPlanos.analytics.FeeAndPlansGA4 as ga4

class PostecipadoMeuAluguelFragment : BaseFragment(), PostecipadoMeuAluguelContract.View {

    val machine: TaxaPlanosSolutionResponse? by lazy {
        arguments?.getParcelable(ARG_MACHINE_FEE_PLAN)
    }
    private val mPresenter: PostecipadoMeuAluguelPresenter by inject { parametersOf(this) }

    companion object {
        fun create(machine: TaxaPlanosSolutionResponse?) = PostecipadoMeuAluguelFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_MACHINE_FEE_PLAN, machine)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_postecipado_seu_aluguel, container, false)
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
        mPresenter.onResume()
        mPresenter.loadRentInformation(machine)
    }

    override fun onPause() {
        super.onPause()
        valueProgressView?.clearProgress()
    }

    override fun showRentInformation(response: PlanInformationResponse, rentAmountValue: Double?) {
        response.firstOrNull()?.let { currentRentInfo ->
            val terminals = currentRentInfo.terminals

            if (terminals.isNullOrEmpty()) goneEquipmentInfo()
            else showEquipmentInfo(terminals)

            if (response.toArray().size > ONE) showBillingHistory(response)
            else goneBillingHistory()

            currentDate?.text = currentRentInfo.currentDate?.convertTimeStampToDate()
            goalStatus?.text = defineGoalStatusToShow(currentRentInfo.percentageReached)
            tvMonth?.text = LocaleUtil.getMonthLongName(currentRentInfo.referenceMonth)
            tvEndOfTermDate?.text = currentRentInfo.expirationDate?.convertTimeStampToDate()

            lastUpdate?.text = setLastUpdateStyle(
                currentRentInfo.dateUpdate?.convertTimeStampToDate(),
                currentRentInfo.dateUpdate?.isoDateToBrHourAndMinute()
            )

            currentRentInfo.billingPerformed?.let {  itBillingPerformed ->
                currentRentInfo.valueContract?.let { itValueContract ->
                    valueProgressView.setup(itBillingPerformed, itValueContract, title = getString(R.string.text_current_earnings_plan_label)).showProgress()
                }
            }
        } ?: showError()
    }

    private fun logScreenView(){
        if (isAttached()){
            ga4.logScreenView(SCREEN_VIEW_FEE_AND_PLANS_MY_RENT)
        }
    }

    private fun showEquipmentInfo(terminals: List<Terminal>) {
        tvSeeAllEquipments?.visible(terminals.size > TWO)

        tvSeeAllEquipments?.setOnClickListener {
            goToEquipmentDetails(terminals)
        }

        rvEquipments?.apply {
            visible()

            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = PostecipadoEquipmentsAdapter(terminals, requireContext(), limitQuantityItems = TWO)
        }
    }

    private fun goToEquipmentDetails(terminals: List<Terminal>) =
        requireActivity().startActivity<EquipmentDetailsActivity>(ARG_PARAM_EQUIPMENT_DETAILS_TERMINAL to terminals)

    private fun goneEquipmentInfo() {
        tvEquipmentTraded?.gone()
        ivEquipmentIcon?.gone()
        rvEquipments?.gone()
    }

    private fun goneBillingHistory() {
        calendarIcon?.gone()
        billingHistoryTitle?.gone()
        rvHistory?.gone()
    }

    private fun showBillingHistory(response: PlanInformationResponse) {
        rvHistory?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = PostecipadoBillingHistoryAdapter(response.toArray().drop(ONE) as List<PostecipadoRentInformationResponse>, requireContext())
        }
    }

    private fun setLastUpdateStyle(
        date: String?,
        hour: String?
    ): Spanned {
        return HtmlCompat.fromHtml(
            getString(R.string.last_update, date, hour),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun defineGoalStatusToShow(percentageReached: String?): String {
         val isUserReachedTheGoal = percentageReached?.toDouble()?.toInt() ?: ZERO >= FULL_PROGRESS_BAR

        return if (isUserReachedTheGoal) getString(R.string.text_reached_the_goal)
        else getString(R.string.text_did_not_reach_the_goal)
    }

    override fun unavailableService(message: String?) {
        errorView?.errorButton?.invisible()

        val errorMessage = if (message.isNullOrEmpty())
            getString(R.string.unavailable_service_plan_information_subtitle) else message

        setupError(
            R.string.unavailable_service_plan_information_title,
            errorMessage,
            R.drawable.ic_29
        )
    }

    override fun notEligibleForPostecipate() {
        errorView?.errorButton?.invisible()

        setupError(
            R.string.bottom_sheet_plan_information_title,
            getString(R.string.bottom_sheet_plan_information_subtitle),
            R.drawable.ic_04
        )
    }

    override fun showError(error: ErrorMessage?) {
        errorView?.errorButton?.visible()

        setupError(
            R.string.text_title_generic_error,
            getString(R.string.text_message_generic_error),
            R.drawable.ic_generic_error_image
        )
        errorView?.errorButton?.setOnClickListener {
            mPresenter.loadRentInformation(machine)
        }
    }

    private fun setupError(
        @StringRes title: Int,
        message: String,
        @DrawableRes image: Int
    ) {
        container_rental?.gone()
        errorView?.visible()

        errorView?.cieloErrorTitle = getString(title)
        errorView?.cieloErrorMessage = message
        errorView?.errorHandlerCieloViewImageDrawable = image
    }

    override fun hideLoading() {
        loading?.gone()
        errorView?.gone()
        container_rental?.visible()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }
}