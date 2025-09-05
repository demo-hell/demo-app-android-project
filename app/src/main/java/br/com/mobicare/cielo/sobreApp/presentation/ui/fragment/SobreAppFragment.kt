package br.com.mobicare.cielo.sobreApp.presentation.ui.fragment

import androidx.annotation.Keep
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.SobreAppFragmentBinding
import br.com.mobicare.cielo.sobreApp.analytics.SobreAppAnalytics.ScreenView.SCREEN_VIEW_OTHERS_ABOUT_APP
import br.com.mobicare.cielo.sobreApp.presentation.presenter.SobreAppPresenter
import br.com.mobicare.cielo.sobreApp.presentation.ui.SobreAppContract
import br.com.mobicare.cielo.sobreApp.analytics.SobreAppAnalytics as ga4

@Keep
class SobreAppFragment : BaseFragment(), SobreAppContract.View {
    private lateinit var presenter: SobreAppContract.Presenter
    private var binding: SobreAppFragmentBinding? = null

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: android.os.Bundle?,
    ) = SobreAppFragmentBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onStart() {
        super.onStart()

        presenter = SobreAppPresenter(this)
        presenter.retrieveAppVersion()

        this.configureToolbarActionListener?.changeTo(title = getString(R.string.menu_sobre))
    }

    override fun fillAppVersion(
        versionName: String,
        versionCode: Int,
    ) {
        binding?.sobreAppVersion?.text = getString(R.string.sobre_app_versao, versionName, versionCode)
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    private fun logScreenView() {
        if (isAttached()) {
            ga4.logScreenView(SCREEN_VIEW_OTHERS_ABOUT_APP)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
