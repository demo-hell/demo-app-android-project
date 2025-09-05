package br.com.mobicare.cielo.coil.presentation.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_COIL
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.Text.AUTOATENDIMENTO
import br.com.mobicare.cielo.commons.constants.Text.BOBINAS
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_500
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.analytics.pipeJoin
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.databinding.FragmentCoilChooseBinding
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CoilChooseFragment : BaseFragment(), CoilChooseContract.View {

    private var binding: FragmentCoilChooseBinding? = null

    private lateinit var actionListener: ActivityStepCoordinatorListener
    private var chosen: (CoilOptionObj) -> Unit = {}

    private val mPresenter: CoilChoosePresenter by inject {
        parametersOf(this)
    }

    private val ga4: SelfServiceAnalytics by inject()

    companion object {
        fun create(
            listener: ActivityStepCoordinatorListener,
            chosen: (CoilOptionObj) -> Unit
        ): Fragment {
            val fragment = CoilChooseFragment().apply {
                this.actionListener = listener
                this.chosen = chosen
            }

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCoilChooseBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPresenter.setView(this)
        mPresenter.loadSupplies()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.setView(this)
        logScreenView()
    }

    override fun onDestroy() {
        mPresenter.onClieared()
        super.onDestroy()
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (isAttached()) {
                if (it.httpStatus >= HTTP_STATUS_500) {
                    binding?.apply {
                        mainView.gone()
                        errorLayout.visible()
                        errorLayout.cieloErrorMessage =
                            getString(R.string.text_message_generic_error)
                        errorLayout.errorButton?.setText(getString(R.string.text_try_again_label))
                        errorLayout.cieloErrorTitle = getString(R.string.text_title_generic_error)
                        errorLayout.errorHandlerCieloViewImageDrawable =
                            R.drawable.ic_generic_error_image
                        errorLayout.configureActionClickListener {
                            showLoading()
                            mPresenter.loadSupplies()
                        }
                    }
                } else {
                    activity?.showMessage(
                        it.message,
                        it.title
                    ) {
                        setBtnRight(getString(R.string.ok))
                    }
                }
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            actionListener.onLogout()
        }
    }

    override fun showSupplies(supplies: ArrayList<CoilOptionObj>) {
        if (isAttached()) {
            val mAdapter = CoilChooseAdapter(supplies,
                chosen = {
                    logClickCardButton(it.title)

                    this.chosen(it)
                    actionListener.onNextStep(false)
                }
            )
            binding?.recyclerView?.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = mAdapter
            }
        }
    }


    override fun showLoading() {
        if (isAttached()) {
            super.showLoading()
            binding?.apply {
                mainView.visible()
                errorLayout.gone()
                frameProgressView.visible()
            }
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            super.hideLoading()
            binding?.frameProgressView.gone()
        }
    }

    override fun showIneligible(message: String) {
        binding?.apply {
            mainView.gone()
            frameProgressView.gone()
            errorLayout.visible()
            errorLayout.cieloErrorMessage = message
            errorLayout.errorButton?.setText(getString(R.string.ok))
            errorLayout.cieloErrorTitle = getString(R.string.text_title_service_unavailable)
            errorLayout.errorHandlerCieloViewImageDrawable = R.drawable.img_ineligible_user
            errorLayout.configureActionClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun logScreenView() {
        Analytics.trackScreenView(
            screenName = pipeJoin(Category.APP_CIELO, AUTOATENDIMENTO, BOBINAS),
            screenClass = this.javaClass
        )
        ga4.logScreenView(SCREEN_VIEW_REQUEST_MATERIALS_COIL)
    }

    private fun logClickCardButton(coilName: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(BOBINA),
            label = listOf(Label.CARD, coilName)
        )
        ga4.logBeginCheckoutCoil(coilName)
    }
}