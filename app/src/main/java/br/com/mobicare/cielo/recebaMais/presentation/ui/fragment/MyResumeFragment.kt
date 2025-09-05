package br.com.mobicare.cielo.recebaMais.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.checkIfFragmentAttached
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.recebaMais.GA_RM_RECEBA_MAIS
import br.com.mobicare.cielo.recebaMais.GA_RM_RESUME_CATEGORY
import br.com.mobicare.cielo.recebaMais.GA_RM_RESUME_SCREEN
import br.com.mobicare.cielo.recebaMais.RM_HELP_ID
import br.com.mobicare.cielo.recebaMais.domains.entities.ContractDetails
import br.com.mobicare.cielo.recebaMais.domains.entities.ContractDetailsResponse
import br.com.mobicare.cielo.recebaMais.presentation.presenter.MyResumeContract
import br.com.mobicare.cielo.recebaMais.presentation.presenter.MyResumePresenter
import br.com.mobicare.cielo.recebaMais.presentation.ui.dialog.InformationContractBottomSheetFragment
import kotlinx.android.synthetic.main.fragment_receba_mais_resumo.*
import kotlinx.android.synthetic.main.include_contract_receive_more.*
import kotlinx.android.synthetic.main.include_extract_receive_more.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


const val TAG = "MyResumeFragment"
const val STATUS_OPENED = 3

class MyResumeFragment : BaseFragment(), MyResumeContract.View, CieloNavigationListener {

    private val presenter: MyResumePresenter by inject {
        parametersOf(this)
    }
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_receba_mais_resumo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().hideSoftKeyboard()

        configureNavigation()
        loadDetails()
        sendGaScreenView()
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.title_toolbar_receive_more))
            navigation?.showHelpButton(true)
            navigation?.showFilterButton(false)
            navigation?.showButton(false)
            navigation?.setNavigationListener(this)
        }
    }

    private fun loadDetails() {
        presenter.loadDetails()
    }

    override fun onHelpButtonClicked() {
        HelpMainActivity.create(
            requireActivity(),
            getString(R.string.text_rm_help_title),
            RM_HELP_ID
        )
    }

    override fun showContract(contract: ContractDetails) {
        container_contract?.visible()

        text_parcel_amount?.text = contract.installmentAmount.toPtBrRealString()
        text_contract_date?.text = contract.contractDate.dateFormatToBr()

        startDetailsContract(contract)
        startViewExtract(contract)
        checkIfFragmentAttached {
            setupInstallment(contract)
        }
    }

    override fun showError(error: ErrorMessage?) {
        doWhenResumed {
                navigation?.showError(
                    getString(R.string.message_error_title_receive_more),
                    getString(R.string.message_error_subtitle_receive_more),
                    getString(R.string.text_try_again_label),
                    R.drawable.ic_07,
                    View.OnClickListener {
                        loadDetails()
                    }
                )
        }
        error?.let {
            sendGaError(it)
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
        container_contract?.gone()
    }

    override fun hideLoading() {
        navigation?.showContent(true)
        container_contract?.gone()
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().finish()
        return super.onBackButtonClicked()
    }

    private fun setupInstallment(contract: ContractDetails) {
        contract.installments.forEachIndexed { index, installmentDetails ->
            when (index) {
                0 -> setupImageStatus(
                    installmentDetails.statusCode,
                    image_view_status_installment,
                    text_title_installment,
                    installmentDetails.installmentNumber,
                    installmentDetails.dueDate
                )
                1 -> setupImageStatus(
                    installmentDetails.statusCode,
                    image_view_status_installment_second,
                    text_title_installment_second,
                    installmentDetails.installmentNumber,
                    installmentDetails.dueDate
                )
                2 -> setupImageStatus(
                    installmentDetails.statusCode,
                    image_view_status_installment_third,
                    text_title_installment_third,
                    installmentDetails.installmentNumber,
                    installmentDetails.dueDate
                )
            }
        }

    }

    private fun setupImageStatus(
        status: Int,
        imageView: ImageView?,
        textView: TextView?,
        numberInstallment: Int,
        date: String
    ) {
        var statusInstallment = ""
        if (status == STATUS_OPENED) {
            imageView?.setBackgroundResource(R.drawable.ic_next_installment)
            statusInstallment = getString(R.string.status_receive_more)
        } else {
            imageView?.setBackgroundResource(R.drawable.ic_installment_paid)
        }

        val shortMonth = DataCustomNew()
            .setDateFromAPI(date)
            .toCalendar()
            .format(SHORT_MONTH_DESCRIPTION)
            .capitalize()

        val title =
            "${numberInstallment}${getString(R.string.status_receive_more_parcel)} $shortMonth $statusInstallment"
        textView?.text = title
    }

    private fun startDetailsContract(contract: ContractDetails) {
        image_view_info_receive_more?.setOnClickListener {
            InformationContractBottomSheetFragment.create(contract).show(childFragmentManager, TAG)
        }
    }

    private fun startViewExtract(contract: ContractDetails) {
        tv_ver_mais?.setOnClickListener {
            findNavController()
                .navigate(
                    MyResumeFragmentDirections
                        .actionMyResumeFragmentToContractInstallmentsFragment(
                            ContractDetailsResponse(listOf(contract))
                        )
                )
        }
    }

    private fun sendGaError(error: ErrorMessage) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RESUME_CATEGORY),
            action = listOf(GA_RM_RECEBA_MAIS),
            label = listOf(Label.MENSAGEM, error.title, error.message)
        )
    }

    private fun sendGaScreenView() {
        Analytics.trackScreenView(
            screenName = GA_RM_RESUME_SCREEN,
            screenClass = this.javaClass
        )
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}