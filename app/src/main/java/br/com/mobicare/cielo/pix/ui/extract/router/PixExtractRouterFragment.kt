package br.com.mobicare.cielo.pix.ui.extract.router

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentPixExtractRouterBinding
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixExtractRouterFragment : BaseFragment(), CieloNavigationListener,
    PixExtractRouterContract.View {

    private val presenter: PixExtractRouterPresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null
    private var _binding: FragmentPixExtractRouterBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixExtractRouterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        presenter.onShowExtract(isHome())
    }

    private fun isHome() = navigation?.getData() as? Boolean ?: true

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.screen_toolbar_text_extract))
            navigation?.setNavigationListener(this)
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onShowExtract() {
        findNavController().navigate(
            PixExtractRouterFragmentDirections.actionPixExtractRouterFragmentToPixExtractFragment()
        )
    }

    override fun onShowOnboarding() {
        findNavController().navigate(
            PixExtractRouterFragmentDirections.actionPixExtractRouterFragmentToPixOnboardingExtractFragment()
        )
    }
}