package br.com.mobicare.cielo.pix.ui.terms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentPixTermBinding
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics
import br.com.mobicare.cielo.pix.constants.IS_PARTNER_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_PARTNER_USAGE_TERMS_URL
import br.com.mobicare.cielo.pix.constants.PIX_USAGE_TERMS_URL
import org.jetbrains.anko.browse
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixTermFragment : BaseFragment(), CieloNavigationListener, PixTermContract.View {

    private var binding: FragmentPixTermBinding? = null
    private val presenter: PixTermPresenter by inject {
        parametersOf(this)
    }
    private val isPartner by lazy {
        arguments?.getBoolean(IS_PARTNER_ARGS, false) ?: false
    }

    private var navigation: CieloNavigation? = null
    private var termsUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
            FragmentPixTermBinding.inflate(
                    inflater, container, false
            ).also {
                binding = it
            }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupView()
        setupListeners()
        logScreenView()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_home_pix))
            navigation?.showContainerButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        if (isPartner) {
            termsUrl = PIX_PARTNER_USAGE_TERMS_URL
            setupViewPartner()
        } else
            termsUrl = PIX_USAGE_TERMS_URL
    }

    private fun logScreenView() {
        PixAnalytics.logScreenView(PixAnalytics.ScreenView.ADHERENCE_REQUEST)
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(error = error)
    }

    override fun successTermPix() {
        navigation?.successTermPix()
    }

    override fun onRetry() {
        requireActivity().finish()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupViewPartner() {
         binding?.containerPixAuthorizationAgreement?.tvTermsPixTerms?.text = getString(R.string.txt_pix_partner_title)
    }

    private fun setupListeners() {
        binding?.apply {
            containerPixAuthorizationAgreement.include.checkBoxPix.setOnCheckedChangeListener { _, selected ->
                btnSendTermPix.isEnabled = selected
            }

            containerPixAuthorizationAgreement.containerReadPixTerms.setOnClickListener {
                termsUrl?.let { url ->
                    requireActivity().browse(url)
                }
            }

            btnSendTermPix.setOnClickListener {
                presenter.sentTermPix(isPartner)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}