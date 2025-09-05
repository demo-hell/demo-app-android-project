package br.com.mobicare.cielo.lighthouse.ui.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.router.APP_ANDROID_MENU
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.addArgument
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.lighthouse.presentation.presenter.LightHouseContract
import br.com.mobicare.cielo.lighthouse.presentation.presenter.LightHousePresenter
import br.com.mobicare.cielo.lighthouse.presentation.ui.LightHouseHiredConciliadorFragment
import br.com.mobicare.cielo.lighthouse.presentation.ui.LightHouseHiredFragment
import br.com.mobicare.cielo.lighthouse.presentation.ui.LightHouseToHireDialog
import br.com.mobicare.cielo.main.domain.Menu
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_superlink.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class LightHouseActivityConciliador : BaseActivity(), LightHouseContract.View {


    val lightHousePresenter: LightHousePresenter by inject {
        parametersOf(this)
    }

    private var menu: Menu? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lighthouse_conciliador)
        loadParams()
        lightHousePresenter.callLightHouse()
        Analytics.trackScreenView(this.javaClass)
    }

    private fun loadParams() {
        intent.extras?.let {
            this.menu = it.getParcelable(APP_ANDROID_MENU)
        }
    }

    override fun showLightHouseBannerToRegister() {
        LightHouseToHireDialog.create()
                .show(supportFragmentManager, LightHouseToHireDialog.TAG)
    }

    override fun showLightHouseProductScreen() {
        val fragment = LightHouseHiredConciliadorFragment()
        this.menu?.let {
            fragment.addArgument(APP_ANDROID_MENU, it)
        }
        fragment.addInFrame(supportFragmentManager, R.id.frameLightHouseContentConciliador)
    }

}