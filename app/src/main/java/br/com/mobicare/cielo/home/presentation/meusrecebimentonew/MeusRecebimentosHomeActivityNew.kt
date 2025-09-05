package br.com.mobicare.cielo.home.presentation.meusrecebimentonew

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.MeusRecebimentosFragmentNew
import kotlinx.android.synthetic.main.activity_meus_recebimentos_home.*

class MeusRecebimentosHomeActivityNew : BaseActivity(), BaseActivity.OnBackButtonListener {

    companion object {
        var CURRENT_POSITION = "current_position"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_recebimentos_home)
        init()
    }

    fun init() {
        val ecToolbarSub = UserPreferences.getInstance().userInformation?.activeMerchant?.id ?: ""
        setupToolbar(toolbar_main as Toolbar, resources.getString(R.string.text_values_received_navigation_label),
                toolbarSubtitle = getString(R.string.ec_value_x, ecToolbarSub))

        super.onBackButtonListener = this
        val fragment = MeusRecebimentosFragmentNew.create()

        if (intent != null && intent.extras != null) {
            val position = intent.getIntExtra(CURRENT_POSITION, -1)
            if (position != -1) {
                var bundle = Bundle()
                bundle.putInt(CURRENT_POSITION, position)
                fragment.arguments = bundle
            }
        }
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainframe, fragment)
                .commitAllowingStateLoss()
    }

    override fun onBackTouched() {
        this.finish()
    }
}
