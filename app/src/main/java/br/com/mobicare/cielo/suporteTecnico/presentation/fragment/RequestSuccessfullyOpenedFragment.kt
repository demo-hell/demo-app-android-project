package br.com.mobicare.cielo.suporteTecnico.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.PATH_OPEN_TICKET_SUCESSO
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.Values.TRACK_REQUEST
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentRequestSuccessfullyOpenedBinding
import br.com.mobicare.cielo.eventTracking.utils.MenuTranslator
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class RequestSuccessfullyOpenedFragment :
    BaseFragment(),
    CieloNavigationListener {
    private var _binding: FragmentRequestSuccessfullyOpenedBinding? = null
    private val binding get() = _binding!!
    private val args: RequestSuccessfullyOpenedFragmentArgs by navArgs()
    private lateinit var protocol: OrderReplacementResponse
    private var navigation: CieloNavigation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        protocol = args.orderReplacementResponse
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentRequestSuccessfullyOpenedBinding
        .inflate(inflater, container, false)
        .also {
            _binding = it
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupView()
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(isCollapsed = false)
            navigation?.showToolbar(isShow = true)
            navigation?.showBackButton(isShow = false)
            navigation?.showCloseButton(isShow = true)
            navigation?.showHelpButton(isShow = false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        binding.apply {
            val hours = resources.getStringArray(R.array.hours)
            tvProtocol.text = getString(R.string.number_protocol, protocol.id).fromHtml()
            tvPeriodAnalysis.text =
                getString(
                    R.string.period_analysis,
                    protocol.hours.toString(),
                    hours[getIndexHours(protocol.hours.orZero)],
                ).fromHtml()
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnAccompanyProtocol.setOnClickListener {
                GA4.logClick(PATH_OPEN_TICKET_SUCESSO, TRACK_REQUEST)
                val requestSupport =
                    MenuTranslator(
                        code = Router.APP_ANDROID_MACHINE_TRACKING,
                        name = getString(R.string.my_requests_title_string),
                    )
                navigate(requestSupport)
            }
        }
    }

    private fun navigate(menu: MenuTranslator) {
        Router.navigateTo(
            requireContext(),
            menu.toMenu(),
            object : Router.OnRouterActionListener {
                override fun actionNotFound(action: Menu) {
                }
            },
        )
    }

    override fun onCloseButtonClicked() {
        navigation?.goToHome()
    }

    private fun getIndexHours(hours: Int): Int =
        if (hours.orZero > ONE) {
            ONE
        } else {
            ZERO
        }
}
